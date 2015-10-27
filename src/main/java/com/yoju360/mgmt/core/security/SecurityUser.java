package com.yoju360.mgmt.core.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.yoju360.mgmt.security.model.MenuNode;
import com.yoju360.mgmt.security.model.SysResource;

public class SecurityUser extends User {

	private static final long serialVersionUID = 1L;
	private Object userObject;
	private String username;
	private Long id;
	private List<SysResource> systems;
	private Map<Long, List<MenuNode>> menu;
	private List<SysResource> operations;
	
	public SecurityUser(Long id, String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Object userObject) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.id = id;
		this.userObject = userObject;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public List<SysResource> getSystems() {
		return systems;
	}

	public void setSystems(List<SysResource> systems) {
		this.systems = systems;
	}

	public Map<Long, List<MenuNode>> getMenu() {
		return menu;
	}

	public void setMenu(Map<Long, List<MenuNode>> menu) {
		this.menu = menu;
	}

	public List<SysResource> getOperations() {
		return operations;
	}

	public void setOperations(List<SysResource> operations) {
		this.operations = operations;
	}
}
