package com.yoju360.mgmt.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

public final class PropertyUtils {
	
	public static final String DEFAULT_PROPERTIES = "config.properties";
	
	private static Logger logger = LoggerFactory.getLogger(PropertyUtils.class);
	
	private static Properties props = new Properties();
	
	static {
		PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource(DEFAULT_PROPERTIES);
		InputStream is;
		try {
			is = resource.getInputStream();
			propertiesPersister.load(props, new InputStreamReader(is, "UTF-8"));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static String get(Object key, String file) {
		Properties props = new Properties();
		PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource(file);
		InputStream is;
		try {
			is = resource.getInputStream();
			propertiesPersister.load(props, new InputStreamReader(is, "UTF-8"));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		Object result = props.get(key);
		return result == null ? "" : result.toString();
	}
	
	public static String get(Object key) {
		Object result = props.get(key);
		return result == null ? "" : result.toString();
	}

}
