package com.yoju360.mgmt.security.service;

import java.util.List;
import java.util.Map;

import com.yoju360.mgmt.core.service.CoreService;
import com.yoju360.mgmt.security.model.SysResource;
import com.yoju360.mgmt.security.model.SysResourceExample;

public interface SysResourceService extends CoreService<SysResource, SysResourceExample>{

	List<SysResource> findByParentId(Long id);

	List<SysResource> findAll();
	
	List<SysResource> findBySystemName();
	/**
	 * 根据用户角色查找相应的菜单
	 * 
	 * @param username
	 * @param systemValue
	 * @param menuValue
	 * @return
	 */
	List<SysResource> findMenuByUsername(String username);

	List<SysResource> findMenuByUserId(Long userId);

	List<SysResource> findFunctionByUserId(Long userId, String systemName);

	List<SysResource> findSystemByUserId(Long userId);

	/**
	 * id, code, title, type, parentId, url, childCount, granted
	 * @param parentId
	 * @return
	 */
	List<Map<String, Object>> getAuthorizedTreeData(Long parentId, Long roleId);

	List<SysResource> getSystems(Long userId);

	List<Map<String, Object>> getTreeDataByParentId(Long parentId);

	List<SysResource> getUserMenuTreeData(Long userId, String systemName);

}