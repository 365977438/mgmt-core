/**
 * 
 */
package com.yoju360.mgmt.core.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author evan
 *
 */
public class JsonUtils {
	public static ObjectMapper mapper;
	static {
		mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}
	
	public static String toJson(Object object) throws JsonGenerationException, JsonMappingException, IOException {
		return mapper.writeValueAsString(object);
	}
	
	/**
	 * note: to array toObject(json, MyClass[].class);
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T toObject(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, clazz);
	}
	
	public static <T> List<T> toList(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
	}
	
	public static <K, V> Map<K, V> toMap(String json, Class<K> keyClazz, Class<V> ValueClazz) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, new TypeReference<Map<K, V>>(){});
	}
}
