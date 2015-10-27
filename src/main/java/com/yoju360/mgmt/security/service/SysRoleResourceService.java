package com.yoju360.mgmt.security.service;

import java.util.List;

import com.yoju360.mgmt.core.service.CoreService;
import com.yoju360.mgmt.security.model.SysRoleResource;
import com.yoju360.mgmt.security.model.SysRoleResourceExample;

public interface SysRoleResourceService extends CoreService<SysRoleResource,SysRoleResourceExample>{

	List<SysRoleResource> findByResourceId(Long id);

	List<SysRoleResource> findByRoleId(Long roleId);

	void deleteByRoleId(Long id);

	void delete(List<SysRoleResource> spList);

	void save(Long id, List<Long> selected);
}