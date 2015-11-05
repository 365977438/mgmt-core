/**
 * 
 */
package com.yoju360.mgmt.core.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author evan
 *
 */
public class ApiUtils {
	private static final Logger logger = LoggerFactory.getLogger(ApiUtils.class);

	public static boolean validateApiSign(String apiKey, Map<String, String> parameters) {
		if (StringUtils.isEmpty(parameters.get("sign")))
			throw new RuntimeException("缺少sign参数");
		
		String apiSign = parameters.get("sign");
		String encryptionSign = createApiSign(parameters, apiKey);
		if (!apiSign.equals(encryptionSign)) {
			return false;
		}
		return true;
	}

	public static String createApiSign(Map<String, String> params, String apiKey) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			List<String> keys = new ArrayList<String>(params.keySet());
			// 1. sort
			Collections.sort(keys, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			for (String key : keys) {
				if (!key.equalsIgnoreCase("sign"))
					byteStream.write((params.get(key)).getBytes("UTF-8"));
			}
			String tosign = new String(byteStream.toByteArray(), "UTF-8");
			logger.debug("keys: " + keys + ", tosign : " + tosign);
			byte[] array = byteStream.toByteArray();
			String sign = HashUtils.bytes2Hex(HashUtils.getHmacMd5Bytes(apiKey.getBytes("UTF-8"), array));
			return sign;
		} catch (Exception e) {
			logger.error("make sign error", e);
			throw new RuntimeException(e.getMessage());
		}
	}
}
