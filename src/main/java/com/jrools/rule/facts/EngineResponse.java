/* (C) 2022 */
package com.jrools.rule.facts;

import static com.jrools.rule.enums.Status.FAILURE;
import static com.jrools.rule.facts.EngineResponse.FieldNames.RULE_TYPE;
import static com.jrools.rule.utils.RuleUtil.groupByFieldWithNullKeys;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jrools.rule.constants.RuleConstants;
import com.jrools.rule.utils.EntityUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jitendrabhadouriya
 * 
 *         <pre>
 * EngineResponse.builder().build().stream()
				.filter(Filter.by(FieldNames.RULE_STATUS, Status.FAILURE).and(FieldNames.REFERENCE_VALUE, 233344))

				.sort(Comparator.comparing(ExecutionInfo::getCategory, Comparator.nullsLast(Comparator.naturalOrder()))
						.thenComparing(Comparator.comparing(ExecutionInfo::getRuleType,
								Comparator.nullsLast(Comparator.reverseOrder()))))
				.build().groupBy(FieldNames.REFERENCE, false);

 *         </pre>
 * 
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EngineResponse {
	@JsonIgnore
	private static final Logger LOGGER = LoggerFactory.getLogger(EngineResponse.class);

	@JsonIgnore
	private List<ExecutionInfo> rawResult;

	@JsonProperty("result")
	private List<ExecutionInfo> result;

	@JsonProperty("groupedResult")
	private Map<Object, List<ExecutionInfo>> groupedResult;

	@JsonCreator
	public EngineResponse(@JsonProperty("result") List<ExecutionInfo> result,

			@JsonProperty("groupedResult") Map<Object, List<ExecutionInfo>> groupedResult) {
		this.result = result;
		this.rawResult = !isEmpty(this.result) ? unmodifiableList(this.result) : null;
		this.groupedResult = groupedResult;
		groupBy(RULE_TYPE, true);

	}

	public EngineResponse(Builder builder) {
		requireNonNull(builder, "builder can' be null");
		this.result = builder.executionInfo;
		this.rawResult = !isEmpty(builder.executionInfo) ? unmodifiableList(builder.executionInfo) : null;
		groupBy(RULE_TYPE, true);

	}

	public static Builder builder() {
		
		return new Builder();
	}

	public Builder stream() {
		return new Builder(this);
	}

	public static EngineResponse merge(EngineResponse engineResponseFirst, EngineResponse engineResponseSecond,
			boolean discardChanges) {
		requireNonNull(engineResponseFirst, "engineResponseFirst can' be null");
		requireNonNull(engineResponseSecond, "engineResponseSecond can' be null");

		return new Builder()
				.executionInfos(discardChanges ? engineResponseFirst.discard().result : engineResponseFirst.result)
				.executionInfos(discardChanges ? engineResponseSecond.discard().result : engineResponseSecond.result)
				.build();

	}

	public EngineResponse discard() {
		this.result = !isEmpty(this.rawResult) ? new ArrayList<>(this.rawResult) : null;
		return groupBy(RULE_TYPE, false);

	}

	public final EngineResponse groupBy(String fieldName, boolean ignoreCaseForString) {
		if (null != this.result) {
			this.groupedResult = groupByFieldWithNullKeys(this.result, fieldName, ignoreCaseForString);

		}
		return this;
	}

	public final EngineResponse groupBy(FieldNames fieldName, boolean ignoreCaseForString) {

		return groupBy(fieldName.val(), ignoreCaseForString && fieldName.ignoreCase());
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Builder {

		@JsonIgnore
		private List<ExecutionInfo> executionInfo;

		@JsonIgnore
		private EngineResponse engineResponse;

		@JsonIgnore
		private Filter filter;

		@JsonIgnore
		private Comparator<ExecutionInfo> sortBy;

		public Builder(EngineResponse engineResponse) {
			requireNonNull(engineResponse, "engineResponse can' be null");
			this.engineResponse = engineResponse;
			this.executionInfo = engineResponse.result;
		}

		public Builder() {
		}

		public Builder executionInfo(ExecutionInfo info) {
			if (null == this.engineResponse && null != info) {
				if (isEmpty(this.executionInfo)) {
					this.executionInfo = new ArrayList<>(5);
				}
				this.executionInfo.add(info);
			}

			return this;
		}

		private final Builder executionInfos(List<ExecutionInfo> infos) {

			if (!isEmpty(infos)) {
				if (isEmpty(this.executionInfo)) {
					this.executionInfo = new ArrayList<>(5);
				}
				this.executionInfo.addAll(infos);
			}

			return this;
		}

		public final Builder filter(Filter filter) {
			requireNonNull(filter, "filter can' be null");

			this.filter = filter;
			return this;
		}

		public final Builder sort(Comparator<ExecutionInfo> sortBy) {
			requireNonNull(sortBy, "sortBy can' be null");

			this.sortBy = sortBy;
			return this;
		}

		public EngineResponse build() {
			EngineResponse engineResponseLoc = this.engineResponse;

			if (null == engineResponseLoc) {
				formulateResponse(this);
				engineResponseLoc = new EngineResponse(this);

			}

			modulate(engineResponseLoc);

			return engineResponseLoc;
		}

		private void modulate(EngineResponse engineResponseLoc) {

			if (!isEmpty(engineResponseLoc.result)) {

				Stream<ExecutionInfo> stream = engineResponseLoc.result.stream();

				if (null != this.filter) {
					stream = stream.filter(this.filter.apply());
				}

				if (null != this.sortBy) {
					stream = stream.sorted(this.sortBy);
				}
				engineResponseLoc.result = stream.collect(toList());

			}

			if (null != engineResponseLoc.rawResult && null != this.sortBy) {
				engineResponseLoc.rawResult = engineResponseLoc.rawResult.stream().sorted(this.sortBy)
						.collect(collectingAndThen(toList(), Collections::unmodifiableList));
			}
			engineResponseLoc.groupBy(RULE_TYPE, false);

			if (!isEmpty(engineResponseLoc.groupedResult) && null != this.sortBy) {
				engineResponseLoc.groupedResult.values().forEach(value -> value.sort(this.sortBy));
			}

		}

		private static void formulateResponse(Builder builder) {

			if (!isEmpty(builder.executionInfo)) {

				List<ExecutionInfo> collect = builder.executionInfo.stream()
						.filter(info -> (info.getAttributeStatus() == FAILURE)).collect(toList());
				builder.executionInfo.stream().forEach(info -> populateFailureStatus(collect, info));

			}

		}

		private static void populateFailureStatus(List<ExecutionInfo> collect, ExecutionInfo info) {

			if (!isEmpty(collect)) {
				collect.forEach(failedInfo -> {
					if ((failedInfo.getReferenceValue() == info.getReferenceValue())
							|| (failedInfo.getReferenceValue().equals(info.getReferenceValue()))
									&& failedInfo.getRuleType() == info.getRuleType()) {
						info.setRuleStatus(FAILURE);

						if (failedInfo.getGroupId().equalsIgnoreCase(info.getGroupId())) {
							info.setGroupStatus(FAILURE);
						}
					}

				});
			}
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Filter {

		@JsonIgnore
		private Predicate<ExecutionInfo> predicate = null;

		private Filter(Predicate<ExecutionInfo> predicate) {
			this.predicate = predicate;
		}

		private Predicate<ExecutionInfo> apply() {
			return this.predicate;
		}

		public static Filter by(String fieldName, Object value) {

			return new Filter(byFieldName(fieldName, value));
		}

		public static Filter by(FieldNames fieldName, Object value) {

			return by(fieldName.val(), value);
		}

		public Filter and(String fieldName, Object value) {

			if (null != this.predicate) {
				this.predicate = this.predicate.and(byFieldName(fieldName, value));
			}
			return this;
		}

		public Filter and(FieldNames fieldName, Object value) {

			return and(fieldName.val(), value);
		}

		public Filter or(String fieldName, Object value) {

			if (null != this.predicate) {
				this.predicate = this.predicate.or(byFieldName(fieldName, value));
			}
			return this;
		}

		public Filter or(FieldNames fieldName, Object value) {
			return or(fieldName.val(), value);
		}

		public Filter andNot(String fieldName, Object value) {

			if (null != this.predicate) {
				this.predicate = this.predicate.and(byFieldName(fieldName, value).negate());
			}
			return this;
		}

		public Filter andNot(FieldNames fieldName, Object value) {

			return andNot(fieldName.val(), value);
		}

		public Filter orNot(String fieldName, Object value) {

			if (null != this.predicate) {
				this.predicate = this.predicate.or(byFieldName(fieldName, value).negate());
			}
			return this;
		}

		public Filter orNot(FieldNames fieldName, Object value) {

			return andNot(fieldName.val(), value);
		}

		public Filter negate() {

			if (null != this.predicate) {
				this.predicate = this.predicate.negate();
			}
			return this;
		}

		public Filter or(Filter... more) {

			Predicate<ExecutionInfo> local = or(
					asList(more).stream().filter(filt -> null != filt.apply()).map(Filter::apply).collect(toList()));

			if (null != this.predicate) {
				this.predicate = this.predicate.or(local);
			} else {
				this.predicate = local;
			}
			return this;
		}

		public Filter and(Filter... more) {

			Predicate<ExecutionInfo> local = or(
					asList(more).stream().filter(filt -> null != filt.apply()).map(Filter::apply).collect(toList()));

			if (null != this.predicate) {
				this.predicate = this.predicate.and(local);
			} else {
				this.predicate = local;
			}
			return this;
		}

		public Predicate<ExecutionInfo> or(List<Predicate<ExecutionInfo>> more) {

			return p -> more.stream().noneMatch(pred -> pred.test(p));
		}

		public Predicate<ExecutionInfo> and(List<Predicate<ExecutionInfo>> more) {

			return p -> more.stream().noneMatch(pred -> !pred.test(p));
		}

		private static Predicate<ExecutionInfo> byFieldName(String fieldName, Object value) {
			return (p -> {
				final Map<String, String> attributeMap = new HashMap<>();
				attributeMap.put(RuleConstants.ATTRIBUTE_NAME, fieldName);

				final Object findFieldByName = EntityUtil.findFromGeneric(p, new HashSet<>(),
						p.getClass().getGenericSuperclass(), attributeMap);
				return ((findFieldByName == value) || (null != findFieldByName && findFieldByName.equals(value)));
			});
		}

	}

	/**
	 * @return the result
	 */
	public List<ExecutionInfo> getResult() {
		return result;
	}

	/**
	 * @return the groupedResult
	 */
	public Map<Object, List<ExecutionInfo>> getGroupedResult() {
		return groupedResult;
	}

	public enum FieldNames {

		RULE_TYPE("ruleType", false), GROUP_ID("groupId", true), GROUP_STATUS("groupStatus", false),
		RULE_ID("ruleId", true), RULE_STATUS("ruleStatus", false), ATTRIBUTE_STATUS("attributeStatus", false),
		REFERENCE_VALUE("referenceValue", true), VALUE("value", true), CATEGORY("category", false),
		REFERENCE("reference", true), ATTRIBUTE_LABEL("attributeLabel", true), ATTRIBUTE("attribute", true);

		private final String value;

		private final boolean ignoreCase;

		private FieldNames(final String value, final boolean ignoreCase) {
			this.value = value;
			this.ignoreCase = ignoreCase;
		}

		@Override
		public String toString() {
			return val();
		}

		public String val() {
			return this.value;
		}

		public boolean ignoreCase() {
			return this.ignoreCase;
		}

	}

}
