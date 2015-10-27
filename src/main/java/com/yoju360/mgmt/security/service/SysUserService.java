package com.yoju360.mgmt.security.service;

import java.util.List;

import com.yoju360.mgmt.core.service.CoreService;
import com.yoju360.mgmt.security.model.SysUser;
import com.yoju360.mgmt.security.model.SysUserExample;

public interface SysUserService extends CoreService<SysUser, SysUserExample>{

	/**
	 * 保存用户修改密码
	 * 
	 * @param user
	 */
	void modifyPwd(Long userId, String oldPwd, String pwd);

	/**
	 * 修改密码
	 * 
	 * @param entity
	 */
	void updatePwd(Long userId, String pwd);

	/**
	 * 根据用户名查找用户
	 * 
	 * @param name
	 *            用户名
	 * @return SysUser
	 */
	SysUser findByUsername(String username);

	/**
	 * 根据用户获取其他所有用户列表
	 * 获得小于当前用户的权限的用户信息 TODO ??
	 * 
	 * @return
	 */

	List<SysUser> findByUserId(Long userId);

	List<SysUser> findByDepartmentId(Long id);

}