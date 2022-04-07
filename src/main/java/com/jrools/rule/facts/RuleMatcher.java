/* (C) 2022 */
package com.jrools.rule.facts;

import static com.jrools.rule.constants.RuleConstants.COMMA;
import static com.jrools.rule.constants.RuleConstants.EMPTY;
import static com.jrools.rule.enums.AttributeType.BOOLEAN;
import static com.jrools.rule.enums.AttributeType.DATE;
import static com.jrools.rule.enums.AttributeType.NUMERIC;
import static com.jrools.rule.enums.NumericType.INTEGER;
import static com.jrools.rule.utils.RuleUtil.parseDate;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.exparity.hamcrest.date.DateMatchers.after;
import static org.exparity.hamcrest.date.DateMatchers.before;
import static org.exparity.hamcrest.date.DateMatchers.isToday;
import static org.exparity.hamcrest.date.DateMatchers.sameDay;
import static org.exparity.hamcrest.date.DateMatchers.sameOrAfter;
import static org.exparity.hamcrest.date.DateMatchers.sameOrBefore;
import static org.exparity.hamcrest.date.DateMatchers.within;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jrools.rule.enums.AttributeType;
import com.jrools.rule.enums.NumericType;
import com.jrools.rule.enums.Operator;
import java.math.BigDecimal;
import java.util.Date;
import org.hamcrest.Matcher;
import org.slf4j.Logger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleMatcher {

	private static final String PROBLEM_WHILE_CONVERTING_TO_FORMAT_WITH_THE_ERROR = "Problem while converting {} to {} format with the error:: {}";

	private static final Logger LOGGER = getLogger(RuleMatcher.class);

	private Matcher<?> matcher;

	public RuleMatcher(Operator operator, String value, AttributeType attributeType, NumericType numericType) {
		String finalValue = value;

		if (null == value) {
			switch (attributeType) {
			case STRING:
				finalValue = EMPTY;
				break;
			case NUMERIC:
				finalValue = "0";
				break;
			case BOOLEAN:
				finalValue = FALSE.toString();
				break;
			default:
				finalValue = EMPTY;
				break;
			}
		}
		this.matcher = formMatcher(operator, finalValue, attributeType, (null != numericType ? numericType : INTEGER));

	}

	private static Matcher<?> formMatcher(Operator operator, String value, AttributeType attributeType,
			NumericType numericType) {

		Matcher<?> returnMatcher = null;

		boolean isNumeric = (attributeType == NUMERIC);

		boolean isDate = (attributeType == DATE);

		boolean isBoolean = (attributeType == BOOLEAN);

		switch (operator) {
		case ANYTHING:
			returnMatcher = anything();
			break;

		case IS_NULL:
			returnMatcher = is(nullValue());
			break;

		case NOT_NULL:
			returnMatcher = not(nullValue());
			break;

		case CONTAINS_STRING:
			returnMatcher = containsString(value);
			break;

		case CONTAINS_STRING_IGNORECASE:
			returnMatcher = containsStringIgnoringCase(value);
			break;

		case STARTS_WITH:
			returnMatcher = startsWith(value);
			break;

		case ENDS_WITH:
			returnMatcher = endsWith(value);
			break;

		case EMPTY:

			returnMatcher = is(empty());
			break;
		case NOT_EMPTY:

			returnMatcher = not(empty());
			break;

		case NULL_OR_EMPTY:

			returnMatcher = allOf(nullValue(), is(empty()));
			break;
		case NOT_NULL_OR_EMPTY:

			returnMatcher = allOf(not(nullValue()), not(empty()));
			break;

		case EQUALS:

			returnMatcher = equals(value, isDate, isBoolean, isNumeric, numericType);
			break;
		case EQUALS_IGNORECASE:

			returnMatcher = equalToIgnoringCase(value);
			break;
		case NOT_EQUALS:

			returnMatcher = notEquals(value, isDate, isBoolean, isNumeric, numericType);
			break;
		case IN:
			returnMatcher = isIn(value, isNumeric, numericType);
			break;

		case NOT_IN:

			returnMatcher = isNotIn(value, isNumeric, numericType);
			break;
		case GREATER_THAN:

			returnMatcher = gt(value, isDate, isNumeric, numericType);
			break;
		case GREATER_THAN_EQUALSTO:

			returnMatcher = gte(value, isDate, isNumeric, numericType);
			break;
		case LESS_THAN:
			returnMatcher = lt(value, isDate, isNumeric, numericType);
			break;

		case LESS_THAN_EQUALSTO:

			returnMatcher = lte(value, isDate, isNumeric, numericType);
			break;

		case NOT_GREATER_THAN:

			returnMatcher = not(gt(value, isDate, isNumeric, numericType));
			break;
		case NOT_GREATER_THAN_EQUALSTO:

			returnMatcher = not(gte(value, isDate, isNumeric, numericType));
			break;
		case NOT_LESS_THAN:
			returnMatcher = not(lt(value, isDate, isNumeric, numericType));
			break;

		case NOT_LESS_THAN_EQUALSTO:

			returnMatcher = not(lte(value, isDate, isNumeric, numericType));
			break;

		case IS_TODAY:

			returnMatcher = isToday();
			break;
		case IS_FUTURE:

			returnMatcher = after(new Date());
			break;
		case IS_PAST:
			returnMatcher = before(new Date());
			break;

		case WITHIN_DAYS:

			returnMatcher = within(parseInt(value), DAYS, new Date());
			break;
		case WITHIN_DAYS_FUTURE:

			returnMatcher = allOf(after(new Date()), within(parseInt(value), DAYS, new Date()));
			break;
		case WITHIN_DAYS_PAST:

			returnMatcher = allOf(before(new Date()), within(parseInt(value), DAYS, new Date()));
			break;
		case WITHIN_MONTHS:

			returnMatcher = within(parseInt(value), MONTHS, new Date());
			break;
		case WITHIN_MONTHS_FUTURE:

			returnMatcher = allOf(after(new Date()), within(parseInt(value), MONTHS, new Date()));
			break;
		case WITHIN_MONTHS_PAST:

			returnMatcher = allOf(before(new Date()), within(parseInt(value), MONTHS, new Date()));
			break;

		default:
			break;
		}

		return returnMatcher;
	}

	/**
	 * @return the matcher
	 */
	public Matcher<?> getMatcher() {
		return matcher;
	}

	/**
	 * @param matcher the matcher to set
	 */
	public void setMatcher(Matcher<?> matcher) {
		this.matcher = matcher;
	}

	private static Matcher<?> gt(String value, boolean isDate, boolean isNumeric, NumericType numericType) {
		Matcher<?> returnMatcher = null;
		try {
			if (isNumeric) {
				switch (numericType) {
				case BYTE:
					returnMatcher = greaterThan(Byte.valueOf(value));
					break;
				case SHORT:
					returnMatcher = greaterThan(Short.valueOf(value));
					break;
				case INTEGER:
					returnMatcher = greaterThan(Integer.valueOf(value));
					break;
				case LONG:
					returnMatcher = greaterThan(Long.valueOf(value));
					break;
				case FLOAT:
					returnMatcher = greaterThan(Float.valueOf(value));
					break;
				case DOUBLE:
					returnMatcher = greaterThan(Double.valueOf(value));
					break;
				case BIGDECIMAL:
					returnMatcher = greaterThan(new BigDecimal(value));
					break;
				default:
					returnMatcher = greaterThan(Integer.valueOf(value));
					break;
				}
			} else if (isDate) {
				returnMatcher = after(parseDate(value, null));
			} else {
				returnMatcher = greaterThan(value);
			}
		} catch (Exception e) {
			LOGGER.debug(PROBLEM_WHILE_CONVERTING_TO_FORMAT_WITH_THE_ERROR, value, numericType,
					e.getMessage());
		}

		return returnMatcher;
	}

	private static Matcher<?> gte(String value, boolean isDate, boolean isNumeric, NumericType numericType) {

		Matcher<?> returnMatcher = null;
		try {
			if (isNumeric) {
				switch (numericType) {
				case BYTE:
					returnMatcher = greaterThanOrEqualTo(Byte.valueOf(value));
					break;
				case SHORT:
					returnMatcher = greaterThanOrEqualTo(Short.valueOf(value));
					break;
				case INTEGER:
					returnMatcher = greaterThanOrEqualTo(Integer.valueOf(value));
					break;
				case LONG:
					returnMatcher = greaterThanOrEqualTo(Long.valueOf(value));
					break;
				case FLOAT:
					returnMatcher = greaterThanOrEqualTo(Float.valueOf(value));
					break;
				case DOUBLE:
					returnMatcher = greaterThanOrEqualTo(Double.valueOf(value));
					break;
				case BIGDECIMAL:
					returnMatcher = greaterThanOrEqualTo(new BigDecimal(value));
					break;
				default:
					returnMatcher = greaterThanOrEqualTo(Integer.valueOf(value));
					break;
				}
			} else if (isDate) {
				returnMatcher = sameOrAfter(parseDate(value, null));
			} else {
				returnMatcher = greaterThanOrEqualTo(value);
			}
		} catch (Exception e) {
			LOGGER.debug(PROBLEM_WHILE_CONVERTING_TO_FORMAT_WITH_THE_ERROR, value, numericType,
					e.getMessage());
		}

		return returnMatcher;

	}

	private static Matcher<?> lt(String value, boolean isDate, boolean isNumeric, NumericType numericType) {

		Matcher<?> returnMatcher = null;
		try {
			if (isNumeric) {
				switch (numericType) {
				case BYTE:
					returnMatcher = lessThan(Byte.valueOf(value));
					break;
				case SHORT:
					returnMatcher = lessThan(Short.valueOf(value));
					break;
				case INTEGER:
					returnMatcher = lessThan(Integer.valueOf(value));
					break;
				case LONG:
					returnMatcher = lessThan(Long.valueOf(value));
					break;
				case FLOAT:
					returnMatcher = lessThan(Float.valueOf(value));
					break;
				case DOUBLE:
					returnMatcher = lessThan(Double.valueOf(value));
					break;
				case BIGDECIMAL:
					returnMatcher = lessThan(new BigDecimal(value));
					break;
				default:
					returnMatcher = lessThan(Integer.valueOf(value));
					break;
				}
			} else if (isDate) {
				returnMatcher = before(parseDate(value, null));
			} else {
				returnMatcher = lessThan(value);
			}
		} catch (Exception e) {
			LOGGER.debug(PROBLEM_WHILE_CONVERTING_TO_FORMAT_WITH_THE_ERROR, value, numericType,
					e.getMessage());
		}

		return returnMatcher;

	}

	private static Matcher<?> lte(String value, boolean isDate, boolean isNumeric, NumericType numericType) {

		Matcher<?> returnMatcher = null;
		try {
			if (isNumeric) {
				switch (numericType) {
				case BYTE:
					returnMatcher = lessThanOrEqualTo(Byte.valueOf(value));
					break;
				case SHORT:
					returnMatcher = lessThanOrEqualTo(Short.valueOf(value));
					break;
				case INTEGER:
					returnMatcher = lessThanOrEqualTo(Integer.valueOf(value));
					break;
				case LONG:
					returnMatcher = lessThanOrEqualTo(Long.valueOf(value));
					break;
				case FLOAT:
					returnMatcher = lessThanOrEqualTo(Float.valueOf(value));
					break;
				case DOUBLE:
					returnMatcher = lessThanOrEqualTo(Double.valueOf(value));
					break;
				case BIGDECIMAL:
					returnMatcher = lessThanOrEqualTo(new BigDecimal(value));
					break;
				default:
					returnMatcher = lessThanOrEqualTo(Integer.valueOf(value));
					break;
				}
			} else if (isDate) {
				returnMatcher = sameOrBefore(parseDate(value, null));
			} else {
				returnMatcher = lessThanOrEqualTo(value);
			}
		} catch (Exception e) {
			LOGGER.debug(PROBLEM_WHILE_CONVERTING_TO_FORMAT_WITH_THE_ERROR, value, numericType,
					e.getMessage());
		}

		return returnMatcher;

	}

	private static Matcher<?> isNotIn(String value, boolean isNumeric, NumericType numericType) {
		Matcher<?> returnMatcher = null;

		if (isNumeric) {

			returnMatcher = is(
					in(asList(value.split(COMMA)).stream().map(val -> parseValue(numericType, val)).collect(toList())));

		} else {
			returnMatcher = is(in(asList(value.split(COMMA))));
		}
		return returnMatcher;
	}

	private static Matcher<?> isIn(String value, boolean isNumeric, NumericType numericType) {

		Matcher<?> returnMatcher = null;

		if (isNumeric) {

			returnMatcher = is(not(is(in(
					asList(value.split(COMMA)).stream().map(val -> parseValue(numericType, val)).collect(toList())))));

		} else {
			returnMatcher = is(not(is(in(asList(value.split(COMMA))))));
		}
		return returnMatcher;

	}

	private static Matcher<?> notEquals(String value, boolean isDate, boolean isBoolean, boolean isNumeric,
			NumericType numericType) {

		Matcher<?> returnMatcher = null;

		if (isNumeric) {
			returnMatcher = is(not(parseValue(numericType, value)));
		} else if (isDate) {
			returnMatcher = not(parseDate(value, null));
		} else if (isBoolean) {
			returnMatcher = not(parseBoolean(value));
		} else {
			returnMatcher = is(not(value));
		}
		return returnMatcher;
	}

	private static Matcher<?> equals(String value, boolean isDate, boolean isBoolean, boolean isNumeric,
			NumericType numericType) {

		Matcher<?> returnMatcher = null;

		if (isNumeric) {
			returnMatcher = is(parseValue(numericType, value));
		} else if (isDate) {
			returnMatcher = sameDay(parseDate(value, null));
		} else if (isBoolean) {
			returnMatcher = not(parseBoolean(value));
		} else {
			returnMatcher = is(value);
		}
		return returnMatcher;

	}

	private static Number parseValue(NumericType numericType, String value) {
		Number numericValue = null;
		try {
			switch (numericType) {
			case BYTE:
				numericValue = Byte.valueOf(value);
				break;
			case SHORT:
				numericValue = Short.valueOf(value);
				break;
			case INTEGER:
				numericValue = Integer.valueOf(value);
				break;
			case LONG:
				numericValue = Long.valueOf(value);
				break;
			case FLOAT:
				numericValue = Float.valueOf(value);
				break;
			case DOUBLE:
				numericValue = Double.valueOf(value);
				break;
			case BIGDECIMAL:
				numericValue = new BigDecimal(value);
				break;
			default:
				numericValue = Integer.valueOf(value);
				break;
			}
		} catch (Exception e) {
			LOGGER.debug(PROBLEM_WHILE_CONVERTING_TO_FORMAT_WITH_THE_ERROR, value, numericType,
					e.getMessage());
		}

		return numericValue;
	}

}
