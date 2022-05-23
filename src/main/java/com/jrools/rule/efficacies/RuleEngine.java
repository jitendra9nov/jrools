/* (C) 2022 */
package com.jrools.rule.efficacies;

import static com.jrools.rule.exception.Message.create;
import static com.jrools.rule.exception.RuleException.create;
import static com.jrools.rule.facts.EngineResponse.builder;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.jrools.rule.enums.RuleType;
import com.jrools.rule.enums.RuleTypeInfo;
import com.jrools.rule.exception.RuleException;
import com.jrools.rule.facts.EngineRequest;
import com.jrools.rule.facts.EngineResponse;
import com.jrools.rule.facts.EngineResponse.Builder;
import com.jrools.rule.facts.Rule;
import com.jrools.rule.service.StorageService;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

public class RuleEngine {

	private static final Logger LOGGER = LoggerFactory.getLogger(RuleEngine.class);

	private final StorageService storageService;

	public RuleEngine(StorageService storageService) {
		this.storageService = storageService;
	}

	public EngineResponse eval(EngineRequest<?> engineRequest) throws RuleException {

		StopWatch watch = null;

		try {
			watch = new StopWatch();

			watch.start();
			LOGGER.info("Rule Execution Started ...");

			Builder builder = builder();

			try {
				List<RuleTypeInfo> ruleTypes = engineRequest.getRuleTypes();
				Objects.requireNonNull(ruleTypes, "ruleTypes can't be null");

				List<Rule> ruleSets = storageService
						.loadAllRules(ruleTypes.stream().map(RuleTypeInfo::getRuleType).collect(toList()));

				Map<String, Rule> referenceRules = loadReferenceRule(ruleSets);

				RuleTask mainTask = new RuleTask(engineRequest, builder, ruleSets, referenceRules);

				commonPool().invoke(mainTask);
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String excepString = sw.toString();

				String finalMess = "Rule Engine Error ::" + excepString;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Exception occurred inside Rule Engine with Details [{}]", finalMess);
				}

				throw create(e.getMessage(), e.getCause(), create("RULE_ENGINE", "TECHNICAL_ERROR", finalMess));
			}
			LOGGER.info("Rule Execution Ended ...");
			return builder.build();

		} finally {
			stopWatchEnd(watch);
		}
	}

	private static void stopWatchEnd(StopWatch watch) {

		if (watch != null) {
			watch.stop();

			LOGGER.info("Total execition time elapsed by the Rule Engine is {} milliseconds",
					watch.getTotalTimeMillis());
		}

	}

	private static Map<String, Rule> loadReferenceRule(List<Rule> ruleSets) {

		Map<String, Rule> ruleSetLocal = null;
		if (!isEmpty(ruleSets)) {
			ruleSetLocal = ruleSets.stream()
					.filter(rule -> (RuleType.REFERENCE == rule.getRuleType() && !isEmpty(rule.getRules())))
					.collect(Collectors.toMap(Rule::getGroupId, rule -> rule));
		}
		return ruleSetLocal;
	}

}
