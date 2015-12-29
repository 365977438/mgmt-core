package com.yoju360.mgmt.common.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoju360.mgmt.common.mapper.GeoRegionMapper;
import com.yoju360.mgmt.common.model.GeoRegion;
import com.yoju360.mgmt.common.model.GeoRegionExample;
import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.core.service.AbstractCoreService;

@Service("geoRegionService")
@Transactional
public class GeoRegionServiceImpl extends AbstractCoreService<GeoRegion, GeoRegionExample> implements GeoRegionService {
	@Autowired
	private GeoRegionMapper geoRegionMapper;
	
	@Override
	protected BaseMapper<GeoRegion, GeoRegionExample> getMapper() {
		return geoRegionMapper;
	}

	@Override
	public String getModuleName() {
		return null;
	}

	public List<Map<String, Object>> getProvinces() {
		return geoRegionMapper.getProvinces();
	}

	public List<Map<String, Object>> getCities(Long provinceId) {
		return geoRegionMapper.getCities(provinceId);
	}

	public List<Map<String, Object>> getRegions(Long cityId) {
		return geoRegionMapper.getRegions(cityId);
	}

	public String getFullyAreaPrefix(Long provinceId, Long cityId, Long regionId) {
		return getModel(provinceId).getName() + getModel(cityId).getName() + getModel(regionId).getName();
	}

}
