/**
 * 
 */
package com.yoju360.mgmt.common.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoju360.mgmt.common.model.GeoRegion;
import com.yoju360.mgmt.common.service.GeoRegionService;
import com.yoju360.mgmt.core.controller.BaseController;

/**
 *	地区信息表
 */
@Controller
@RequestMapping(value="/address")
public class GeoRegionController extends BaseController<GeoRegion>{
	@Autowired
	GeoRegionService geoRegionService;
	
	@RequestMapping(value = "/getThisArea.do", method = RequestMethod.GET)
	@ResponseBody
	public void getThisArea(
			@RequestParam(value = "provinceId", required = false) Long provinceId,
			@RequestParam(value = "cityId", required = false) Long cityId,
			@RequestParam(value = "regionId", required = false) Long regionId,
			HttpServletResponse response) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("province", geoRegionService.getProvinces());
		result.put("city", geoRegionService.getCities(provinceId));
		result.put("region", geoRegionService.getRegions(cityId));
		writeJson(result, response);
	}
	
	@RequestMapping(value = "/getProvinces.do", method = RequestMethod.GET)
	@ResponseBody
    public void getProvinces(HttpServletResponse response) throws Exception {
		List<Map<String, Object>> provinces = geoRegionService.getProvinces();
		writeJson(provinces, response);
	}
	
	@RequestMapping(value = "/getCities.do", method = RequestMethod.GET)
	@ResponseBody
    public void getCities(@RequestParam(value = "provinceId", required = false) Long provinceId, HttpServletResponse response) throws Exception {
		List<Map<String, Object>> cities = geoRegionService.getCities(provinceId);
		writeJson(cities, response);
	}
	
	@RequestMapping(value = "/getRegions.do", method = RequestMethod.GET)
	@ResponseBody
    public void getRegions(@RequestParam(value = "cityId", required = false) Long cityId, HttpServletResponse response) throws Exception {
		List<Map<String, Object>> regions = geoRegionService.getRegions(cityId);
		writeJson(regions, response);
	}
	
	/**
	 * 获取地址拼接文本信息（如：广东省广州市天河区）
	 * @param provinceId
	 * @param cityId
	 * @param regionId
	 * @return
	 */
	@RequestMapping(value = "/getFullyAreaPrefix.do", method = RequestMethod.GET)
	@ResponseBody
	public String getFullyAreaPrefix(
			@RequestParam(value = "provinceId", required = true)Long provinceId,
			@RequestParam(value = "cityId", required = true)Long cityId,
			@RequestParam(value = "regionId", required = true)Long regionId) {
		return 	geoRegionService.getFullyAreaPrefix(provinceId, cityId, regionId);
	}
}
