package com.jrools.rule.service;

import java.util.List;

import org.apache.tomcat.util.digester.Rule;

import com.jrools.rule.facts.RuleType;

public interface StorageService {
	
	List<Rule> loadAllRules(List<RuleType> ruleTypes);

}
