/* (C) 2022 */
package com.jrools.rule.facts;

import static com.jrools.rule.enums.Iterate.ANY;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jrools.rule.enums.AttributeType;
import com.jrools.rule.enums.Category;
import com.jrools.rule.enums.Combinator;
import com.jrools.rule.enums.Iterate;
import com.jrools.rule.enums.NumericType;
import com.jrools.rule.enums.Operator;
import com.jrools.rule.enums.RuleType;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {

	// Group Attribute
	private String groupId;
	private String groupVerbiage;
	private String groupName;
	private RuleType ruleType;
	private Category category;
	private Integer priority;
	private Combinator combinator;
	private Boolean negator;
	private Boolean skipIfNull;
	private Boolean ignorecase;
	private boolean auxiliary;
	private List<Rule> rules;
	private Map<String, String> addOns;

	// Rule Attributes
	private String ruleId;
	private String ruleName;
	private String failureText;
	private Integer executionOrder = 0;
	private String attribute;
	private AttributeType attributeType;
	private NumericType numericType;
	private String attributeLabel;
	private String attributeBeanName;
	private String containerBeanName;
	private String containerName;

	private Boolean jsonPath = false;
	
	private String value;
	private String valueReference;
	private Operator operator;
	private Iterate iterate=ANY;

	private String pattern;

	private String businessEntity;
	private String referenceRule;
	@JsonIgnore private RuleMatcher ruleMatcher;
	
	@JsonCreator
	public Rule(@JsonProperty("operator") Operator operator,

			@JsonProperty("value") String value,

			@JsonProperty("attributeType") AttributeType attributeType,

			@JsonProperty("numericType") NumericType numericType) {
		this.operator=operator;
		this.value=value;
		this.attributeType=attributeType;
		this.numericType=numericType;
		if(null!=operator && null!=attributeType) {
			setRuleMatcher(new RuleMatcher(operator,value,attributeType,numericType));
		}

	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @return the groupVerbiage
	 */
	public String getGroupVerbiage() {
		return groupVerbiage;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @return the ruleType
	 */
	public RuleType getRuleType() {
		return ruleType;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @return the priority
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * @return the combinator
	 */
	public Combinator getCombinator() {
		return combinator;
	}

	/**
	 * @return the negator
	 */
	public Boolean getNegator() {
		return negator;
	}

	/**
	 * @return the skipIfNull
	 */
	public Boolean getSkipIfNull() {
		return skipIfNull;
	}

	/**
	 * @return the ignorecase
	 */
	public Boolean getIgnorecase() {
		return ignorecase;
	}

	/**
	 * @return the auxiliary
	 */
	public boolean isAuxiliary() {
		return auxiliary;
	}

	/**
	 * @return the rules
	 */
	public List<Rule> getRules() {
		return rules;
	}

	/**
	 * @return the addOns
	 */
	public Map<String, String> getAddOns() {
		return addOns;
	}

	/**
	 * @return the ruleId
	 */
	public String getRuleId() {
		return ruleId;
	}

	/**
	 * @return the ruleName
	 */
	public String getRuleName() {
		return ruleName;
	}

	/**
	 * @return the failureText
	 */
	public String getFailureText() {
		return failureText;
	}

	/**
	 * @return the executionOrder
	 */
	public Integer getExecutionOrder() {
		return executionOrder;
	}

	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * @return the attributeType
	 */
	public AttributeType getAttributeType() {
		return attributeType;
	}

	/**
	 * @return the numericType
	 */
	public NumericType getNumericType() {
		return numericType;
	}

	/**
	 * @return the attributeLabel
	 */
	public String getAttributeLabel() {
		return attributeLabel;
	}

	/**
	 * @return the attributeBeanName
	 */
	public String getAttributeBeanName() {
		return attributeBeanName;
	}

	/**
	 * @return the containerBeanName
	 */
	public String getContainerBeanName() {
		return containerBeanName;
	}

	/**
	 * @return the containerName
	 */
	public String getContainerName() {
		return containerName;
	}

	/**
	 * @return the jsonPath
	 */
	public Boolean getJsonPath() {
		return jsonPath;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the valueReference
	 */
	public String getValueReference() {
		return valueReference;
	}

	/**
	 * @return the operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * @return the iterate
	 */
	public Iterate getIterate() {
		return iterate;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @return the businessEntity
	 */
	public String getBusinessEntity() {
		return businessEntity;
	}

	/**
	 * @return the referenceRule
	 */
	public String getReferenceRule() {
		return referenceRule;
	}

	/**
	 * @return the ruleMatcher
	 */
	public RuleMatcher getRuleMatcher() {
		return ruleMatcher;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @param groupVerbiage the groupVerbiage to set
	 */
	public void setGroupVerbiage(String groupVerbiage) {
		this.groupVerbiage = groupVerbiage;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @param ruleType the ruleType to set
	 */
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @param combinator the combinator to set
	 */
	public void setCombinator(Combinator combinator) {
		this.combinator = combinator;
	}

	/**
	 * @param negator the negator to set
	 */
	public void setNegator(Boolean negator) {
		this.negator = negator;
	}

	/**
	 * @param skipIfNull the skipIfNull to set
	 */
	public void setSkipIfNull(Boolean skipIfNull) {
		this.skipIfNull = skipIfNull;
	}

	/**
	 * @param ignorecase the ignorecase to set
	 */
	public void setIgnorecase(Boolean ignorecase) {
		this.ignorecase = ignorecase;
	}

	/**
	 * @param auxiliary the auxiliary to set
	 */
	public void setAuxiliary(boolean auxiliary) {
		this.auxiliary = auxiliary;
	}

	/**
	 * @param rules the rules to set
	 */
	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	/**
	 * @param addOns the addOns to set
	 */
	public void setAddOns(Map<String, String> addOns) {
		this.addOns = addOns;
	}

	/**
	 * @param ruleId the ruleId to set
	 */
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	/**
	 * @param ruleName the ruleName to set
	 */
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	/**
	 * @param failureText the failureText to set
	 */
	public void setFailureText(String failureText) {
		this.failureText = failureText;
	}

	/**
	 * @param executionOrder the executionOrder to set
	 */
	public void setExecutionOrder(Integer executionOrder) {
		this.executionOrder = executionOrder;
	}

	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * @param attributeType the attributeType to set
	 */
	public void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @param numericType the numericType to set
	 */
	public void setNumericType(NumericType numericType) {
		this.numericType = numericType;
	}

	/**
	 * @param attributeLabel the attributeLabel to set
	 */
	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}

	/**
	 * @param attributeBeanName the attributeBeanName to set
	 */
	public void setAttributeBeanName(String attributeBeanName) {
		this.attributeBeanName = attributeBeanName;
	}

	/**
	 * @param containerBeanName the containerBeanName to set
	 */
	public void setContainerBeanName(String containerBeanName) {
		this.containerBeanName = containerBeanName;
	}

	/**
	 * @param containerName the containerName to set
	 */
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	/**
	 * @param jsonPath the jsonPath to set
	 */
	public void setJsonPath(Boolean jsonPath) {
		this.jsonPath = jsonPath;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param valueReference the valueReference to set
	 */
	public void setValueReference(String valueReference) {
		this.valueReference = valueReference;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	/**
	 * @param iterate the iterate to set
	 */
	public void setIterate(Iterate iterate) {
		this.iterate = iterate;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @param businessEntity the businessEntity to set
	 */
	public void setBusinessEntity(String businessEntity) {
		this.businessEntity = businessEntity;
	}

	/**
	 * @param referenceRule the referenceRule to set
	 */
	public void setReferenceRule(String referenceRule) {
		this.referenceRule = referenceRule;
	}

	/**
	 * @param ruleMatcher the ruleMatcher to set
	 */
	public void setRuleMatcher(RuleMatcher ruleMatcher) {
		this.ruleMatcher = ruleMatcher;
	}


}
