/* (C) 2022 */
package com.jrools.rule.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public enum Category {

	@JsonProperty("A")
	A("A"),

	@JsonProperty("B")
	B("B"),

	@JsonProperty("C")
	C("C"),

	@JsonProperty("D")
	D("D"),
	
	@JsonProperty("E")
	E("E"),
	
	@JsonProperty("F")
	F("F");

	private static final Map<String, Category> FORMAT_MAP = of(values())
			.collect(toMap(s -> s.value, identity()));

	@JsonCreator
	public static Category form(final String type) {
		return ofNullable(FORMAT_MAP.get(type)).orElseThrow(() -> new IllegalArgumentException(type));
	}

	private final String value;

	private Category(final String value) {
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
