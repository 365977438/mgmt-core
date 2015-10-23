package com.yoju360.mgmt.security.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.yoju360.mgmt.core.security.SecurityUser;
import com.yoju360.mgmt.security.model.SysUser;

/**
 * 实现SpringSecurity的UserDetailsService接口,实现获取用户Detail信息的回调函数.
 * 
 */
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRoleService sysRoleService;

	/**
	 * 获取用户Details信息的回调函数.
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		SysUser user = sysUserService.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("用户" + username + " 不存在");
		}

		Set<GrantedAuthority> grantedAuths = new HashSet<GrantedAuthority>();
	    sysRoleService.findGrantedAuthorityByUserId(user.getId(),grantedAuths);
		//sysGivePermissionMgtService.findGrantedAuthorityByUserId(user.getId(),grantedAuths);
		
		
		//-- 暂时全部设为true. --//
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		SecurityUser userdetails = new SecurityUser(user.getId(), user.getFullName(), user.getAuthenticator().toLowerCase(), enabled,
				accountNonExpired, credentialsNonExpired, accountNonLocked, grantedAuths, user);
		userdetails.setUsername(user.getUsername());
		return userdetails;
	}
}
