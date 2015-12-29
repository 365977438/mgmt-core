package com.yoju360.mgmt.common.service;

import java.util.List;
import java.util.Map;

import com.yoju360.mgmt.common.model.GeoRegion;
import com.yoju360.mgmt.common.model.GeoRegionExample;
import com.yoju360.mgmt.core.service.CoreService;

public interface GeoRegionService extends CoreService<GeoRegion, GeoRegionExample> {
	List<Map<String, Object>> getProvinces();

	List<Map<String, Object>> getCities(Long provinceId);

	List<Map<String, Object>> getRegions(Long cityId);

	String getFullyAreaPrefix(Long provinceId, Long cityId, Long regionId);
}