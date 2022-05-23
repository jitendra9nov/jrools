/* (C) 2022 */
package com.jrools.rule.efficacies;

import static com.jrools.rule.constants.RuleConstants.ATTRIBUTE_BEAN_NAME;
import static com.jrools.rule.constants.RuleConstants.ATTRIBUTE_NAME;
import static com.jrools.rule.constants.RuleConstants.CONTAINER_BEAN_NAME;
import static com.jrools.rule.constants.RuleConstants.CONTAINER_NAME;
import static com.jrools.rule.constants.RuleConstants.EMPTY;
import static com.jrools.rule.constants.RuleConstants.ITERATE;
import static com.jrools.rule.efficacies.Assessor.with;
import static com.jrools.rule.enums.AttributeType.ENUM;
import static com.jrools.rule.enums.AttributeType.STRING;
import static com.jrools.rule.enums.Combinator.AND;
import static com.jrools.rule.enums.Combinator.OR;
import static com.jrools.rule.enums.RuleType.REFERENCE;
import static com.jrools.rule.enums.Status.FAILURE;
import static com.jrools.rule.enums.Status.NA;
import static com.jrools.rule.enums.Status.SUCCESS;
import static com.jrools.rule.enums.Status.UNKNOWN;
import static com.jrools.rule.facts.RuleInfo.create;
import static com.jrools.rule.utils.RuleUtil.applyPattern;
import static com.jrools.rule.utils.RuleUtil.enumCode;
import static com.jrools.rule.utils.RuleUtil.filterMapByKey;
import static com.jrools.rule.utils.RuleUtil.findFieldValue;
import static com.jrools.rule.utils.RuleUtil.groupByFieldWithNullKeys;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static java.util.Comparator.reverseOrder;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.hasText;

import com.jrools.rule.enums.RuleType;
import com.jrools.rule.enums.RuleTypeInfo;
import com.jrools.rule.facts.EngineRequest;
import com.jrools.rule.facts.EngineResponse.Builder;
import com.jrools.rule.facts.ExecutionInfo;
import com.jrools.rule.facts.MultiValueKey;
import com.jrools.rule.facts.Rule;
import com.jrools.rule.facts.RuleInfo;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleTask extends RecursiveAction {

	private static final long serialVersionUID = -7149796134261364497L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RuleTask.class);

	private transient EngineRequest<?> engineRequest;
	private transient Builder builder;
	private transient RuleTypeInfo typeInfo;
	private transient List<Rule> ruleSets;
	private transient Map<String, Rule> referenceRules;

	public RuleTask(EngineRequest<?> engineRequest, Builder builder, List<Rule> ruleSets,
			Map<String, Rule> referenceRules) {
		this.engineRequest = engineRequest;
		this.builder = builder;
		this.ruleSets = ruleSets;
		this.referenceRules = referenceRules;
	}

	private RuleTask(EngineRequest<?> engineRequest, Builder builder, List<Rule> ruleSets,
			Map<String, Rule> referenceRules, RuleTypeInfo typeInfo) {
		this.engineRequest = engineRequest;
		this.builder = builder;
		this.ruleSets = ruleSets;
		this.referenceRules = referenceRules;
		this.typeInfo = typeInfo;
	}

	@Override
	protected void compute() {

		boolean isStopOnFirstTypeFailure = TRUE.equals(this.engineRequest.getStopOnFirstRuleFailure());

		final List<RuleTypeInfo> ruleTypes = this.engineRequest.getRuleTypes();
		int ruleListSize = ruleTypes.size();

		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("Rule Type [{}], isStopOnFirstTypeFailure: {}, ruleListSize: {}", ruleTypes,
					isStopOnFirstTypeFailure, ruleListSize);
		}

		if (isStopOnFirstTypeFailure || ruleListSize <= 1) {

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Executing Rule in Sequence because isStopOnFirstTypeFailure: {}, ruleListSize: {}",
						isStopOnFirstTypeFailure, ruleListSize);
			}

			for (RuleTypeInfo ruleTypeInfoLoc : ruleTypes) {
				boolean[] isAnyRuleFailed = { false };

				ruleTypeAction(ruleTypeInfoLoc, isAnyRuleFailed);

				if (isAnyRuleFailed[0] && isStopOnFirstTypeFailure) {
					if (LOGGER.isDebugEnabled()) {

						LOGGER.debug(
								"Stopping further Rule Execution because isAnyRuleFailed: {} , isStopOnFirstTypeFailure: {}",
								isAnyRuleFailed[0], isStopOnFirstTypeFailure);
					}

					break;
				}
			}

		} else if (null != this.typeInfo) {
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Executing Child Task for Rule type:{} in parellel processing.", this.typeInfo);
			}

			boolean[] isAnyRuleFailed = { false };

			ruleTypeAction(this.typeInfo, isAnyRuleFailed);
		} else {

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Executing Main Task which will create child tasks for parellel processing.");
			}

			List<RuleTask> ruleActions = engineRequest.getRuleTypes().stream()
					.map(ruleTypeLoc -> new RuleTask(this.engineRequest, this.builder, this.ruleSets,
							this.referenceRules, ruleTypeLoc))
					.collect(Collectors.toList());

			invokeAll(ruleActions);

		}

	}

	private void ruleTypeAction(RuleTypeInfo ruleTypeInfo, boolean[] isAnyRuleFailed) {

		// Filter Rules on the basis of ruleType

		RuleType ruleType = ruleTypeInfo.getRuleType();
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("Executing Rules for ruleType: {}", ruleType);
		}

		boolean isStopOnFirstRuleFailure = nonNull(ruleTypeInfo.getStopOnFirstRuleFailure())
				? TRUE.equals(ruleTypeInfo.getStopOnFirstRuleFailure())
				: TRUE.equals(this.engineRequest.getStopOnFirstRuleFailure());

		boolean isOnList = TRUE.equals(ruleTypeInfo.getOnList());

		List<Rule> filteredRuleSet = loadSpecificRule(ruleType);

		if (isEmpty(filteredRuleSet)) {
			ExecutionInfo info = new ExecutionInfo();

			info.setRuleStatus(UNKNOWN);
			info.setRuleType(ruleType);
			info.setErrorMessage(format("No Rule defined for Rule Type %s", ruleType));
			this.builder.executionInfo(info);
		} else {
			if (isOnList) {
				isAnyRuleFailed[0] = executeOnList(isStopOnFirstRuleFailure, this.engineRequest.getReferenceKey(),
						this.builder, filteredRuleSet, this.engineRequest.getRequestObjects());
			} else {
				this.engineRequest.getRequestObjects().stream().forEach(object -> {
					boolean isAnyRuleFailedLoc = execute(isStopOnFirstRuleFailure, this.engineRequest.getReferenceKey(),
							this.builder, filteredRuleSet, object);
					if (isAnyRuleFailedLoc) {
						isAnyRuleFailed[0] = isAnyRuleFailedLoc;
						return; // TODO Check if this works or need Break
					}
				});
			}
		}

	}

	private Assessor byGroup(Rule group, Assessor assessor, Builder builder, boolean stopOnFirstRuleFailure,
			Object referenceValue) {

		List<Assessor> validatorList = new ArrayList<>();

		Assessor assessorGroup = assessor.fresh(group.getCombinator());

		Assessor assessorRule = assessorGroup;

		boolean isFirstRule = true;

		if (!isEmpty(group.getRules())) {

			// Iterate over each rule in the list for each grop

			for (Rule rule : group.getRules()) {

				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug("Executing Rules [{}] of Group [{}]", rule.getRuleId(), group.getGroupId());
				}

				if (!isEmpty(rule.getRules())) {

					// This is group

					assessorRule = byGroup(rule, assessorGroup, builder, stopOnFirstRuleFailure, referenceValue);
					validatorList.add(assessorRule);
				} else {
					// This is Rule

					if (isFirstRule) {

						assessorRule = assessorGroup.fresh(group.getCombinator());
					}
					isFirstRule = false;

					// THis is for rulestopOnFirstRuleFailure is true and rule failed

					byRule(group, builder, assessorRule, rule, stopOnFirstRuleFailure, referenceValue);
				}

				// Stop further rule validation if

				if (!assessorRule.found() && stopOnFirstRuleFailure) {
					break;
				}

			}
		}

		validatorList.add(assessorRule);

		applyCombinator(group, validatorList, assessorGroup);

		return assessorGroup.negate(group.getNegator());

	}

	private static void applyCombinator(Rule group, List<Assessor> validatorList, Assessor assessorGroup) {

		Assessor[] ruleValidatorArray = new Assessor[validatorList.size()];

		ruleValidatorArray = validatorList.toArray(ruleValidatorArray);

		if (AND == group.getCombinator()) {
			assessorGroup.and(ruleValidatorArray);
		} else if (OR == group.getCombinator()) {
			assessorGroup.or(ruleValidatorArray);
		}

		if (REFERENCE == group.getRuleType()) {
			assessorGroup.setRefValue(validatorList.stream().filter(ass -> null != ass.getRefValue()).findFirst()
					.map(Assessor::getRefValue).orElse(null));
		}

	}

	private void byRule(Rule group, Builder builder, Assessor assessorRule, Rule rule, boolean stopOnFirstRuleFailure,
			Object referenceValue) {

		if (hasLength(rule.getAttribute())) {
			attributeLevel(group, builder, assessorRule, rule, stopOnFirstRuleFailure, referenceValue);
		} else {
			businessEntity(group, builder, assessorRule, rule, stopOnFirstRuleFailure, referenceValue);
		}
	}

	private void businessEntity(Rule group, Builder builder, Assessor assessorRule, Rule rule,
			boolean stopOnFirstRuleFailure, Object referenceValue) {

		Rule referenceRuleGrp = referenceRules.get(rule.getReferenceRule());

		Assessor assessorBusinessEntity = null;

		if (null != rule.getBusinessAttribute()) {
			RuleInfo ruleInfo = create(group, rule, referenceValue, builder);

			Object businessObject = findFieldValue(ruleInfo, TRUE.equals(rule.getJsonPath()),
					assessorRule.getTargerObject());

			if (businessObject instanceof List) {

				Assessor assessorDefault = assessorRule.fresh(referenceRuleGrp.getCombinator(), null);

				List<Assessor> collectedRules = ((List<?>) businessObject).stream()
						.map(bsnObject -> byGroup(referenceRuleGrp,
								assessorRule.fresh(referenceRuleGrp.getCombinator(), bsnObject), builder,
								stopOnFirstRuleFailure, referenceValue))
						.collect(toList());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("collectedRules List size {} ", collectedRules.size());
				}

				Optional<Assessor> findFirst = collectedRules.stream().filter(Assessor::notFound).findFirst();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("BusinessAttribute MATCH FOUND: {}", findFirst.isPresent());
				}

				assessorBusinessEntity = findFirst.orElse(assessorDefault);

			} else {
				assessorBusinessEntity = byGroup(referenceRuleGrp,
						assessorRule.fresh(referenceRuleGrp.getCombinator(), businessObject), builder,
						stopOnFirstRuleFailure, referenceValue);

			}

		} else {

			assessorBusinessEntity = byGroup(referenceRuleGrp, assessorRule.fresh(referenceRuleGrp.getCombinator()),
					builder, stopOnFirstRuleFailure, referenceValue);

		}

		boolean isFailed = null == assessorBusinessEntity || !assessorBusinessEntity.found();

		populateExecutionInfo(isFailed, builder, group, rule, null, null, referenceValue);

		if (AND == group.getCombinator()) {
			assessorRule.and(assessorBusinessEntity);
		} else if (OR == group.getCombinator()) {
			assessorRule.or(assessorBusinessEntity);
		}

	}

	private void attributeLevel(Rule group, Builder builder, Assessor assessorRule, Rule rule,
			boolean stopOnFirstRuleFailure, Object referenceValue) {

		// Evaluate Single Rule based on Combinator for matcher

		RuleInfo ruleInfo = create(group, rule, referenceValue, builder);

		// For a Rule of Diff attribute

		if (hasText(rule.getValueReference())) {

			Rule referenceRuleGrp = referenceRules.get(rule.getValueReference());

			Assessor assessorBusinessEntity = byGroup(referenceRuleGrp,
					assessorRule.fresh(referenceRuleGrp.getCombinator()), builder, stopOnFirstRuleFailure,
					referenceValue);

			if (null != assessorBusinessEntity.getRefValue()) {
				ruleInfo.setRefRuleValue(ENUM == referenceRuleGrp.getAttributeType()
						? enumCode(assessorBusinessEntity.getRefValue()).toString()
						: assessorBusinessEntity.getRefValue().toString());

				assessorBusinessEntity.setRefValue(null);
			}

			ruleInfo.setRefRuleValueSuccess(assessorBusinessEntity.found());

		}

		if (AND == group.getCombinator()) {
			assessorRule.and(ruleInfo);
		} else if (OR == group.getCombinator()) {
			assessorRule.or(ruleInfo);
		}

	}

	private boolean execute(boolean isStopOnFirstRuleFailure, String referenceKey, Builder builder,
			List<Rule> filteredRuleSet, Object object) {

		boolean isAnyRuleFailed = false;

		if (!isEmpty(filteredRuleSet)) {

			Assessor assessor = with(object);

			Object referenceValue = assessor.findFieldValue(referenceKey, false);

			// Iterate over each Rule Group in the list and evaluate each group one by one

			for (Rule group : filteredRuleSet) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Executing Group [{}] ", group.getGroupId());
				}

				// call to evaluate single group and collect the evaluation result

				boolean isFound = byGroup(group, assessor, builder, isStopOnFirstRuleFailure, referenceValue).found();

				// set variable true if rule failes

				isAnyRuleFailed = isAnyRuleFailed(isAnyRuleFailed, isFound);

				// stop further rule validation if isStopOnFirstRuleFailure is true and rule
				// failed

				if (!isFound && isStopOnFirstRuleFailure) {
					break;
				}

			}

		}

		return isAnyRuleFailed;
	}

	private boolean executeOnList(boolean isStopOnFirstRuleFailure, String referenceKey, Builder builder,
			List<Rule> filteredRuleSet, List<?> requestObjects) {

		boolean isAnyRuleFailed = false;

		if (!isEmpty(filteredRuleSet)) {

			for (Rule group : filteredRuleSet) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Executing Group [{}] ", group.getGroupId());
				}

				boolean compatible = executeRuleOnList(isStopOnFirstRuleFailure, group, builder, requestObjects,
						referenceKey);

				// set variable true if rule failes

				isAnyRuleFailed = isAnyRuleFailed(isAnyRuleFailed, compatible);

				// stop further rule validation if isStopOnFirstRuleFailure is true and rule
				// failed

				if (!compatible && isStopOnFirstRuleFailure) {
					break;
				}

			}

		}

		return isAnyRuleFailed;

	}

	private static boolean isAnyRuleFailed(boolean isAnyRuleFailed, boolean isFound) {

		if (!isAnyRuleFailed && !isFound) {
			isAnyRuleFailed = true;
		}
		return isAnyRuleFailed;
	}

	private boolean executeRuleOnList(boolean isStopOnFirstRuleFailure, Rule group, Builder builder2,
			List<?> requestObjects, String referenceKey) {
		boolean isMatched = true;

		for (Rule rule : group.getRules()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Executing Rule: [{}] of Group [{}] ", rule.getRuleId(), group.getGroupId());
			}

			// we are grouping the list of quote by rule attribute and then collecting all
			// groupedd ref ids

			boolean ignorecaseForString = (STRING == rule.getAttributeType() && nonNull(rule.getIgnorecase()))
					? TRUE.equals(rule.getIgnorecase())
					: TRUE.equals(group.getIgnorecase());

			boolean negate = TRUE.equals(group.getNegator());

			Map<String, String> attributeMap = new HashMap<>();
			attributeMap.put(ATTRIBUTE_NAME, rule.getAttribute());
			attributeMap.put(ATTRIBUTE_BEAN_NAME, rule.getAttributeBeanName());
			attributeMap.put(CONTAINER_BEAN_NAME, rule.getContainerBeanName());
			attributeMap.put(CONTAINER_NAME, rule.getContainerName());
			attributeMap.put(ITERATE, rule.getIterate().val());

			Map<String, String> collectorMap = new HashMap<>();
			collectorMap.put(ATTRIBUTE_NAME, referenceKey);

			Map<Object, List<Object>> groupedMap = groupByFieldWithNullKeys(requestObjects, ignorecaseForString,
					attributeMap, collectorMap);

			// if above call returns only 1 group, which means all rules are satiesfied and
			// then check negate to determine if passed rules are compatible or not

			isMatched = (groupedMap.size() == 1);

			isMatched = applyMatcherAndSkipNull(group, rule, builder, isMatched, negate, groupedMap,
					(!isEmpty(requestObjects) && requestObjects.size() == 1));

			// stop further rule validation if isStopOnFirstRuleFailure is true and rule
			// failed

			if (!isMatched && isStopOnFirstRuleFailure) {
				break;
			}

		}

		return isMatched;
	}

	private boolean applyMatcherAndSkipNull(Rule group, Rule rule, Builder builder, boolean isMatched, boolean negate,
			Map<Object, List<Object>> groupedMap, boolean isSingleObject) {

		boolean isAuxiliary = rule.isAuxiliary() ? rule.isAuxiliary() : group.isAuxiliary();

		// if above call returns only one group and all have values as null, then skip
		// this only if value is true to skip

		if (isAuxiliary) {
			isMatched = true;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Auxiliary attribute found for path [{}], value [{}]", rule.getAttribute(), groupedMap);
			}
		} else if (isMatched && groupedMap.containsKey(null)) {

			boolean skipIfNull = nonNull(rule.getSkipIfNull()) ? TRUE.equals(rule.getSkipIfNull())
					: TRUE.equals(group.getSkipIfNull());
			if (skipIfNull || isSingleObject) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Skipping check as Null value found for path [{}], value [{}]m Single Object [{}]",
							rule.getAttribute(), groupedMap, isSingleObject);
				}
			} else {
				isMatched = true;
			}

		}

		// if above call returns only 1 group then filter out as per pattern defined in
		// rule

		String errorMessage = ";";
		if (isMatched && null != rule.getRuleMatcher() && null != rule.getRuleMatcher().getMatcher()) {

			Map<Object, List<Object>> filteredMap = filterMapByKey(groupedMap, rule);

			isMatched = groupedMap.size() == filteredMap.size();

			errorMessage = formatError(isMatched, rule, groupedMap, errorMessage, filteredMap);

		}

		boolean isFailure = !(!negate && isMatched);

		setExecutionInfo(isFailure, builder, group, rule, groupedMap, errorMessage);

		return isMatched;
	}

	private static String formatError(boolean isMatched, Rule rule, Map<Object, List<Object>> groupedMap,
			String errorMessage, Map<Object, List<Object>> filteredMap) {

		if (!isMatched) {

			errorMessage = format(" and Condition [%s] failed",
					(null != rule.getRuleMatcher() ? rule.getRuleMatcher().getMatcher().toString().replace("\"", "'")
							: EMPTY));

			groupedMap.entrySet().removeAll(filteredMap.keySet());

		}
		return errorMessage;
	}

	private void setExecutionInfo(boolean isFailure, Builder builder, Rule group, Rule rule,
			Map<Object, List<Object>> groupedMap, String errorMessage) {

		Map<Object, Object> flatMapOfReference = groupedMap.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(v -> new SimpleEntry<>(entry.getKey(), v)))
				.collect(HashMap::new, (m, v) -> m.put(v.getValue(), v.getKey()), HashMap::putAll);

		flatMapOfReference.entrySet().forEach(
				flatEntry -> populateExecutionInfo(isFailure, builder, group, rule, errorMessage, flatEntry, null));

	}

	private void populateExecutionInfo(boolean isFailed, Builder builder, Rule group, Rule rule, String errorMessage,
			Entry<Object, Object> flatEntry, Object referenceValue) {

		if (flatEntry != null && flatEntry.getValue() instanceof MultiValueKey) {
			multiValueList(isFailed, builder, group, rule, errorMessage, flatEntry);
		} else {
			singleValue(isFailed, builder, group, rule, errorMessage, flatEntry, referenceValue);
		}

	}

	private void singleValue(boolean isFailed, Builder builder, Rule group, Rule rule, String errorMessage,
			Entry<Object, Object> flatEntry, Object referenceValue) {

		ExecutionInfo info = new ExecutionInfo();

		info.setAttribute(rule.getAttribute());
		info.setAttributeLabel(rule.getAttributeLabel());
		info.setOrder(rule.getExecutionOrder());
		info.setBusinessEntity(rule.getBusinessEntity());
		info.setReferenceRule(rule.getReferenceRule());
		info.setRuleId(rule.getRuleId());
		info.setRuleName(rule.getRuleName());
		info.setAttributeType(rule.getAttributeType());

		if (null != flatEntry) {
			info.setValue(flatEntry.getValue());
			info.setReferenceValue(flatEntry.getKey());

		} else {
			info.setReferenceValue(referenceValue);
		}

		info.setPattern(rule.getPattern());
		applyPattern(info);

		info.setAttributeStatus(!isFailed ? SUCCESS : FAILURE);

		info.setAddOns(group.getAddOns());
		info.setCategory(group.getCategory());
		info.setRuleType(group.getRuleType());
		info.setGroupId(group.getGroupId());
		info.setGroupName(group.getGroupName());
		info.setGroupVerbiage(group.getGroupVerbiage());
		info.setRuleStatus(!isFailed ? SUCCESS : FAILURE);
		info.setGroupStatus(!isFailed ? SUCCESS : FAILURE);

		auxiliaryRule(group, rule, info);

		if (isFailed) {

			info.setFailureText(rule.getFailureText());
			if (null != flatEntry) {
				info.setErrorMessage(format("Data Mismatch found for Rule %s %s", rule.getRuleId(), errorMessage));
			} else {
				info.setErrorMessage(format("Rile %s condition [%s] failed", rule.getRuleId(),
						(null != rule.getRuleMatcher()
								? rule.getRuleMatcher().getMatcher().toString().replace("\"", "'")
								: rule.getRuleName())));
			}
		}
		builder.executionInfo(info);

	}

	private static void multiValueList(boolean isFailed, Builder builder, Rule group, Rule rule, String errorMessage,
			Entry<Object, Object> flatEntry) {
		AtomicInteger order = new AtomicInteger(rule.getExecutionOrder());
		(asList(((MultiValueKey) flatEntry.getValue()).getValues())).forEach(action -> {

			ExecutionInfo info = new ExecutionInfo();

			info.setAttribute(rule.getAttribute());
			info.setAttributeLabel(rule.getAttributeLabel());
			info.setOrder(order.getAndAdd(100));
			info.setBusinessEntity(rule.getBusinessEntity());
			info.setReferenceRule(rule.getReferenceRule());
			info.setRuleId(rule.getRuleId());
			info.setRuleName(rule.getRuleName());
			info.setAttributeType(rule.getAttributeType());

			info.setValue(null != action ? action : flatEntry.getValue());
			info.setReferenceValue(flatEntry.getKey());

			info.setPattern(rule.getPattern());
			applyPattern(info);

			info.setAttributeStatus(!isFailed ? SUCCESS : FAILURE);

			info.setAddOns(group.getAddOns());
			info.setCategory(group.getCategory());
			info.setRuleType(group.getRuleType());
			info.setGroupId(group.getGroupId());
			info.setGroupName(group.getGroupName());
			info.setGroupVerbiage(group.getGroupVerbiage());
			info.setRuleStatus(!isFailed ? SUCCESS : FAILURE);
			info.setGroupStatus(!isFailed ? SUCCESS : FAILURE);

			auxiliaryRule(group, rule, info);

			if (isFailed) {

				info.setFailureText(rule.getFailureText());
				info.setErrorMessage(format("Data Mismatch found for Rule %s %s", rule.getRuleId(), errorMessage));

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Rule Failed for Rule[{}], Attrubute [{}]", rule.getRuleId(), rule.getAttribute());
				}

			}
			builder.executionInfo(info);
		});

	}

	private static void auxiliaryRule(Rule group, Rule rule, ExecutionInfo info) {
		boolean isAuxiliary = rule.isAuxiliary() ? rule.isAuxiliary() : group.isAuxiliary();
		if (isAuxiliary) {
			info.setAttributeStatus(NA);
			info.setRuleStatus(NA);
			info.setGroupStatus(NA);
		}
	}

	private List<Rule> loadSpecificRule(RuleType ruleType) {

		notNull(ruleType, "ruleType can't be null");

		List<Rule> ruleSetLocal = null;

		if (!isEmpty(this.ruleSets)) {

			Comparator<Rule> ruleComparator = comparing(Rule::getCategory, nullsLast(naturalOrder()))
					.thenComparing(comparing(Rule::getPriority, nullsLast(reverseOrder())));
			
			ruleSetLocal=this.ruleSets.stream().filter(rule -> ((ruleType ==rule.getRuleType()) && !isEmpty(rule.getRules()))).collect(toList());
			ruleSetLocal=ruleSetLocal.stream().sorted(ruleComparator).collect(toList());
			
		}
		return ruleSetLocal;
	}

}
