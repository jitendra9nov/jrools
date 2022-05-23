/* (C) 2022 */
package com.jrools.rule.utils;

import static com.jrools.rule.constants.RuleConstants.ATTRIBUTE_BEAN_NAME;
import static com.jrools.rule.constants.RuleConstants.ATTRIBUTE_FOUND;
import static com.jrools.rule.constants.RuleConstants.ATTRIBUTE_NAME;
import static com.jrools.rule.constants.RuleConstants.CONTAINER_BEAN_NAME;
import static com.jrools.rule.constants.RuleConstants.CONTAINER_NAME;
import static com.jrools.rule.constants.RuleConstants.DATE_TIME_FORMAT;
import static com.jrools.rule.constants.RuleConstants.INSIDE_CONTAINER;
import static com.jrools.rule.constants.RuleConstants.ITERATE;
import static com.jrools.rule.utils.EntityUtil.findFromGeneric;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.asList;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jayway.jsonpath.JsonPath;
import com.jrools.rule.enums.AttributeType;
import com.jrools.rule.facts.EnumInfo;
import com.jrools.rule.facts.ExecutionInfo;
import com.jrools.rule.facts.MultiValueKey;
import com.jrools.rule.facts.Rule;
import com.jrools.rule.facts.RuleInfo;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * This is Utility class containing reusable utility methods .
 *
 * @author jitendrabhadouriya
 */
public class RuleUtil {

	private static final String FAILURE_ERROR = "Failed while parsing given Java value as a String";

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
			LOGGER.warn(RuleUtil.FAILURE_ERROR, e);
		}
		return readValue;
	}

	public static <T> T convert(Object obj, Class<T> type) {
		T readValue = null;
		try {
			readValue = MAPPER.readValue(writeValueAsString(obj, false), type);
		} catch (IOException e) {
			LOGGER.warn(RuleUtil.FAILURE_ERROR, e);
		}
		return readValue;
	}

	public static <T> T convert(String jsonString, TypeReference<T> type) {
		T readValue = null;
		try {
			readValue = MAPPER.readValue(jsonString, type);
		} catch (IOException e) {
			LOGGER.warn(RuleUtil.FAILURE_ERROR, e);
		}
		return readValue;
	}

	public static <T> T convert(String jsonString, Class<T> type) {
		T readValue = null;
		try {
			readValue = MAPPER.readValue(jsonString, type);
		} catch (IOException e) {
			LOGGER.warn(RuleUtil.FAILURE_ERROR, e);
		}
		return readValue;
	}

	public static <T> T merge(Object source, Object target, TypeReference<T> type) {
		T readValue = null;
		try {
			ObjectReader objectReader = MAPPER.readerForUpdating(target);
			readValue = objectReader.readValue(writeValueAsString(source, false));
		} catch (IOException e) {
			LOGGER.warn(RuleUtil.FAILURE_ERROR, e);
		}
		return readValue;
	}

	public static <T> T merge(Object source, Object target, Class<T> type) {
		T readValue = null;
		try {
			ObjectReader objectReader = MAPPER.readerForUpdating(target);
			readValue = objectReader.readValue(writeValueAsString(source, false));
		} catch (IOException e) {
			LOGGER.warn(RuleUtil.FAILURE_ERROR, e);
		}
		return readValue;
	}

	public static <T> T merge(String source, String target, TypeReference<T> type) {
		T readValue = null;
		try {
			ObjectReader objectReader = MAPPER.readerForUpdating(target);
			readValue = objectReader.readValue(source);
		} catch (IOException e) {
			LOGGER.warn(RuleUtil.FAILURE_ERROR, e);
		}
		return readValue;
	}

	public static <T> T merge(String source, String target, Class<T> type) {
		T readValue = null;
		try {
			ObjectReader objectReader = MAPPER.readerForUpdating(target);
			readValue = objectReader.readValue(source);
		} catch (IOException e) {
			LOGGER.warn(RuleUtil.FAILURE_ERROR, e);
		}
		return readValue;
	}

	@SuppressWarnings("rawtypes")
	public static TreeMap caseOrder(boolean ignoreCase) {

		TreeMap tree = null;
		if (ignoreCase) {
			tree = new TreeMap<>(nullsLast(CASE_INSENSITIVE_ORDER));
		} else {
			tree = new TreeMap<>(nullsLast(naturalOrder()));
		}
		return tree;
	}

	public static Date parseDate(String dateString, String format) {
		Date date = null;

		SimpleDateFormat formatter = new SimpleDateFormat(null != format ? format : DATE_TIME_FORMAT);

		try {
			date = formatter.parse(dateString);
		} catch (ParseException e) {
			LOGGER.warn(RuleUtil.FAILURE_ERROR, e);
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
		return Boolean.TRUE.equals(bool) ? "Y" : "N";
	}

	public static boolean booleanFromN(String yn) {
		return "Y".equalsIgnoreCase(yn);
	}

	public static Date addDays(Date targetDate, long numOfDays) {
		Date finalDate = targetDate;

		if (null != targetDate && numOfDays != 0) {

			LocalDate incrementedDate = targetDate.toInstant().atZone(systemDefault()).toLocalDate()
					.plusDays(numOfDays);
			finalDate = Date.from(incrementedDate.atStartOfDay().atZone(systemDefault()).toInstant());
		}

		return finalDate;

	}

	public static String replaceSlash(String source) {
		String replaces = source;
		if (null != replaces) {
			replaces = replaces.replace("\\\"", "");
		}
		return replaces;
	}

	public static Object enumCode(final Object attributeValue) {
		Object code = attributeValue;

		if (null != attributeValue) {
			if (attributeValue instanceof List) {
				code = getCodeList(attributeValue);
			} else {
				final Class<? extends Object> clazz = attributeValue.getClass();
				boolean anyFound = asList(clazz.getDeclaredFields()).stream()
						.anyMatch(fd -> attributeValue.toString().equalsIgnoreCase(fd.getName()));
				if (anyFound) {
					code = invoke(attributeValue, clazz, GET_CODE);
				}
			}
		}
		return code;
	}

	@SuppressWarnings("unchecked")
	public static <T> Map<Object, List<Object>> groupByField(final List<T> targetList, final String fieldName,
			final String collectorField, final boolean ignoreCase) {
		final Map<String, String> attributeMap = new HashMap<>();
		attributeMap.put(ATTRIBUTE_NAME, fieldName);

		final Map<String, String> collectorMap = new HashMap<>();
		collectorMap.put(ATTRIBUTE_NAME, collectorField);

		return targetList.stream().collect(groupingBy(obj -> {
			attributeMap.remove(ATTRIBUTE_FOUND);
			attributeMap.remove(INSIDE_CONTAINER);
			return findFromGeneric(obj, new HashSet<>(), obj.getClass().getGenericSuperclass(), attributeMap);

		}, () -> caseOrder(ignoreCase), mapping(obj -> {
			final boolean hasText = hasText(collectorField);
			if (hasText) {
				collectorMap.remove(ATTRIBUTE_FOUND);
				collectorMap.remove(INSIDE_CONTAINER);
			}
			return hasText ? findFromGeneric(obj, new HashSet<>(), obj.getClass().getGenericSuperclass(), collectorMap)
					: obj;
		}, toList()))

		);

	}

	public static <T> List<Object> distinctByField(final List<T> targetList, final String fieldName,
			final String collectorField, final boolean ignoreCase) {
		final Map<String, String> attributeMap = new HashMap<>();
		attributeMap.put(ATTRIBUTE_NAME, fieldName);

		final Map<String, String> collectorMap = new HashMap<>();
		collectorMap.put(ATTRIBUTE_NAME, collectorField);

		return targetList.stream().filter(distinctByKey(obj -> {
			attributeMap.remove(ATTRIBUTE_FOUND);
			attributeMap.remove(INSIDE_CONTAINER);
			final Object findFieldByName = findFromGeneric(obj, new HashSet<>(), obj.getClass().getGenericSuperclass(),
					attributeMap);

			return ignoreCase ? findFieldByName.toString().toLowerCase() : findFieldByName;

		})).collect(mapping(obj -> {
			final boolean hasText = hasText(collectorField);
			if (hasText) {
				collectorMap.remove(ATTRIBUTE_FOUND);
				collectorMap.remove(INSIDE_CONTAINER);
			}
			return hasText ? findFromGeneric(obj, new HashSet<>(), obj.getClass().getGenericSuperclass(), collectorMap)
					: obj;
		}, toList()));

	}

	public static <T> Map<Object, T> filterMapByKey(final Map<Object, T> targetMap, final Rule rule) {

		return targetMap.entrySet().stream().filter(map -> {
			Object obj = map.getKey();
			if (obj instanceof MultiValueKey) {
				obj = asList(((MultiValueKey) obj).getValues());

			}
			if (AttributeType.ENUM == rule.getAttributeType()) {
				obj = enumCode(obj);
			}

			boolean found = false;
			if (obj instanceof List) {
				found = ((List<?>) obj).stream().anyMatch(o -> rule.getRuleMatcher().getMatcher().matches(o));
			} else {
				found = rule.getRuleMatcher().getMatcher().matches(obj);
			}
			return found;
		}

		).collect(toMap(Entry::getKey, Entry::getValue));
	}

	@SuppressWarnings("unchecked")
	public static <T> Map<Object, List<Object>> groupByFieldWithNullKeys(final List<T> targetList,
			final boolean ignoreCase, Map<String, String> attributeMap, Map<String, String> collectorMap) {

		notNull(attributeMap, "attributeMap can not be null");

		return targetList.stream().collect(toMap(obj -> {
			attributeMap.remove(ATTRIBUTE_FOUND);
			attributeMap.remove(INSIDE_CONTAINER);
			final Object findFromGeneric = findFromGeneric(obj, new HashSet<>(), obj.getClass().getGenericSuperclass(),
					attributeMap);

			return (findFromGeneric instanceof List) ? new MultiValueKey(((List<Object>) findFromGeneric).toArray())
					: findFromGeneric;
		}, x -> {
			List<Object> list = new ArrayList<>();
			final boolean empty = CollectionUtils.isEmpty(collectorMap);
			if (!empty) {
				collectorMap.remove(ATTRIBUTE_FOUND);
				collectorMap.remove(INSIDE_CONTAINER);
			}
			list.add(
					empty ? x : findFromGeneric(x, new HashSet<>(), x.getClass().getGenericSuperclass(), collectorMap));
			return list;
		}, (List<Object> oldList, List<Object> newL) -> {
			List<Object> newList = new ArrayList<>(oldList.size() + 1);
			newList.addAll(oldList);
			newList.addAll(newL);
			return newList;
		}

				, () -> caseOrder(ignoreCase)));

	}

	@SuppressWarnings("unchecked")
	public static <T> Map<Object, List<T>> groupByFieldWithNullKeys(List<T> targetList, String fieldName,
			boolean ignoreCase) {

		final Map<String, String> attributeMap = new HashMap<>();
		attributeMap.put(ATTRIBUTE_NAME, fieldName);

		return targetList.stream().collect(toMap(obj -> {
			attributeMap.remove(ATTRIBUTE_FOUND);
			attributeMap.remove(INSIDE_CONTAINER);

			return findFromGeneric(obj, new HashSet<>(), obj.getClass().getGenericSuperclass(), attributeMap);
		}, x -> {
			List<Object> list = new ArrayList<>();

			list.add(x);
			return list;
		}, (List<Object> oldList, List<Object> newL) -> {
			List<Object> newList = new ArrayList<>(oldList.size() + 1);
			newList.addAll(oldList);
			newList.addAll(newL);
			return newList;
		}

				, () -> caseOrder(ignoreCase))

		);

	}

	public static void applyPattern(ExecutionInfo info) {

		AttributeType type = info.getAttributeType();

		if (null != type) {
			switch (type) {
			case STRING:
				stringPattern(info, info.getPattern());

				break;

			case DATE:
				datePattern(info, info.getPattern());

				break;

			case ENUM:
				enumPattern(info);

				break;

			case OBJECT:
				info.setValue(null != info.getValue() ? info.getValue().toString() : info.getValue());

				break;

			case COLLECTION:
				info.setValue(null != info.getValue() ? info.getValue().toString() : info.getValue());

				break;

			default:
				break;
			}
		}

	}

	public static Object findFieldValue(RuleInfo ruleInfo, boolean isJsonPath, Object targetObject) {
		Object obj = null;

		String attribute = (null != ruleInfo.getRule().getBusinessAttribute())
				? ruleInfo.getRule().getBusinessAttribute()
				: ruleInfo.getRule().getAttribute();

		try {

			if (isJsonPath) {
				obj = JsonPath.read(targetObject, attribute);
			} else {
				final Map<String, String> attributeMap = new HashMap<>();
				attributeMap.put(ATTRIBUTE_NAME, attribute);
				attributeMap.put(ATTRIBUTE_BEAN_NAME, ruleInfo.getRule().getAttributeBeanName());
				attributeMap.put(CONTAINER_BEAN_NAME, ruleInfo.getRule().getContainerBeanName());
				attributeMap.put(CONTAINER_NAME, ruleInfo.getRule().getContainerName());
				attributeMap.put(ITERATE, ruleInfo.getRule().getIterate().val());

				obj = findFromGeneric(targetObject, new HashSet<>(), targetObject.getClass().getGenericSuperclass(),
						attributeMap);

			}
			LOGGER.debug("attribute path[{}], value [{}]", attribute, obj);

		} catch (Exception e) {
			LOGGER.warn("Error Reading attribute path[{}], value [{}]", attribute, obj);
		}

		return obj;

	}

	private static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();

		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE == null);
	}

	private static void enumPattern(ExecutionInfo info) {
		Object attributeValue = info.getValue();

		if (null != attributeValue) {

			if (attributeValue instanceof List) {
				enumList(info, attributeValue);
			} else {
				EnumInfo enumInfo = populateEnumInfo(attributeValue);
				if (null != enumInfo && null != enumInfo.getCode()) {
					info.setValue(enumInfo.getCode());
				}
				info.setEnumInfo(enumInfo);
			}

		}

	}

	private static void enumList(ExecutionInfo info, Object attributeValue) {
		List<Object> enms = new ArrayList<>();
		List<Object> values = new ArrayList<>();

		((List<?>) attributeValue).forEach(action -> {
			EnumInfo enumInfo = populateEnumInfo(attributeValue);
			if (null != enumInfo) {
				enms.add(enumInfo);
			}
		});

		if (!isEmpty(enms)) {
			info.setEnumInfo(enms);

			enms.forEach(enumInfo -> {
				if (null != ((EnumInfo) enumInfo).getCode()) {
					info.setValue(((EnumInfo) enumInfo).getCode());
				}
			});

			info.setValue(values);

		}

	}

	private static EnumInfo populateEnumInfo(Object action) {

		Class<? extends Object> clazz = action.getClass();

		return asList(clazz.getDeclaredFields()).stream().filter(fd -> action.toString().equalsIgnoreCase(fd.getName()))
				.findFirst().map(fd -> {
					EnumInfo enm = new EnumInfo();

					enm.setCode(invoke(action, clazz, GET_CODE));
					enm.setDescription(invoke(action, clazz, GET_DESC));
					enm.setName(fd.getName());
					return enm;
				}).orElse(null);
	}

	private static Object getCodeList(Object attributeValue) {
		Object code = attributeValue;

		List<Object> codes = new ArrayList<>();

		((List<?>) attributeValue).forEach(action -> {
			Class<? extends Object> clazz = action.getClass();

			boolean anyFound = asList(clazz.getDeclaredFields()).stream()
					.anyMatch(fd -> action.toString().equalsIgnoreCase(fd.getName()));
			if (anyFound) {
				codes.add(invoke(action, clazz, GET_CODE));
			}

		});

		if (!isEmpty(codes)) {
			code = codes;
		}

		return code;

	}

	private static void datePattern(ExecutionInfo info, String pattern) {

		Object attributeValue = info.getValue();

		if (null != attributeValue) {
			
			String formattedValue=attributeValue.toString();
			
			if(null!=pattern) {
				try {
					SimpleDateFormat fmt=new SimpleDateFormat(pattern);
					formattedValue=fmt.format(attributeValue);
				} catch (Exception e) {
					LOGGER.debug("Problem while formatting {} date to {} format with error ::{}",attributeValue,pattern,e.getMessage());
				}
			}
			
			info.setValue(formattedValue);
		}

	}

	private static void stringPattern(ExecutionInfo info, String pattern) {
		
		if(null!=pattern) {
			info.setValue(StringUtils.defaultString((String)info.getValue()));
		}

	}
	@SuppressWarnings("all")
	private static String invoke(Object instance, Class<? extends Object> clazz, String methodName) {
		
		String result=null; 
		
		
		try {
			
			Method m1=clazz.getMethod(methodName, null);
			
			result=(String)m1.invoke(instance, null);
			
		} catch (Exception e) {
			LOGGER.debug("Problem invoking {} method of class [{}] on Instance [{}], Error :: {}",methodName,clazz,instance,e.getMessage());
		}
		
		return result;
	}

	private RuleUtil() {

		throw new IllegalStateException("Utility class should not be instanciated");
	}
}
