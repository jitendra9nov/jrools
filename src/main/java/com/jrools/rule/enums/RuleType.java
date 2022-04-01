/* (C) 2022 */
package com.jrools.rule.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public enum RuleType {

	@JsonProperty("compatibility")
	COMPATIBILITY("compatibility"),

	@JsonProperty("eligibility")
	ELIGIBILITY("eligibility"),

	@JsonProperty("suitability")
	SUITABILITY("suitability"),

	@JsonProperty("reference")
	REFERENCE("reference"),

	@JsonProperty("auxilary")
	AUXILARY("auxilary");

	private static final Map<String, RuleType> FORMAT_MAP = of(values())
			.collect(toMap(s -> s.value, identity()));

	@JsonCreator
	public static RuleType form(final String type) {
		return ofNullable(FORMAT_MAP.get(type)).orElseThrow(() -> new IllegalArgumentException(type));
	}

	private final String value;

	private RuleType(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return val();
	}

	public String val() {
		return this.value;
	}

}
