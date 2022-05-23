package com.jrools.rule.service;

import java.util.List;

import com.jrools.rule.enums.RuleType;
import com.jrools.rule.facts.Rule;

public interface StorageService {
	
	List<Rule> loadAllRules(List<RuleType> ruleTypes);

}
