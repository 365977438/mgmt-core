package com.yoju360.mgmt.security.service;

import java.util.List;

import com.yoju360.mgmt.core.service.CoreService;
import com.yoju360.mgmt.security.model.SysUserRole;
import com.yoju360.mgmt.security.model.SysUserRoleExample;

public interface SysUserRoleService extends CoreService<SysUserRole, SysUserRoleExample>{

	/**
	 * 保存用户授予的角色
	 * 
	 * @param roleIds
	 * @param userId
	 */
	void saveUserRoleShip(List<Long> roleIds, Long userId);

	List<SysUserRole> findByUserId(Long userId);

	/**
	 * 通过角色ID获取所有的用户ID
	 * 
	 * @param roldId
	 * @return
	 */
	List<SysUserRole> getUserIdByRole(Long roleId);

	void delete(List<SysUserRole> list);

	void deleteByRoleId(Long id);

}