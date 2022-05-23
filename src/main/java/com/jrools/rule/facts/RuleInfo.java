package com.jrools.rule.facts;

import com.jrools.rule.facts.EngineResponse.Builder;

public class RuleInfo {
	
	public static RuleInfo create(Rule group, Rule rule, Object referenceValue, Builder builder) {
		return new RuleInfo(group, rule, referenceValue, builder);
	}
	private Rule group;
	
	private Rule rule;
	
	private Object referenceValue;
	
	private Object attributeValue;
	
	private String refRuleValue;
	
	private Boolean refRuleValueSuccess;
	
	private RuleMatcher refRuleMatcher;
	
	private Builder builder;

	private RuleInfo(Rule group, Rule rule, Object referenceValue, Builder builder) {
		this.group = group;
		this.rule = rule;
		this.referenceValue = referenceValue;
		this.builder = builder;
	}

	/**
	 * @return the group
	 */
	public Rule getGroup() {
		return group;
	}

	/**
	 * @return the rule
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * @return the referenceValue
	 */
	public Object getReferenceValue() {
		return referenceValue;
	}

	/**
	 * @return the attributeValue
	 */
	public Object getAttributeValue() {
		return attributeValue;
	}

	/**
	 * @return the refRuleValue
	 */
	public String getRefRuleValue() {
		return refRuleValue;
	}

	/**
	 * @return the refRuleValueSuccess
	 */
	public Boolean getRefRuleValueSuccess() {
		return refRuleValueSuccess;
	}

	/**
	 * @return the refRuleMatcher
	 */
	public RuleMatcher getRefRuleMatcher() {
		return refRuleMatcher;
	}

	/**
	 * @return the builder
	 */
	public Builder getBuilder() {
		return builder;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(Rule group) {
		this.group = group;
	}

	/**
	 * @param rule the rule to set
	 */
	public void setRule(Rule rule) {
		this.rule = rule;
	}

	/**
	 * @param referenceValue the referenceValue to set
	 */
	public void setReferenceValue(Object referenceValue) {
		this.referenceValue = referenceValue;
	}

	/**
	 * @param attributeValue the attributeValue to set
	 */
	public void setAttributeValue(Object attributeValue) {
		this.attributeValue = attributeValue;
	}

	/**
	 * @param refRuleValue the refRuleValue to set
	 */
	public void setRefRuleValue(String refRuleValue) {
		this.refRuleValue = refRuleValue;
	}

	/**
	 * @param refRuleValueSuccess the refRuleValueSuccess to set
	 */
	public void setRefRuleValueSuccess(Boolean refRuleValueSuccess) {
		this.refRuleValueSuccess = refRuleValueSuccess;
	}

	/**
	 * @param refRuleMatcher the refRuleMatcher to set
	 */
	public void setRefRuleMatcher(RuleMatcher refRuleMatcher) {
		this.refRuleMatcher = refRuleMatcher;
	}

	/**
	 * @param builder the builder to set
	 */
	public void setBuilder(Builder builder) {
		this.builder = builder;
	}
	
	

}
