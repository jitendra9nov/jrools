/* (C) 2022 */
package com.jrools.rule.facts;

import static com.jrools.rule.facts.EngineResponse.FieldNames.RULE_TYPE;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

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
	
	public static Builder stream() {
		return new Builder(this);
	}
	public static EngineResponse merge(EngineResponse engineResponseFirst,EngineResponse engineResponseSecond, boolean discardChanges) {
		Objects.requireNonNull(engineResponseFirst, "engineResponseFirst can' be null");
		Objects.requireNonNull(engineResponseSecond, "engineResponseSecond can' be null");
		
		return new Builder().executionInfor(discardChanges? engineResponseFirst.discard().result:engineResponseFirst.result)
				.executionInfor(discardChanges? engineResponseSecond.discard().result:engineResponseSecond.result).build();

	}
	public  EngineResponse discard() {
		this.result = !isEmpty(this.rawResult) ? new ArrayList<>(this.rawResult): null;
		return groupBy(RULE_TYPE, false);
		
	}
	
	public  final EngineResponse groupBy(String fieldName, boolean ignoreCaseForString) {
		
	}
	
	
	
	
	

	public enum FieldNames {

		RULE_TYPE("ruleType", false), GROUP_ID("groupId", true), GROUP_STATUS("groupStatus", false),
		RULE_ID("ruleId", true), RULE_STATUS("ruleStatus", false), ATTRIBUTE_STATUS("attributeStatus", false),
		REFERENCE_VALUE("referenceValue", true), VALUE("value", true), CATEGORY("category", false),
		ATTRIBUTE_LABEL("attributeLabel", true), ATTRIBUTE("attribute", true);

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
