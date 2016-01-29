/**
 * 
 */
package com.yoju360.mgmt.core.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.yoju360.mgmt.core.util.ApiUtils;

/**
 * 处理API接口的基类。
 * 
 * @author evan
 *
 */
@SuppressWarnings("rawtypes")
public abstract class ApiController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	protected String getRequestParameter(HttpServletRequest request, String name, boolean required) {
		String value = request.getParameter(name);
		if (required && value == null)
			throw new IllegalArgumentException("缺少必要参数: " + name);
		return value;
	}
	
	/**
	 * 取得请求参数Map
	 * 
	 * @param request
	 * @return
	 */
	protected Map<String, String> getRequestParameters(HttpServletRequest request) {
		Enumeration enu = request.getParameterNames();
		Map<String, String> params = new HashMap<String, String>();
		while (enu.hasMoreElements()) {
			String name = enu.nextElement().toString();
			String[] values = request.getParameterValues(name);
			String arrayStr = Arrays.toString(values);
			arrayStr = arrayStr.substring(1, arrayStr.length()-1);
			params.put(name, arrayStr.length()>0?arrayStr:null);
		}
		return params;
	}
	/**
	 * 验证接口必要参数与签名
	 * @param request
	 * @throws Exception
	 */
	protected void validateApiRequest(HttpServletRequest request) throws Exception {
		Map<String, String> params = getRequestParameters(request);
		
		if (StringUtils.isEmpty(params.containsKey("appName")))
			throw new Exception("缺少appName");
		if (StringUtils.isEmpty(params.containsKey("timestamp")))
			throw new Exception("缺少timestamp");
		if (StringUtils.isEmpty(params.containsKey("sign")))
			throw new Exception("缺少sign");
		
		String apiKey = getApiKey(params.get("appName"));
		if (StringUtils.isEmpty(apiKey))
			throw new Exception("找不到appName对应的应用");
		
		if (!ApiUtils.validateApiSign(apiKey, params))
			throw new Exception("sign不正确");
	}
	
	/**
	 * 返回接口响应报文
	 * @param code
	 * @param msg
	 * @param response
	 * @throws IOException
	 */
	protected void apiResponse(int code, String msg,  HttpServletResponse response) throws IOException {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("code", code);
		messageMap.put("msg", msg);
		logger.info("Api response: " + messageMap);
		writeJson(messageMap, response);
	}
	/**
	 * 返回接口响应报文
	 * @param code
	 * @param msg
	 * @param result
	 * @param response
	 * @throws IOException
	 */
	protected void apiResponse(int code, String msg, Object result, HttpServletResponse response) throws IOException {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("code", code);
		messageMap.put("msg", msg);
		messageMap.put("result", result);
		writeJson(messageMap, response);
	}
	
	/**
	 * 根据请求的appName返回该app的apiKey
	 * @param appName
	 * @return
	 */
	protected abstract String getApiKey(String appName);
}
