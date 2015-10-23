/**
 * 
 */
package com.yoju360.mgmt.core.model;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.yoju360.mgmt.core.util.ReflectionUtils;

/**
 * Help search by example.
 * Relation - 'AND'， 'OR'.
 * 
 * @author evan
 *
 */
public class ModelExampleHelper {
	
	public static final String LIKE = "Like";
	public static final String EQ = "EqualTo";
	public static final String BETWEEN = "Between";
	public static final String GT = "GreaterThan";
	public static final String GE = "GreaterThanOrEqualTo";
	public static final String IN = "In";
	public static final String LT = "LessThan";
	public static final String LE = "LessThanOrEqualTo";
	public static final String NOT_LIKE = "NotLike";
	public static final String NOT_IN = "NotIn";
	public static final String NOT_BETWEEN = "NotBetween";
	public static final String NOT_EQ = "NotEqualTo";
	public static final String regex = "([a-z])([A-Z]+)";
	public static final String replacement = "$1_$2";
	
	public static String addOrder2Example(Map<String, String> orderFields, Object example) throws Exception {
		Method set = example.getClass().getMethod("setOrderByClause", String.class);
		StringBuilder sb = new StringBuilder();
		Set<String> keys = orderFields.keySet();
		int i=0;
		for (String field : keys) {
			String uncameled = field.replaceAll(regex, replacement).toLowerCase();
			sb.append(uncameled);
			if (i<orderFields.size()-1)
				sb.append(",");
			sb.append(" ");
			sb.append(orderFields.get(field));
			i++;
		}
		if (sb.length()>0)
			set.invoke(example, sb.toString());
		return sb.toString();
	}
	
	public static void model2Example(Object model, Object example, Map<String, String> fieldsOperator, boolean fieldsAnd) throws Exception {
		Map<String, Object> fields = ReflectionUtils.object2Map(model);
		Method createCriteriaMethod = null;
		Object criteria = null;
		try {
			createCriteriaMethod = example.getClass().getMethod("createCriteria");
			criteria = createCriteriaMethod.invoke(example);
		} catch (Exception e) {
			throw new IllegalArgumentException("Can not create Criteria from example: " + example, e);
		} 
		for (String key : fieldsOperator.keySet()) {
			Object fieldVal = fields.get(key);
			if (fieldVal == null)
				continue;
			if (!fieldsAnd) {
				createCriteriaMethod = example.getClass().getMethod("or");
				criteria = createCriteriaMethod.invoke(example); // create for every field makes them ored.
			}
			String rel = fieldsOperator.get(key);
			Method addCriterionMethod = null;
			addCriterionMethod = criteria.getClass().getMethod("and" + StringUtils.capitalize(key) + rel, fieldVal.getClass());
			if (rel.equals(LIKE) || rel.equals(NOT_LIKE))
				addCriterionMethod.invoke(criteria, "%"+fieldVal+"%");
			else
				addCriterionMethod.invoke(criteria, fieldVal);
		}
	}
	
	public static void andModel2Example(Object model, Object example, Map<String, String> fieldsOperator, boolean fieldsAnd) throws Exception {
		Map<String, Object> fields = ReflectionUtils.object2Map(model);
		Method createCriteriaMethod = null;
		Object criteria = null;
		try {
			createCriteriaMethod = example.getClass().getMethod("createAndCriteria");
			criteria = createCriteriaMethod.invoke(example);
		} catch (Exception e) {
			throw new IllegalArgumentException("Can not create Criteria from example: " + example, e);
		} 
		for (String key : fieldsOperator.keySet()) {
			Object fieldVal = fields.get(key);
			if (fieldVal == null)
				continue;
			if (!fieldsAnd) {
				createCriteriaMethod = example.getClass().getMethod("and");
				criteria = createCriteriaMethod.invoke(example); // create for every field makes them ored.
			}
			String rel = fieldsOperator.get(key);
			Method addCriterionMethod = null;
			addCriterionMethod = criteria.getClass().getMethod("and" + StringUtils.capitalize(key) + rel, fieldVal.getClass());
			if (rel.equals(LIKE) || rel.equals(NOT_LIKE))
				addCriterionMethod.invoke(criteria, "%"+fieldVal+"%");
			else
				addCriterionMethod.invoke(criteria, fieldVal);
		}
	}
}
