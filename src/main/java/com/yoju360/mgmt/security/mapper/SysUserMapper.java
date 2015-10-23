package com.yoju360.mgmt.security.mapper;

import java.util.List;

import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.security.model.SysUser;
import com.yoju360.mgmt.security.model.SysUserExample;

public interface SysUserMapper extends BaseMapper<SysUser, SysUserExample> {

	/**
	 * 根据用户获取其他受管理的用户列表，这些用户的角色要属于当前用户的角色
	 * 
	 * @param userId
	 * @return
	 */
	List<SysUser> findManagedUserByUserId(Long userId);

}