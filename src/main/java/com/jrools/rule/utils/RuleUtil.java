/* (C) 2022 */
package com.jrools.rule.utils;

import static com.jrools.rule.constants.RuleConstants.DATE_TIME_FORMAT;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jrools.rule.facts.ExecutionInfo;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is Utility class containing reusable utility methods .
 *
 * @author jitendrabhadouriya
 */
public class RuleUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RuleUtil.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final String GET_CODE = "getCode";

	private static final String GET_DESC = "getDescription";

	/**
	 * 
	 * 
	 * /** This method converts Java Beans into Json String by serialising the
	 * object
	 *
	 * @param obj      - object to be converted into json string
	 * @param isPretty - true if want to format json; else false
	 * @return
	 */
	public static String writeValueAsString(final Object obj, final boolean isPretty) {

		String stringJson = null;

		try {
			final ObjectWriter objWriter = isPretty ? MAPPER.writerWithDefaultPrettyPrinter() : MAPPER.writer();

			stringJson = objWriter.writeValueAsString(obj);
		} catch (final JsonProcessingException e) {
			LOGGER.warn("Failed while serializing given Java value as a String", e);
		}
		return stringJson;
	}

	/**
	 * @param urlString
	 * @return
	 */
	public static boolean isValidURL(String urlString) {
		try {
			URL url = new URL(urlString);
			url.toURI();
			return true;
		} catch (Exception e) {

			LOGGER.warn("Provided urlString {} is not a valid URL", urlString);
			return false;
		}
	}

	public static ObjectMapper getMapper() {
		return MAPPER;
	}

	public static <T> T convert(Object obj, TypeReference<T> type) {
		T readValue = null;
		try {
			readValue = MAPPER.readValue(writeValueAsString(obj, false), type);
		} catch (IOException e) {
			LOGGER.warn("Failed while parsing given Java value as a String", e);
		}
		return readValue;
	}

	public static <T> T convert(Object obj, Class<T> type) {
		T readValue = null;
		try {
			readValue = MAPPER.readValue(writeValueAsString(obj, false), type);
		} catch (IOException e) {
			LOGGER.warn("Failed while parsing given Java value as a String", e);
		}
		return readValue;
	}

	public static <T> T convert(String jsonString, TypeReference<T> type) {
		T readValue = null;
		try {
			readValue = MAPPER.readValue(jsonString, type);
		} catch (IOException e) {
			LOGGER.warn("Failed while parsing given Java value as a String", e);
		}
		return readValue;
	}

	public static <T> T convert(String jsonString, Class<T> type) {
		T readValue = null;
		try {
			readValue = MAPPER.readValue(jsonString, type);
		} catch (IOException e) {
			LOGGER.warn("Failed while parsing given Java value as a String", e);
		}
		return readValue;
	}

	public static <T> T merge(Object source, Object target, TypeReference<T> type) {
		T readValue = null;
		try {
			ObjectReader objectReader = MAPPER.readerForUpdating(target);
			readValue = objectReader.readValue(writeValueAsString(source, false));
		} catch (IOException e) {
			LOGGER.warn("Failed while parsing given Java value as a String", e);
		}
		return readValue;
	}

	public static <T> T merge(Object source, Object target, Class<T> type) {
		T readValue = null;
		try {
			ObjectReader objectReader = MAPPER.readerForUpdating(target);
			readValue = objectReader.readValue(writeValueAsString(source, false));
		} catch (IOException e) {
			LOGGER.warn("Failed while parsing given Java value as a String", e);
		}
		return readValue;
	}

	public static <T> T merge(String source, String target, TypeReference<T> type) {
		T readValue = null;
		try {
			ObjectReader objectReader = MAPPER.readerForUpdating(target);
			readValue = objectReader.readValue(source);
		} catch (IOException e) {
			LOGGER.warn("Failed while parsing given Java value as a String", e);
		}
		return readValue;
	}

	public static <T> T merge(String source, String target, Class<T> type) {
		T readValue = null;
		try {
			ObjectReader objectReader = MAPPER.readerForUpdating(target);
			readValue = objectReader.readValue(source);
		} catch (IOException e) {
			LOGGER.warn("Failed while parsing given Java value as a String", e);
		}
		return readValue;
	}

	public static TreeMap caseOrder(boolean ignoreCase) {

		TreeMap tree = null;
		if (ignoreCase) {
			tree = new TreeMap<>(nullsLast(CASE_INSENSITIVE_ORDER));
		} else {
			tree = new TreeMap<>(nullsLast(naturalOrder()));
		}
		return tree;
	}

	public static Date parsedate(String dateString, String format) {
		Date date = null;

		SimpleDateFormat formatter = new SimpleDateFormat(null != format ? format : DATE_TIME_FORMAT);

		try {
			date = formatter.parse(dateString);
		} catch (ParseException e) {
			LOGGER.warn("Failed while parsing given Java value as a String", e);
		}
		return date;
	}

	public static String formatDate(Date date, String format) {
		String dateString = null;

		if (null != date) {
			dateString = new SimpleDateFormat(null != format ? format : DATE_TIME_FORMAT).format(date);
		}

		return dateString;
	}
	
	public static String toStringYN(Boolean bool) {
		return Boolean.TRUE.equals(bool)?"Y":"N";
	}
	
	public static boolean booleanFromN(String yn) {
		return "Y".equalsIgnoreCase(yn);
	}

	private RuleUtil() {

		throw new IllegalStateException("Utility class should not be instanciated");
	}

	public static Map<Object, List<ExecutionInfo>> groupByFieldWithNullKeys(List<ExecutionInfo> result,
			String fieldName, boolean ignoreCaseForString) {
		// TODO Auto-generated method stub
		return null;
	}
}
