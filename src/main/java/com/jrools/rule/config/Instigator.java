/* s (C)2022 */
package com.jrools.rule.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrools.rule.efficacies.RuleEngine;
import com.jrools.rule.service.StorageService;

@Configuration
@ComponentScan(basePackages = "com.jrools.rule")
@PropertySource(value = "classpath:application.properties")
public class Instigator {
	
	@Value("${spring.profiles.active}")
	private String activeProfile;
	
	@Value("${${spring.profiles.active}.rule.storage.type:resource}")
	private String storageType;
	
@Bean
public ObjectMapper mapper() {
	return new ObjectMapper();
}

@Bean
//@ConditionalOnExpression("'${${spring.profiles.active}.rule.storage.type}'=='resource'")
public RuleEngine ruleEngine(@Autowired StorageService storageService) {
	return new RuleEngine(storageService);
}
	

}
