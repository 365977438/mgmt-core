package com.yoju360.mgmt.core.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicUtils {
	private final static Logger logger = LoggerFactory.getLogger(PublicUtils.class);
	
	// Solr Date format
	private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
	
	/**
	 * 将源实体中相应的值赋值到目标实体中
	 * @param entity
	 * @param targetEntity
	 */
	public static void copyDataToSolrEntity(Object sourceEntity, Object targetEntity) {
		Field[] sourceFields = sourceEntity.getClass().getDeclaredFields();
		// 原实体属性合并上父类属性
		Field[] sourceSuperClassFields = sourceEntity.getClass().getSuperclass().getDeclaredFields();
		if(sourceSuperClassFields.length > 0) {
			List<Field> sourceFieldList = new ArrayList<Field>(Arrays.asList(sourceFields));
			sourceFieldList.addAll(Arrays.asList(sourceSuperClassFields));
			sourceFields = sourceFieldList.toArray(new Field[]{});
		}
		
		Field[] targetFields = targetEntity.getClass().getDeclaredFields();
		for(Field sf : sourceFields) {
			for(Field tf : targetFields) {
				// 如果源实体中的属性名等于目标实体中_*标识前的字符串，则将复制其值
				if(sf.getName().equals(tf.getName().split("_")[0])) {
					try {
						tf.setAccessible(true);
						sf.setAccessible(true);
						if(sf.getType().isAssignableFrom(java.sql.Timestamp.class) || 
								sf.getType().isAssignableFrom(java.sql.Date.class) || sf.getType().isAssignableFrom(java.util.Date.class)) {
							tf.set(targetEntity, format.format(sf.get(sourceEntity)));
						}else {
							tf.set(targetEntity, sf.get(sourceEntity));
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						logger.error("Failed to copy "+sourceEntity.getClass().getName() + " data to "+targetEntity.getClass().getName()+"! Error:"+e.getMessage());
					}
					break;
				}
			}
		}
	}
}
