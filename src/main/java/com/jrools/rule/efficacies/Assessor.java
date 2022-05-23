/* (C) 2022 */
package com.jrools.rule.efficacies;

import static com.jayway.jsonpath.Configuration.defaultConfiguration;
import static com.jayway.jsonpath.JsonPath.parse;
import static com.jrools.rule.constants.RuleConstants.ATTRIBUTE_BEAN_NAME;
import static com.jrools.rule.constants.RuleConstants.ATTRIBUTE_NAME;
import static com.jrools.rule.constants.RuleConstants.CONTAINER_BEAN_NAME;
import static com.jrools.rule.constants.RuleConstants.CONTAINER_NAME;
import static com.jrools.rule.constants.RuleConstants.ITERATE;
import static com.jrools.rule.enums.AttributeType.COLLECTION;
import static com.jrools.rule.enums.AttributeType.ENUM;
import static com.jrools.rule.enums.AttributeType.OBJECT;
import static com.jrools.rule.enums.Combinator.AND;
import static com.jrools.rule.enums.RuleType.REFERENCE;
import static com.jrools.rule.enums.Status.FAILURE;
import static com.jrools.rule.enums.Status.NA;
import static com.jrools.rule.enums.Status.SUCCESS;
import static com.jrools.rule.utils.EntityUtil.findFromGeneric;
import static com.jrools.rule.utils.RuleUtil.applyPattern;
import static com.jrools.rule.utils.RuleUtil.enumCode;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jrools.rule.enums.Combinator;
import com.jrools.rule.facts.ExecutionInfo;
import com.jrools.rule.facts.Rule;
import com.jrools.rule.facts.RuleInfo;
import com.jrools.rule.facts.RuleMatcher;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Assessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(Assessor.class);

	private final Object targerObject;

	private Boolean isFound;

	private Object refValue;

	private Assessor(Object obj) {
		this.targerObject = obj;
	}

	// BELOW METHODS ARE TO CREATE OBJECT USING JSON STRING/ FILE/ READER

	private static String convertReaderToString(Reader reader) throws IOException {

		if (null != reader) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);

				}
			} finally {
				reader.close();
			}

			return writer.toString();
		}
		return "";
	}

	public static Assessor with(InputStream is) throws IOException {
		Reader reader = new InputStreamReader(is);
		return with(reader);
	}

	private static Assessor with(Reader reader) throws IOException {
		return with(convertReaderToString(reader));
	}

	// BELOW METHODS ARE TO CREATE OBJECT USING ANOTHER OBJECT

	public static Assessor with(Object obj) {
		return new Assessor(obj);
	}

	public static Assessor with(String json) {
		return new Assessor(parse(json));
	}

	private boolean found(boolean found) {
		this.isFound = TRUE.equals(found);
		return this.isFound;

	}

	public boolean found() {
		return TRUE.equals(this.isFound);
	}

	public boolean notFound() {
		return !TRUE.equals(this.isFound);
	}

	/**
	 * @return the targerObject
	 */
	public Object getTargerObject() {
		return targerObject;
	}

	/**
	 * @return the refValue
	 */
	public Object getRefValue() {
		return refValue;
	}

	/**
	 * @param refValue the refValue to set
	 */
	public void setRefValue(Object refValue) {
		this.refValue = refValue;
	}

	private Assessor found(Combinator combinator) {
		this.isFound = (AND == combinator);
		return this;
	}

	private Assessor fresh() {
		return new Assessor(this.targerObject);
	}

	private static Assessor fresh(Object businessObject) {
		return new Assessor(businessObject);
	}

	public Assessor fresh(Combinator combinator) {
		return fresh().found(combinator);
	}

	public Assessor fresh(Combinator combinator, Object businessObject) {
		return fresh(businessObject).found(combinator);
	}

	public Assessor negate(Boolean value) {
		if (TRUE.equals(value)) {
			this.found(!this.found());
		}
		return this;
	}

	public Assessor notDefined(String path) {
		try {
			Configuration c = defaultConfiguration();

			JsonPath.using(c).parse(this.targerObject).read(path);
			LOGGER.debug("Document contains ths path [{}] but was expected not to", path);
		} catch (PathNotFoundException e) {

			LOGGER.warn("Document path [{}] not defimed {}", path, e);
		}
		return this;
	}

	// FROM HERE ALL MATCHER METHODS ARE STARTING AND THOSE ARE USED FOR MATCHING A
	// CONDITION DEPENDING ON THE MATCHER OBJECT DEFINED IN RULE

	public Assessor and(Assessor... more) {
		this.found(this.found() && asList(more).stream().noneMatch(Assessor::notFound));
		return this;
	}

	public Assessor and(RuleInfo ruleInfo) {
		boolean isFoundLocal = this.found();
		this.the(ruleInfo);
		this.found(isFoundLocal && this.found());
		return this;
	}

	public Assessor or(Assessor... more) {
		this.found(this.found() || asList(more).stream().anyMatch(Assessor::found));
		return this;
	}

	public Assessor or(RuleInfo ruleInfo) {
		boolean isFoundLocal = this.found();
		this.the(ruleInfo);
		this.found(isFoundLocal || this.found());
		return this;
	}

	private void setExecutionInfo(RuleInfo ruleInfo, Object value, Integer order) {

		Rule rule = ruleInfo.getRule();
		Rule group = ruleInfo.getGroup();

		boolean isFailed = !this.found();

		ExecutionInfo info = new ExecutionInfo();
		info.setAttribute(rule.getAttribute());
		info.setAttributeLabel(rule.getAttributeLabel());
		info.setOrder(null != order ? order : rule.getExecutionOrder());
		info.setBusinessEntity(rule.getBusinessEntity());
		info.setReferenceRule(rule.getReferenceRule());
		info.setRuleId(rule.getRuleId());
		info.setRuleName(rule.getRuleName());
		info.setAttributeType(rule.getAttributeType());
		info.setValue(null != value ? value : ruleInfo.getAttributeValue());
		info.setPattern(rule.getPattern());
		applyPattern(info);

		info.setReferenceValue(ruleInfo.getReferenceValue());
		info.setAttributeStatus(!isFailed ? SUCCESS : FAILURE);

		info.setAddOns(group.getAddOns());
		info.setCategory(group.getCategory());
		info.setRuleType(group.getRuleType());
		info.setGroupId(group.getGroupId());
		info.setGroupName(group.getGroupName());
		info.setGroupVerbiage(group.getGroupVerbiage());
		info.setRuleStatus(!isFailed ? SUCCESS : FAILURE);
		info.setGroupStatus(!isFailed ? SUCCESS : FAILURE);

		boolean isAuxiliary = rule.isAuxiliary() ? rule.isAuxiliary() : group.isAuxiliary();

		if (isAuxiliary) {
			info.setAttributeStatus(NA);
			info.setRuleStatus(NA);
			info.setGroupStatus(NA);
		}

		if (isFailed) {
			RuleMatcher ruleMatcher = (null != ruleInfo.getRefRuleMatcher()) ? ruleInfo.getRefRuleMatcher()
					: rule.getRuleMatcher();

			info.setFailureText(rule.getFailureText());
			info.setErrorMessage(format("Rile %s condition [%s] failed", rule.getRuleId(),
					(null != ruleMatcher ? ruleMatcher.getMatcher().toString().replace("\"", "'") : rule.getRuleName())));
		}
		ruleInfo.getBuilder().executionInfo(info);

	}

	private Assessor the(RuleInfo ruleInfo) {

		Rule rule = ruleInfo.getRule();
		Rule group = ruleInfo.getGroup();

		Object obj = findFieldValue(ruleInfo, TRUE.equals(rule.getJsonPath()));

		ruleInfo.setAttributeValue(obj);

		boolean skipIfNull = nonNull(rule.getSkipIfNull()) ? TRUE.equals(rule.getSkipIfNull())
				: TRUE.equals(group.getSkipIfNull());

		boolean isAuxiliary = rule.isAuxiliary() ? rule.isAuxiliary() : group.isAuxiliary();

		if (obj instanceof List && !(OBJECT == rule.getAttributeType() || COLLECTION == rule.getAttributeType())) {
			AtomicInteger order = new AtomicInteger(rule.getExecutionOrder());

			((List<?>) obj)
					.forEach(action -> validateRule(ruleInfo, action, skipIfNull, isAuxiliary, order.getAndAdd(100)));

		} else {
			validateRule(ruleInfo, obj, skipIfNull, isAuxiliary, null);
		}

		return this;

	}

	private Assessor validateRule(RuleInfo ruleInfo, Object obj, boolean skipIfNull, boolean isAuxiliary,
			Integer order) {
		Object localObj = obj;

		Rule rule = ruleInfo.getRule();
		if (ENUM == rule.getAttributeType()) {
			localObj = enumCode(obj);
		}

		if (isAuxiliary) {
			this.found(true);
			LOGGER.debug("Auxiliary attribute found for path [{}], value [{}]", rule.getAttribute(), localObj);
		} else if (isNull(localObj) && skipIfNull) {
			this.found(true);
			LOGGER.debug("Skipping check as Null value found for path [{}]", rule.getAttribute());

		} else {

			RuleMatcher ruleMatcher = rule.getRuleMatcher();
			if (null != ruleInfo.getRefRuleMatcher()) {
				LOGGER.debug("Rule matcher Updated for Value.. [{}]", ruleInfo.getRefRuleValue());

				ruleMatcher = new RuleMatcher(rule.getOperator(), ruleInfo.getRefRuleValue(), rule.getAttributeType(),
						rule.getNumericType());

				ruleInfo.setRefRuleMatcher(ruleMatcher);
			}

			if (null != ruleMatcher && !this.found(ruleMatcher.getMatcher().matches(localObj))) {
				LOGGER.debug("Matcher Error: \n Expected:\n {} \n Actual: \n {}", ruleMatcher.getMatcher(), localObj);
			} else {
				LOGGER.debug("Matcher Success: \n Expected:\n {} \n Actual: \n {}",
						(null != ruleMatcher ? ruleMatcher.getMatcher() : ""), localObj);
			}

		}
		
		//This is only for reference rule
		
		if(REFERENCE ==ruleInfo.getGroup().getRuleType()) {
			this.setRefValue(localObj);
		}
		
		//Set Failure Info if rule fail
		setExecutionInfo(ruleInfo, localObj, order);
		

		return this;
	}

	private Object findFieldValue(RuleInfo ruleInfo, boolean isJsonPath) {
		Object obj = null;

		String attribute = ruleInfo.getRule().getAttribute();

		try {

			if (isJsonPath) {
				obj = JsonPath.read(this.targerObject, attribute);
			} else {

				Map<String, String> attributeMap = new HashMap<>();
				attributeMap.put(ATTRIBUTE_NAME, attribute);
				attributeMap.put(ATTRIBUTE_BEAN_NAME, ruleInfo.getRule().getAttributeBeanName());
				attributeMap.put(CONTAINER_BEAN_NAME, ruleInfo.getRule().getContainerBeanName());
				attributeMap.put(CONTAINER_NAME, ruleInfo.getRule().getContainerName());
				attributeMap.put(ITERATE, ruleInfo.getRule().getIterate().val());

				obj = findFromGeneric(this.targerObject, new HashSet<>(),
						this.targerObject.getClass().getGenericSuperclass(), attributeMap);
			}
			LOGGER.debug("attribute path [{}], value [{}]", attribute, obj);

		} catch (Exception e) {

			LOGGER.warn("Error Reading attribute path [{}], value [{}], Error :: {}", attribute, obj, e.getMessage());
		}

		return obj;
	}

	public Object findFieldValue(String attribute, boolean isJsonPath) {
		Object obj = null;

		try {

			if (isJsonPath) {
				obj = JsonPath.read(this.targerObject, attribute);
			} else {

				Map<String, String> attributeMap = new HashMap<>();
				attributeMap.put(ATTRIBUTE_NAME, attribute);

				obj = findFromGeneric(this.targerObject, new HashSet<>(),
						this.targerObject.getClass().getGenericSuperclass(), attributeMap);
			}
			LOGGER.debug("attribute path [{}], value [{}]", attribute, obj);

		} catch (Exception e) {

			LOGGER.warn("Error Reading attribute path [{}], value [{}], Error :: {}", attribute, obj, e.getMessage());
		}

		return obj;
	}

}
