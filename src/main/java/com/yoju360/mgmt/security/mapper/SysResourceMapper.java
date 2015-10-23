package com.yoju360.mgmt.security.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.security.model.SysResource;
import com.yoju360.mgmt.security.model.SysResourceExample;

public interface SysResourceMapper extends BaseMapper<SysResource, SysResourceExample> {

	List<SysResource> findMenuByUserIdAndSystem(@Param("userId")Long userId, @Param("systemName")String systemName, @Param("roleStatus")boolean roleStatusType);

	List<SysResource> findFunctionByUserId(@Param("userId")Long userId, @Param("systemName")String systemName, @Param("resType")short resType, @Param("roleStatus")boolean roleStatusType);

	List<SysResource> findMenuByUsername(@Param("username")String username, @Param("systemName")String systemName);

	/**
	 * id, code, title, type, parentId, url, hasChildren
	 * 
	 * @param parentId
	 * @param systemName
	 * @return
	 */
	List<Map<String, Object>> getTreeDataByParentId(@Param("parentId")Long parentId, @Param("systemName")String systemName);

	List<Map<String, Object>> getAuthorizedTreeData(@Param("parentId")Long parentId, @Param("roleId")Long roleId,
			@Param("systemName")String systemName);
	
	List<SysResource> getUserMenuTreeData(@Param("userId")Long userId, @Param("systemName")String systemName);
	
	List<SysResource> getSystems(@Param("userId")Long userId);
}