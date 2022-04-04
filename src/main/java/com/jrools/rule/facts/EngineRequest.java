package com.jrools.rule.facts;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jrools.rule.enums.RuleTypeInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EngineRequest<T> {

	@JsonProperty("ruleTypes")
	private List<RuleTypeInfo> ruleTypes;

	@JsonProperty("requestObjects")
	private List<T> requestObjects;

	@JsonProperty("referenceKey")
	private String referenceKey;

	@JsonProperty("stopOnFirstRuleTypeFailure")
	private Boolean stopOnFirstRuleTypeFailure;

	@JsonProperty("referenceKey")
	private Boolean stopOnFirstRuleFailure;

	public EngineRequest() {
	}

	@JsonCreator
	public EngineRequest(@JsonProperty("ruleTypes") List<RuleTypeInfo> ruleTypes,

			@JsonProperty("requestObjects") List<T> requestObjects,

			@JsonProperty("referenceKey") String referenceKey,

			@JsonProperty("stopOnFirstRuleTypeFailure") Boolean stopOnFirstRuleTypeFailure,

			@JsonProperty("referenceKey") Boolean stopOnFirstRuleFailure) {
		this.ruleTypes=ruleTypes;
		this.requestObjects=requestObjects;
		this.referenceKey=referenceKey;
		this.stopOnFirstRuleTypeFailure=stopOnFirstRuleTypeFailure;
		this.stopOnFirstRuleFailure=stopOnFirstRuleFailure;

	}

	/**
	 * @return the ruleTypes
	 */
	public List<RuleTypeInfo> getRuleTypes() {
		return ruleTypes;
	}

	/**
	 * @return the requestObjects
	 */
	public List<T> getRequestObjects() {
		return requestObjects;
	}

	/**
	 * @return the referenceKey
	 */
	public String getReferenceKey() {
		return referenceKey;
	}

	/**
	 * @return the stopOnFirstRuleTypeFailure
	 */
	public Boolean getStopOnFirstRuleTypeFailure() {
		return stopOnFirstRuleTypeFailure;
	}

	/**
	 * @return the stopOnFirstRuleFailure
	 */
	public Boolean getStopOnFirstRuleFailure() {
		return stopOnFirstRuleFailure;
	}

	/**
	 * @param ruleTypes the ruleTypes to set
	 */
	public void setRuleTypes(List<RuleTypeInfo> ruleTypes) {
		this.ruleTypes = ruleTypes;
	}

	/**
	 * @param requestObjects the requestObjects to set
	 */
	public void setRequestObjects(List<T> requestObjects) {
		this.requestObjects = requestObjects;
	}

	/**
	 * @param referenceKey the referenceKey to set
	 */
	public void setReferenceKey(String referenceKey) {
		this.referenceKey = referenceKey;
	}

	/**
	 * @param stopOnFirstRuleTypeFailure the stopOnFirstRuleTypeFailure to set
	 */
	public void setStopOnFirstRuleTypeFailure(Boolean stopOnFirstRuleTypeFailure) {
		this.stopOnFirstRuleTypeFailure = stopOnFirstRuleTypeFailure;
	}

	/**
	 * @param stopOnFirstRuleFailure the stopOnFirstRuleFailure to set
	 */
	public void setStopOnFirstRuleFailure(Boolean stopOnFirstRuleFailure) {
		this.stopOnFirstRuleFailure = stopOnFirstRuleFailure;
	}
	

}
