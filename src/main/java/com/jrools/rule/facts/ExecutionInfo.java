/* (C) 2022 */
package com.jrools.rule.facts;

import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jrools.rule.enums.AttributeType;
import com.jrools.rule.enums.Category;
import com.jrools.rule.enums.RuleType;
import com.jrools.rule.enums.Status;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionInfo {
	
	private Object referenceValue;
	private RuleType  ruleType;
	private Status  ruleStatus;
	private String  groupId;
	private String  groupVerbiage;
	private String  groupName;
	private Status  groupStatus;
	private Category  category;
	private Map<String, String>  addOns;
	private String  ruleId;
	private String  ruleName;
	private String  attribute;
	private String  attributeLabel;
	private AttributeType  attributeType;
	private Integer  Order;
	
	@JsonInclude(JsonInclude.Include.ALWAYS)
	private Object  value;
	
	
	private String  pattern;
	private Object  enumInfo;
	private Status  attributeStatus;
	
	private String  businessEntity;
	private String  referenceRule;
	private String  errorMessage;
	private String  failureText;
	/**
	 * @return the referenceValue
	 */
	public Object getReferenceValue() {
		return referenceValue;
	}
	/**
	 * @return the ruleType
	 */
	public RuleType getRuleType() {
		return ruleType;
	}
	/**
	 * @return the ruleStatus
	 */
	public Status getRuleStatus() {
		return ruleStatus;
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
	 * @return the groupStatus
	 */
	public Status getGroupStatus() {
		return groupStatus;
	}
	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
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
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}
	/**
	 * @return the attributeLabel
	 */
	public String getAttributeLabel() {
		return attributeLabel;
	}
	/**
	 * @return the attributeType
	 */
	public AttributeType getAttributeType() {
		return attributeType;
	}
	/**
	 * @return the order
	 */
	public Integer getOrder() {
		return Order;
	}
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}
	/**
	 * @return the enumInfo
	 */
	public Object getEnumInfo() {
		return enumInfo;
	}
	/**
	 * @return the attributeStatus
	 */
	public Status getAttributeStatus() {
		return attributeStatus;
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
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @return the failureText
	 */
	public String getFailureText() {
		return failureText;
	}
	/**
	 * @param referenceValue the referenceValue to set
	 */
	public void setReferenceValue(Object referenceValue) {
		this.referenceValue = referenceValue;
	}
	/**
	 * @param ruleType the ruleType to set
	 */
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}
	/**
	 * @param ruleStatus the ruleStatus to set
	 */
	public void setRuleStatus(Status ruleStatus) {
		this.ruleStatus = ruleStatus;
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
	 * @param groupStatus the groupStatus to set
	 */
	public void setGroupStatus(Status groupStatus) {
		this.groupStatus = groupStatus;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}
	/**
	 * @param addOns the addOns to set
	 */
	public void setAddOns(Map<String, String> addOns) {
		if(!isEmpty(addOns)) {
			
			if(isEmpty(this.addOns)) {
				this.addOns = addOns;
			}
			this.addOns.putAll(addOns);
		}
		
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
	 * @param attribute the attribute to set
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	/**
	 * @param attributeLabel the attributeLabel to set
	 */
	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}
	/**
	 * @param attributeType the attributeType to set
	 */
	public void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
	}
	/**
	 * @param order the order to set
	 */
	public void setOrder(Integer order) {
		Order = order;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	/**
	 * @param enumInfo the enumInfo to set
	 */
	public void setEnumInfo(Object enumInfo) {
		this.enumInfo = enumInfo;
	}
	/**
	 * @param attributeStatus the attributeStatus to set
	 */
	public void setAttributeStatus(Status attributeStatus) {
		this.attributeStatus = attributeStatus;
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
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 * @param failureText the failureText to set
	 */
	public void setFailureText(String failureText) {
		this.failureText = failureText;
	}
	
	

}
