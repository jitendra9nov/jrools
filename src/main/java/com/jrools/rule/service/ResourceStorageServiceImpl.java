/* (C) 2022 */
package com.jrools.rule.service;

import static com.jrools.rule.constants.RuleConstants.JSON_EXT;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrools.rule.enums.RuleType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.digester.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression("'${${spring.profiles.active}.rule.storage.type}'=='resource'")
public class ResourceStorageServiceImpl implements StorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceStorageServiceImpl.class);

	@Autowired
	private ObjectMapper mapper;

	@Value("${${spring.profiles.active}.rule.storage.path:}")
	private String rulesetFilePath;

	@Override
	public List<Rule> loadAllRules(List<RuleType> ruleTypes) {
		List<Rule> ruleSets = new ArrayList<>();

		if (!isEmpty(ruleSets)) {
			ruleTypes.forEach(ruleType -> loadRuleForType(ruleSets, ruleType));

		} else {
			loadRuleForType(ruleSets, null);
		}

		return ruleSets;
	}

	private void loadRuleForType(List<Rule> ruleSets, RuleType ruleType) {
		try {
			final String ruleFile = ((null != ruleType) && !hasText(ruleType.val())) ? ruleType.val() + JSON_EXT
					: "*" + JSON_EXT;

			LOGGER.debug("loading {} rules from file {}", ruleType, ruleFile);

			for (Resource resource : getRuleFiles(ruleFile)) {

				loadRuleFile(ruleSets, resource);
			}

		} catch (IOException e) {
			LOGGER.error("Exception Occurred while loading rules", e);
		}

	}

	private void loadRuleFile(List<Rule> ruleSets, Resource resource) {
		try(final InputStream inputStream = resource.getInputStream();) {
			LOGGER.debug("loading Rule File :{}",resource.getFilename());
			
			ruleSets.addAll(mapper.readValue(inputStream, new TypeReference<List<Rule> >() {
			}).stream().collect(toList()));
		} catch (Exception e) {
			LOGGER.error("Exception Occurred while loading rules", e);
		}

	}

	private Resource[] getRuleFiles(String ruleFile) throws IOException {
		final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		return resolver.getResources("classpath*:" + rulesetFilePath + "**/" + ruleFile);
	}

}
