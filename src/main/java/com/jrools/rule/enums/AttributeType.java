/* (C) 2022 */
package com.jrools.rule.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public enum AttributeType {

	@JsonProperty("string")
	STRING("string"),

	@JsonProperty("boolean")
	BOOLEAN("boolean"),

	@JsonProperty("enum")
	ENUM("enum"),

	@JsonProperty("numeric")
	NUMERIC("numeric"),
	
	@JsonProperty("object")
	OBJECT("object"),
	
	@JsonProperty("collection")
	COLLECTION("collection"),

	@JsonProperty("date")
	DATE("date");

	private static final Map<String, AttributeType> FORMAT_MAP = of(values())
			.collect(toMap(s -> s.value, identity()));

	@JsonCreator
	public static AttributeType form(final String type) {
		return ofNullable(FORMAT_MAP.get(type)).orElseThrow(() -> new IllegalArgumentException(type));
	}

	private final String value;

	private AttributeType(final String value) {
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
