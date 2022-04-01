/* (C) 2022 */
package com.jrools.rule.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public enum NumericType {

	@JsonProperty("byte")
	BYTE("byte"),
	@JsonProperty("short")
	SHORT("short"),
	@JsonProperty("int")
	INTEGER("int"),
	@JsonProperty("float")
	FLOAT("float"),
	@JsonProperty("double")
	DOUBLE("double"),
	@JsonProperty("long")
	LONG("long"),
	@JsonProperty("bigdecimal")
	BIGDECIMAL("bigdecimal");

	private static final Map<String, NumericType> FORMAT_MAP = of(values())
			.collect(toMap(s -> s.value, identity()));

	@JsonCreator
	public static NumericType form(final String type) {
		return ofNullable(FORMAT_MAP.get(type)).orElseThrow(() -> new IllegalArgumentException(type));
	}

	private final String value;

	private NumericType(final String value) {
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
