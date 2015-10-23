/**
 * 
 */
package com.yoju360.mgmt.core.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author evan
 *
 */
public class ReflectionUtils {
	private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);
	
	public static Map<String, Object> object2Map(Object obj) {
		Map<String, Object> objectAsMap = new HashMap<String, Object>();
		try {
		    BeanInfo info = Introspector.getBeanInfo(obj.getClass());
		    for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
		        Method reader = pd.getReadMethod();
		        if (reader != null)
		            objectAsMap.put(pd.getName(),reader.invoke(obj));
		    }
		} catch (Exception e) {
			logger.error("Failed to convert object to map: " + obj, e);
		}
		return objectAsMap;
	}
	
	public static Object map2Object(Map<String, Object> map, Object obj) {
		try {
			BeanInfo info = Introspector.getBeanInfo(obj.getClass());
			for (String field : map.keySet()) {
				PropertyDescriptor pd = getBeanProp(info, field);
				if (pd!=null) {
					Class<?> targetType = pd.getPropertyType();
					Object actVal = convert(map.get(field), targetType);
					if (actVal != null) {
						pd.getWriteMethod().invoke(obj, actVal);
					}
				}
			}
			return obj;
		} catch (Exception e) {
			logger.error("Failed to set " + obj + " with " + map, e);
			return obj;
		}
	}
	
	private static Object convert(Object v, Class clazz) {
		try {
			return ConvertUtils.convert(v, clazz);
		} catch (Exception e){
			// ignore, allow some field failure
			if (logger.isDebugEnabled())
				logger.debug("Failed converting " + v + " to " + clazz);
		}
		return null;
	}
	
	private static PropertyDescriptor getBeanProp(BeanInfo info, String name) {
	    for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
	    	if (pd.getName().equals(name))
	    		return pd;
	    }
	    return null;
	}
}
