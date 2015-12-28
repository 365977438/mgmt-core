package com.yoju360.mgmt.common.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yoju360.mgmt.common.model.GeoRegion;
import com.yoju360.mgmt.common.model.GeoRegionExample;
import com.yoju360.mgmt.core.mapper.BaseMapper;

public interface GeoRegionMapper extends BaseMapper<GeoRegion, GeoRegionExample> {
	List<Map<String, Object>> getProvinces();

	List<Map<String, Object>> getCities(@Param("provinceId")Long provinceId);

	List<Map<String, Object>> getRegions(@Param("cityId")Long cityId);
}