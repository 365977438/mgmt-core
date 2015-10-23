/**
 * 
 */
package com.yoju360.mgmt.core.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yoju360.mgmt.core.service.CoreService;
import com.yoju360.mgmt.core.util.ExcelUtils;
import com.yoju360.mgmt.core.util.ReflectionUtils;

/**
 * @author evan
 *
 */
public abstract class BaseController<T> {
    
	static {
		JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	}
	
	@RequestMapping(value = "/exportExcel.do")
	@ResponseBody
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String headers = request.getParameter("headers");
		String rows = request.getParameter("rows");
		String fileName = request.getParameter("fileName");
		List<String> listHeaders = JSON.parseArray(headers, String.class);
		List<List> listRows = JSON.parseArray(rows, List.class);
		
		if (StringUtils.isEmpty(fileName))
			fileName = "excel.xls";
		
		Workbook workbook = ExcelUtils.createExcelSheet(listHeaders, listRows);
		
		ExcelUtils.setExcelResponseHeader(request, response, fileName);
		OutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
	}
	
	public void writeJson(Object obj, HttpServletResponse response) throws IOException {
		String s = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter printwriter = response.getWriter();
		printwriter.print(s);
		printwriter.close();
	}
	
	public void writeResponse(String msg, boolean success, HttpServletResponse response) throws IOException {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("success", success);
		messageMap.put("msg", msg);
		writeJson(messageMap, response);
	}

	protected <TT> TT getRequestObject(HttpServletRequest request, Class<TT> clazz) {
		return getRequestObject(request, "object", clazz);
	}
	
	/**
	 * 把新对象的属性值覆盖到原有的对象（如果按id字段查找存在的话），保留原有对象未被设置的值。
	 * 
	 * @param request
	 * @param clazz
	 * @param idField
	 * @param service
	 * @return
	 */
	protected <TT> TT getRequestObject(HttpServletRequest request, Class<TT> clazz, String idField, CoreService service) {
		return getRequestObject(request, "object", clazz, idField, service);
	}
	
	protected <TT> TT getRequestObject(HttpServletRequest request, String paramName, Class<TT> clazz) {
		return getRequestObject(request, paramName, clazz, null, null);
	}
	
	/**
	 * 把新对象的属性值覆盖到原有的对象（如果按id字段查找存在的话），保留原有对象未被设置的值。
	 * 
	 * @param request
	 * @param paramName
	 * @param clazz
	 * @param idField
	 * @param service
	 * @return
	 */
	protected <TT> TT getRequestObject(HttpServletRequest request, String paramName, 
			Class<TT> clazz, String idField, CoreService service) {
		if (service!=null && idField != null && request.getParameter(paramName)!=null) {
			Map<String, Object> map = (Map<String, Object>)JSON.parse(request.getParameter(paramName));
			if (map.containsKey(idField) && map.get(idField) != null) {
				TT oldModel = (TT)service.getModel(Long.parseLong(map.get(idField).toString()));
				if (oldModel!=null)
					return (TT)ReflectionUtils.map2Object(map, oldModel);
			}
		}
		
		if (request.getParameter(paramName)!=null)
			return JSON.parseObject(request.getParameter(paramName), clazz);
		
		return null;
	}
	
	protected <TT> List<TT> getRequestArray(HttpServletRequest request, 
			Class<TT> clazz) {
		return getRequestArray(request, "array", clazz);
	}
	
	protected <TT> List<TT> getRequestArray(HttpServletRequest request, String paramName, 
			Class<TT> clazz) {
		if (request.getParameter(paramName)!=null)
			return JSON.parseArray(request.getParameter(paramName), clazz);
		return null;
	}
}
