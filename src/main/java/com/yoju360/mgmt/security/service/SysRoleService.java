package com.yoju360.mgmt.security.service;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.yoju360.mgmt.core.service.CoreService;
import com.yoju360.mgmt.security.model.SysRole;
import com.yoju360.mgmt.security.model.SysRoleExample;

public interface SysRoleService extends CoreService<SysRole, SysRoleExample>{

	SysRole findByRoleCode(String roleCode);

	/**
	 * 获取用户的角色
	 * @param username
	 * @return
	 */
	List<SysRole> findByUsername(String username);

	List<SysRole> findByUserId(Long userId);

	List<SysRole> findByUrl(String url);

	Set<GrantedAuthority> findGrantedAuthorityByUsername(String username);

	Set<GrantedAuthority> findGrantedAuthorityByUserId(Long userId, Set<GrantedAuthority> grantedAuths);

}