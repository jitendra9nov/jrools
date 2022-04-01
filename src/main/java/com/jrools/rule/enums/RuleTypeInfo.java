/* (C) 2022 */
package com.jrools.rule.enums;

import static com.jrools.rule.enums.RuleType.REFERENCE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleTypeInfo {

	@JsonProperty("ruleType")
	private RuleType ruleType;

	@JsonProperty("onList")
	private Boolean onList;

	@JsonProperty("stopOnFirstRuleFailure")
	private Boolean stopOnFirstRuleFailure;

	public RuleTypeInfo() {
		this(REFERENCE, null, null);
	}

	public RuleTypeInfo(RuleType ruleType, Boolean stopOnFirstRuleFailure, Boolean onList) {
		this.ruleType = ruleType;
		this.stopOnFirstRuleFailure = stopOnFirstRuleFailure;
		this.onList = onList;
	}

	@JsonCreator
	public static RuleTypeInfo form(@JsonProperty("ruleType") RuleType ruleType,
			@JsonProperty("stopOnFirstRuleFailure") Boolean stopOnFirstRuleFailure,
			@JsonProperty("onList") Boolean onList) {
		return new RuleTypeInfo(ruleType, stopOnFirstRuleFailure, onList);
	}

	public static RuleTypeInfo form(RuleType ruleType, Boolean onList) {
		return new RuleTypeInfo(ruleType, null, onList);
	}

	public static RuleTypeInfo form(RuleType ruleType) {
		return new RuleTypeInfo(ruleType, null, null);
	}

	/**
	 * @return the ruleType
	 */
	public RuleType getRuleType() {
		return ruleType;
	}

	/**
	 * @return the onList
	 */
	public Boolean getOnList() {
		return onList;
	}

	/**
	 * @return the stopOnFirstRuleFailure
	 */
	public Boolean getStopOnFirstRuleFailure() {
		return stopOnFirstRuleFailure;
	}

	/**
	 * @param ruleType the ruleType to set
	 */
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	/**
	 * @param onList the onList to set
	 */
	public void setOnList(Boolean onList) {
		this.onList = onList;
	}

	/**
	 * @param stopOnFirstRuleFailure the stopOnFirstRuleFailure to set
	 */
	public void setStopOnFirstRuleFailure(Boolean stopOnFirstRuleFailure) {
		this.stopOnFirstRuleFailure = stopOnFirstRuleFailure;
	}
	
	

}
