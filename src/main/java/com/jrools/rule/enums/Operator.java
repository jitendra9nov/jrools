/* (C) 2022 */
package com.jrools.rule.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public enum Operator {

	@JsonProperty("isToday")
	IS_TODAY("isToday"),
	
	@JsonProperty("isFuture")
	IS_FUTURE("isFuture"),
	
	@JsonProperty("isPast")
	IS_PAST("isPast"),
	
	@JsonProperty("withinDays")
	WITHIN_DAYS("withinDays"),
	
	@JsonProperty("withinDaysFuture")
	WITHIN_DAYS_FUTURE("withinDaysFuture"),
	
	@JsonProperty("withinDaysPast")
	WITHIN_DAYS_PAST("withinDaysPast"),
	
	@JsonProperty("withinMonths")
	WITHIN_MONTHS("withinMonths"),
	
	@JsonProperty("withinMonthsFuture")
	WITHIN_MONTHS_FUTURE("withinMonthsFuture"),
	
	@JsonProperty("withinMonthsPast")
	WITHIN_MONTHS_PAST("withinMonthsPast"),
	
	@JsonProperty("anything")
	ANYTHING("anything"),
	
	@JsonProperty("isNull")
	IS_NULL("isNull"),
	
	@JsonProperty("notNull")
	NOT_NULL("notNull"),
	
	@JsonProperty("containsString")
	CONTAINS_STRING("containsString"),
	
	@JsonProperty("containsStringIgnorecase")
	CONTAINS_STRING_IGNORECASE("containsStringIgnorecase"),
	
	@JsonProperty("startsWith")
	STARTS_WITH("startsWith"),
	
	@JsonProperty("endsWith")
	ENDS_WITH("endsWith"),
	
	@JsonProperty("equals")
	EQUALS("equals"),
	
	@JsonProperty("equalsIgnorecase")
	EQUALS_IGNORECASE("equalsIgnorecase"),
	
	@JsonProperty("notEquals")
	NOT_EQUALS("notEquals"),
	
	@JsonProperty("in")
	IN("in"),
	
	@JsonProperty("notIn")
	NOT_IN("notIn"),
	
	@JsonProperty("gt")
	GREATER_THAN("gt"),
	
	@JsonProperty("gte")
	GREATER_THAN_EQUALSTO("gte"),
	
	@JsonProperty("lt")
	LESS_THAN("lt"),
	
	@JsonProperty("gte")
	LESS_THAN_EQUALSTO("lte"),
	
	@JsonProperty("ngt")
	NOT_GREATER_THAN("ngt"),
	
	@JsonProperty("ngte")
	NOT_GREATER_THAN_EQUALSTO("ngte"),
	
	@JsonProperty("nlt")
	NOT_LESS_THAN("nlt"),
	
	@JsonProperty("gte")
	NOT_LESS_THAN_EQUALSTO("nlte"),
	
	@JsonProperty("empty")
	EMPTY("empty"),
	
	@JsonProperty("notEmpty")
	NOT_EMPTY("notEmpty"),
	
	@JsonProperty("nullOrEmpty")
	NULL_OR_EMPTY("nullOrEmpty"),
	
	@JsonProperty("notNullOrEmpty")
	NOT_NULL_OR_EMPTY("notNullOrEmpty"),;

	private static final Map<String, Operator> FORMAT_MAP = of(values())
			.collect(toMap(s -> s.value, identity()));

	@JsonCreator
	public static Operator form(final String type) {
		return ofNullable(FORMAT_MAP.get(type)).orElseThrow(() -> new IllegalArgumentException(type));
	}

	private final String value;

	private Operator(final String value) {
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
