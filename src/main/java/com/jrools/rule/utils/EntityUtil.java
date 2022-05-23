/* (C) 2022 */
package com.jrools.rule.utils;

import static com.jrools.rule.constants.RuleConstants.ATTRIBUTE_BEAN_NAME;
import static com.jrools.rule.constants.RuleConstants.ATTRIBUTE_FOUND;
import static com.jrools.rule.constants.RuleConstants.ATTRIBUTE_NAME;
import static com.jrools.rule.constants.RuleConstants.CONTAINER_BEAN_NAME;
import static com.jrools.rule.constants.RuleConstants.CONTAINER_NAME;
import static com.jrools.rule.constants.RuleConstants.INSIDE_CONTAINER;
import static com.jrools.rule.constants.RuleConstants.ITERATE;
import static com.jrools.rule.enums.Iterate.ANY;
import static com.jrools.rule.enums.Iterate.DEFAULT;
import static com.jrools.rule.enums.Iterate.form;
import static java.lang.Boolean.parseBoolean;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static org.springframework.beans.BeanUtils.isSimpleValueType;
import static org.springframework.util.Assert.notNull;

import com.jrools.rule.enums.Iterate;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityUtil {

	private EntityUtil() {

		throw new IllegalStateException("Utility class should not be instanciated");
	}

	private static final String ERROR_OCCURRED_WITH_MESSAGE = "Method {}: Problem occurred while checking root {} for {} with message {} ";

	private static final Logger LOGGER = LoggerFactory.getLogger(EntityUtil.class);

	private static final List<Class<?>> EXCLUSION_CLASS = new ArrayList<>();

	static {
		EXCLUSION_CLASS.add(Object.class);
		EXCLUSION_CLASS.add(org.slf4j.Logger.class);
		EXCLUSION_CLASS.add(ch.qos.logback.classic.Logger.class);
	}

	public static Object findFromGeneric(Object fieldValue, Set<Object> inspected, Type listType,
			Map<String, String> attributeMap) {

		Object fieldValueInCollection = null;

		notNull(attributeMap, "attributeMap can't be null");

		try {
			if (listType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) listType;

				Type keyType = pt;
				if (pt.getActualTypeArguments().length != 0) {
					keyType = pt.getActualTypeArguments()[0];
				}
				if (pt.getRawType().equals(Map.class) || pt.getRawType().equals(AbstractMap.class)
						|| pt.getRawType().equals(HashMap.class) || pt.getRawType().equals(LinkedHashMap.class)) {
					fieldValueInCollection = findInMap(fieldValue, inspected, pt, attributeMap);
				} else if (pt.getRawType().equals(List.class) || pt.getRawType().equals(AbstractList.class)
						|| pt.getRawType().equals(ArrayList.class) || pt.getRawType().equals(Set.class)
						|| pt.getRawType().equals(AbstractSet.class) || pt.getRawType().equals(HashSet.class)
						|| pt.getRawType().equals(LinkedHashSet.class)) {
					fieldValueInCollection = findInListOrSet(fieldValue, inspected, keyType, attributeMap);
				} else {
					fieldValueInCollection = findDeepField(fieldValue, inspected, attributeMap);
				}

			} else {
				fieldValueInCollection = findDeepField(fieldValue, inspected, attributeMap);
			}

		} catch (IllegalArgumentException | NoSuchMethodException | InvocationTargetException
				| IllegalAccessException e) {
			LOGGER.debug("Problem Reading attribute [{}] , value [{}], Error :: {}", attributeMap.get(ATTRIBUTE_NAME),
					fieldValue, e.getMessage());
		}
		return fieldValueInCollection;
	}

	@SuppressWarnings("all")
	private static Object findInListOrSet(Object fieldValue, Set<Object> inspected, Type keyType,
			Map<String, String> attributeMap)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		Object container = getContainer(attributeMap, ((Collection<?>) fieldValue));

		Method m = fieldValue.getClass().getMethod("iterator", null);

		Iterator<Object> iter = (Iterator<Object>) m.invoke(fieldValue, null);
		return iterateCollection(null, iter, inspected, keyType, attributeMap, container);
	}

	@SuppressWarnings("all")
	private static Object findInMap(Object fieldValue, Set<Object> inspected, ParameterizedType pt,
			Map<String, String> attributeMap)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		Object container = getContainer(attributeMap, ((Map<?, ?>) fieldValue).values());

		Type valueType = pt.getActualTypeArguments()[1];

		Method m = fieldValue.getClass().getMethod("entrySet", null);

		Set<Map.Entry<Object, Object>> entrySet = (Set<Entry<Object, Object>>) m.invoke(fieldValue, null);

		Iterator<Entry<Object, Object>> iter = entrySet.iterator();

		return iterateCollection(iter, null, inspected, valueType, attributeMap, container);
	}

	private static Object getContainer(Map<String, String> attributeMap, Collection<?> collection) {
		Object container = null;
		boolean isOtherClass = collection.stream().anyMatch(element -> nonNull(element)
				&& !element.getClass().getCanonicalName().equals(attributeMap.get(CONTAINER_BEAN_NAME)));

		if (isOtherClass) {
			container = collection.stream()
					.filter(element -> nonNull(element)
							&& !element.getClass().getCanonicalName().equals(attributeMap.get(CONTAINER_BEAN_NAME)))
					.findFirst().orElse(null);
		}

		return container;
	}

	private static Object iterateCollection(Iterator<Entry<Object, Object>> mapIterator, Iterator<Object> iter,
			Set<Object> inspected, Type collType, Map<String, String> attributeMap, Object container) {
		Object fieldValueInCollection = null;
		Iterate iterate = null != container ? DEFAULT : ANY;
		iterate = (iterate == ANY && attributeMap.containsKey(ITERATE)) ? form(attributeMap.get(ITERATE)) : iterate;

		switch (iterate) {
		case FIRST:
			fieldValueInCollection = firstElement(mapIterator, iter, inspected, collType, attributeMap);
			break;
		case LAST:
			fieldValueInCollection = lastlement(mapIterator, iter, inspected, collType, attributeMap);
			break;
		case ANY:
			fieldValueInCollection = anyElements(mapIterator, iter, inspected, collType, attributeMap);
			break;
		case ALL:
			fieldValueInCollection = allElements(mapIterator, iter, inspected, collType, attributeMap);
			break;
		default:
			fieldValueInCollection = findFromGeneric(container, inspected, collType, attributeMap);
			break;
		}

		return fieldValueInCollection;
	}

	private static Object anyElements(Iterator<Entry<Object, Object>> mapIterator, Iterator<Object> iter,
			Set<Object> inspected, Type collType, Map<String, String> attributeMap) {
		Object fieldValueInCollection = null;
		String attributeName = attributeMap.get(ATTRIBUTE_NAME);

		if (null != mapIterator) {
			fieldValueInCollection = mapAnyElement(mapIterator, inspected, collType, attributeMap, attributeName);
		} else {
			fieldValueInCollection = collectionAnyElement(iter, inspected, collType, attributeMap);
		}

		return fieldValueInCollection;
	}

	private static Object allElements(Iterator<Entry<Object, Object>> mapIterator, Iterator<Object> iter,
			Set<Object> inspected, Type collType, Map<String, String> attributeMap) {
		Object fieldValueInCollection = null;

		String attributeName = attributeMap.get(ATTRIBUTE_NAME);

		if (null != mapIterator) {
			fieldValueInCollection = mapAllElement(mapIterator, inspected, collType, attributeMap, attributeName);
		} else {
			fieldValueInCollection = collectionAllElement(iter, inspected, collType, attributeMap);
		}
		return fieldValueInCollection;
	}

	private static Object collectionAnyElement(Iterator<Object> iter, Set<Object> inspected, Type collType,
			Map<String, String> attributeMap) {
		Object fieldValueInCollection = null;

		while (iter.hasNext()) {
			boolean isBreak = false;

			Object next = iter.next();

			try {

				fieldValueInCollection = findFromGeneric(next, inspected, collType, attributeMap);
				isBreak = (null != fieldValueInCollection || parseBoolean(attributeMap.get(ATTRIBUTE_FOUND)));

			} catch (Exception e) {
				LOGGER.debug(ERROR_OCCURRED_WITH_MESSAGE, "collectionAnyElement", next,
						attributeMap.get(ATTRIBUTE_NAME), e.getMessage());
				isBreak = true;
			}
			if (isBreak) {
				break;
			}

		}
		return fieldValueInCollection;
	}

	private static Object mapAnyElement(Iterator<Entry<Object, Object>> mapIterator, Set<Object> inspected,
			Type collType, Map<String, String> attributeMap, String attributeName) {
		Object fieldValueInCollection = null;

		while (mapIterator.hasNext()) {
			boolean isBreak = false;

			Entry<Object, Object> entry = mapIterator.next();

			try {
				fieldValueInCollection = entry.getValue();
				if (attributeName.equals(entry.getKey())) {
					isBreak = true;
				} else if (null != fieldValueInCollection) {

					fieldValueInCollection = findFromGeneric(fieldValueInCollection, inspected, collType, attributeMap);
					isBreak = (null != fieldValueInCollection);
				}

			} catch (Exception e) {
				LOGGER.debug(ERROR_OCCURRED_WITH_MESSAGE, "mapAnyElement", entry, attributeMap.get(ATTRIBUTE_NAME),
						e.getMessage());
				isBreak = true;
			}
			if (isBreak) {
				break;
			}

		}
		return fieldValueInCollection;
	}

	private static Object collectionAllElement(Iterator<Object> iter, Set<Object> inspected, Type collType,
			Map<String, String> attributeMap) {
		List<Object> fieldValueInCollections = null;

		while (iter.hasNext()) {
			Object fieldValueInCollection = null;

			Object next = iter.next();

			try {

				fieldValueInCollection = findFromGeneric(next, inspected, collType, attributeMap);

			} catch (Exception e) {
				LOGGER.debug(ERROR_OCCURRED_WITH_MESSAGE, "collectionAnyElement", next,
						attributeMap.get(ATTRIBUTE_NAME), e.getMessage());
			}
			if (null != fieldValueInCollection) {

				if (null == fieldValueInCollections) {
					fieldValueInCollections = new ArrayList<>();

				}
				if (fieldValueInCollection instanceof List) {
					fieldValueInCollections.addAll((List<?>) fieldValueInCollection);
				} else {
					fieldValueInCollections.add(fieldValueInCollection);
				}
				if (parseBoolean(attributeMap.get(ATTRIBUTE_FOUND))) {
					attributeMap.remove(ATTRIBUTE_FOUND);
				}
			}

		}
		return fieldValueInCollections;
	}

	private static Object mapAllElement(Iterator<Entry<Object, Object>> mapIterator, Set<Object> inspected,
			Type collType, Map<String, String> attributeMap, String attributeName) {

		List<Object> fieldValueInCollections = null;

		while (mapIterator.hasNext()) {

			Entry<Object, Object> entry = mapIterator.next();

			Object fieldValueInCollection = null;

			try {
				fieldValueInCollection = entry.getValue();
				if (!attributeName.equals(entry.getKey()) && null != fieldValueInCollection) {
					fieldValueInCollection = findFromGeneric(fieldValueInCollection, inspected, collType, attributeMap);
				}

			} catch (Exception e) {
				LOGGER.debug(ERROR_OCCURRED_WITH_MESSAGE, "mapAllElement", entry, attributeMap.get(ATTRIBUTE_NAME),
						e.getMessage());
			}
			if (null != fieldValueInCollection) {

				if (null == fieldValueInCollections) {
					fieldValueInCollections = new ArrayList<>();

				}
				if (fieldValueInCollection instanceof List) {
					fieldValueInCollections.addAll((List<?>) fieldValueInCollection);
				} else {
					fieldValueInCollections.add(fieldValueInCollection);
				}
				if (parseBoolean(attributeMap.get(ATTRIBUTE_FOUND))) {
					attributeMap.remove(ATTRIBUTE_FOUND);
				}
			}

		}
		return fieldValueInCollections;

	}

	private static Object firstElement(Iterator<Entry<Object, Object>> mapIterator, Iterator<Object> iter,
			Set<Object> inspected, Type collType, Map<String, String> attributeMap) {
		Object fieldValueInCollection = null;
		String attributeName = attributeMap.get(ATTRIBUTE_NAME);

		try {
			Object element = null;
			boolean foundInKey = false;
			if (null != mapIterator) {
				Entry<Object, Object> entry = mapIterator.next();
				element = fieldValueInCollection = entry.getValue();
				foundInKey = attributeName.equals(entry.getKey());

			} else {
				element = iter.next();
			}

			if (null != iter || !foundInKey) {
				fieldValueInCollection = findFromGeneric(element, inspected, collType, attributeMap);
			}

		} catch (Exception e) {
			LOGGER.debug(ERROR_OCCURRED_WITH_MESSAGE, "firstElement", iter, attributeName, e.getMessage());
		}
		return fieldValueInCollection;
	}

	private static Object lastlement(Iterator<Entry<Object, Object>> mapIterator, Iterator<Object> iter,
			Set<Object> inspected, Type collType, Map<String, String> attributeMap) {

		Object fieldValueInCollection = null;
		String attributeName = attributeMap.get(ATTRIBUTE_NAME);

		try {
			Object element = null;
			boolean foundInKey = false;
			if (null != mapIterator) {
				Entry<Object, Object> entry = null;
				do {
					entry = mapIterator.next();

				} while (mapIterator.hasNext());
				element = fieldValueInCollection = entry.getValue();
				foundInKey = attributeName.equals(entry.getKey());

			} else {
				do {
					element = iter.next();
				} while (iter.hasNext());

			}

			if (null != iter || !foundInKey) {
				fieldValueInCollection = findFromGeneric(element, inspected, collType, attributeMap);
			}

		} catch (Exception e) {
			LOGGER.debug(ERROR_OCCURRED_WITH_MESSAGE, "lastlement", iter, attributeName, e.getMessage());
		}
		return fieldValueInCollection;

	}

	private static Iterable<Field> gatherField(Class<?> clazz) {
		// find All fields, even the ones from super classes

		List<Field> fields = new ArrayList<>();

		Class<?> searchType = clazz;

		while (searchType != null && !isExclusion(searchType)) {
			fields.addAll(asList(searchType.getDeclaredFields()));
			searchType = searchType.getSuperclass();
		}

		return fields;

	}

	private static <T> Object checkFieldByName(T root, Map<String, String> attributeMap) {
		String fieldName = attributeMap.get(ATTRIBUTE_NAME);
		String attributeBeanName = attributeMap.get(ATTRIBUTE_BEAN_NAME);

		Object fieldValue = null;

		Class<?> searchType = root.getClass();

		if (null == attributeBeanName || (parseBoolean(attributeMap.get(INSIDE_CONTAINER)) && null != searchType
				&& searchType.getCanonicalName().equals(attributeBeanName))) {
			fieldValue = getValueOfField(root, fieldName, searchType);
		}

		// serach super type
		if (null == fieldValue) {

			searchType = searchType != null ? searchType.getSuperclass() : searchType;

			if (null == attributeBeanName || (parseBoolean(attributeMap.get(INSIDE_CONTAINER)) && null != searchType
					&& searchType.getCanonicalName().equals(attributeBeanName))) {
				fieldValue = getValueOfField(root, fieldName, searchType);
			}

		}
		return fieldValue;
	}

	private static <T> Object getValueOfField(T root, String fieldName, Class<?> searchType) {
		Object fieldValue = null;

		if (searchType != null && !isExclusion(searchType)) {
			Field field = null;

			try {
				field = searchType.getDeclaredField(fieldName);

				field.setAccessible(true);
				fieldValue = field.get(root);
				field.setAccessible(false);

				LOGGER.debug("Method {}: Found attribute {} while checking root {} in field {} with value {}",
						"getValueOfField", fieldName, root, field, fieldValue);

			} catch (Exception e) {

				if (null != field) {
					field.setAccessible(false);
				}
				LOGGER.debug(ERROR_OCCURRED_WITH_MESSAGE, "getValueOfField", root, fieldName, e.getMessage());
			}

		}
		return fieldValue;
	}

	private static boolean isExclusion(Class<?> searchType) {
		return (EXCLUSION_CLASS.contains(searchType) || isJavaOrSdkClass(searchType));
	}

	private static boolean isExclusionClasses(Class<?> searchType) {
		return (EXCLUSION_CLASS.contains(searchType));
	}

	private static boolean isJavaOrSdkClass(Class<?> searchType) {
		return (null != searchType.getPackage() && (searchType.getPackage().getName().startsWith("org.hibernate"))
				|| searchType.getPackage().getName().startsWith("org.springframework")
				|| searchType.getPackage().getName().startsWith("javax.persistance")
				|| searchType.getPackage().getName().startsWith("java.lang")
				|| searchType.getPackage().getName().startsWith("java.util"));
	}

	private static <T> Object findDeepField(T root, Set<Object> inspected, Map<String, String> attributeMap)
			throws IllegalAccessException {

		Object fieldValue = null;

		if (null != root) {
			Object unproxy = root;
			try {
				// unproxy=Hibernate.unproxy(unproxy);
				if (inspected.contains(unproxy)) {// prevents stack overflow
					return fieldValue;
				}

			} catch (Exception e) {
				LOGGER.debug(ERROR_OCCURRED_WITH_MESSAGE, "findDeepField", unproxy, attributeMap.get(ATTRIBUTE_NAME),
						e.getMessage());
				return fieldValue;
			}

			inspected.add(unproxy);

			fieldValue = checkFieldByName(unproxy, attributeMap);

			if (null == fieldValue) {
				fieldValue = checkAllFields(unproxy, inspected, attributeMap);
			}
		}

		return fieldValue;
	}

	private static <T> Object checkAllFields(T root, Set<Object> inspected, Map<String, String> attributeMap)
			throws IllegalAccessException {

		Object fieldValue = null;

		for (Field field : gatherField(root.getClass())) {
			boolean isBreak = false;

			field.setAccessible(true);

			fieldValue = field.get(root);

			String attributeName = attributeMap.get(ATTRIBUTE_NAME);

			if (isAttributeFound(root, fieldValue, field, attributeMap)) {
				LOGGER.debug("Found attribute {} while checking root {} in field {} with value {}", attributeName, root,
						field, fieldValue);
				isBreak = true;
			} else if (isFurtherProcessing(root, fieldValue, field, attributeMap)) {

				if (root.getClass().getCanonicalName().equals(attributeMap.get(CONTAINER_BEAN_NAME))) {
					attributeMap.put(INSIDE_CONTAINER, Boolean.toString(true));
				}

				fieldValue = findFromGeneric(fieldValue, inspected, field.getGenericType(), attributeMap);

				if (notFound(attributeMap, fieldValue)) {
					field.setAccessible(false);
					fieldValue = findDeepField(root, inspected, attributeMap);
				}

				if (found(attributeMap, fieldValue)) {
					isBreak = true;
				}
				if (root.getClass().getCanonicalName().equals(attributeMap.get(CONTAINER_BEAN_NAME))) {
					attributeMap.remove(INSIDE_CONTAINER);
				}

			}
			if (isBreak) {
				field.setAccessible(false);
				attributeMap.put(ATTRIBUTE_FOUND, Boolean.toString(true));
				break;
			}
			fieldValue = null;

		}
		return fieldValue;
	}

	private static boolean found(Map<String, String> attributeMap, Object fieldValue) {
		return null != fieldValue
				|| ((null == attributeMap.get(CONTAINER_BEAN_NAME) || parseBoolean(attributeMap.get(INSIDE_CONTAINER)))
						&& parseBoolean(attributeMap.get(ATTRIBUTE_FOUND)));
	}

	private static boolean notFound(Map<String, String> attributeMap, Object fieldValue) {
		return null != fieldValue && !parseBoolean(attributeMap.get(ATTRIBUTE_FOUND));
	}

	private static <T> boolean isFurtherProcessing(T root, Object fieldValue, Field field,
			Map<String, String> attributeMap) {

		String containerName = attributeMap.get(CONTAINER_NAME);
		String containerBeanName = attributeMap.get(CONTAINER_BEAN_NAME);
		return (null != fieldValue && null != fieldValue.getClass() && !isSimpleValueType(fieldValue.getClass())
				&& !isExclusionClasses(fieldValue.getClass())
				&& (null == containerBeanName || !root.getClass().getCanonicalName().equals(containerBeanName)
						|| (root.getClass().getCanonicalName().equals(containerBeanName)
								&& (((fieldValue instanceof Map) && ((Map<?, ?>) fieldValue).containsKey(containerName))
										|| (field.getName().equals(containerName))))));
	}

	private static <T> boolean isAttributeFound(T root, Object fieldValue, Field field,
			Map<String, String> attributeMap) {
		String attributeName = attributeMap.get(ATTRIBUTE_NAME);
		String containerName = attributeMap.get(CONTAINER_NAME);
		String containerBeanName = attributeMap.get(CONTAINER_BEAN_NAME);

		return ((null == containerBeanName || null == containerName) && attributeName.equals(field.getName()))
				|| (null == fieldValue && (root.getClass().getCanonicalName().equals(containerBeanName)
						&& field.getName().equals(containerName)));
	}

}
