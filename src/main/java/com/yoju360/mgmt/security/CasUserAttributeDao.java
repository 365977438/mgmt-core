/**
 * 
 */
package com.yoju360.mgmt.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AbstractFlatteningPersonAttributeDao;
import org.jasig.services.persondir.support.AttributeNamedPersonImpl;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;

import com.yoju360.mgmt.core.util.JsonUtils;
import com.yoju360.mgmt.security.model.MenuNode;
import com.yoju360.mgmt.security.model.SysResource;
import com.yoju360.mgmt.security.model.SysRole;
import com.yoju360.mgmt.security.model.SysUser;
import com.yoju360.mgmt.security.model.type.ResourceType;
import com.yoju360.mgmt.security.service.SysResourceService;
import com.yoju360.mgmt.security.service.SysRoleService;
import com.yoju360.mgmt.security.service.SysUserService;

/**
 * @author evan
 *
 */
public class CasUserAttributeDao extends AbstractFlatteningPersonAttributeDao {
	public static final String ATTRIBUTE_SYSTEMS = "systems";
	public static final String ATTRIBUTE_MENU = "menu";
	public static final String ATTRIBUTE_OPERATIONS = "operations";
	public static final String ATTRIBUTE_USER = "sysUser";
	public static final String ATTRIBUTE_ROLES = "roles";
	public static final String ATTRIBUTE_AUTH_DATA = "authorizationData";
	private SysResourceService sysResourceService;
	private SysUserService sysUserService;
	private SysRoleService sysRoleService;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public IPersonAttributes getPerson(String username) {
		try {
			Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
			
			SysUser user = sysUserService.findByUsername(username);
			if (user==null)
				return null;
			Long userId = user.getId();
			// url protected authorization data
			Map<String, Collection<ConfigAttribute>> resourceMap = new HashMap<String, Collection<ConfigAttribute>>();
			List<SysResource> resources = sysResourceService.findAll();
			for (SysResource resource : resources) {
				Collection<ConfigAttribute> atts = new HashSet<ConfigAttribute>();
				List<SysRole> roles = sysRoleService.findByUrl(resource.getUrl());
				for (SysRole role : roles) {
					ConfigAttribute ca = new SecurityConfig(role.getId().toString());
					atts.add(ca);
				}

				resourceMap.put(resource.getUrl(), atts);
			}
			attributes.put(ATTRIBUTE_AUTH_DATA, (List)Collections.singletonList(JsonUtils.toJson(resourceMap)));
			
			// sys user
			attributes.put(ATTRIBUTE_USER, (List)Collections.singletonList(JsonUtils.toJson(user)));
			// roles
			Set<GrantedAuthority> grantedAuths = new HashSet<GrantedAuthority>();
		    sysRoleService.findGrantedAuthorityByUserId(user.getId(),grantedAuths);
			attributes.put(ATTRIBUTE_ROLES, (List)Collections.singletonList(JsonUtils.toJson(grantedAuths)));
//			sysGivePermissionMgtService.findGrantedAuthorityByUserId(user.getId(),grantedAuths);
			// systems
			List<SysResource> systems = sysResourceService.getSystems(userId);
			attributes.put(ATTRIBUTE_SYSTEMS, (List)Collections.singletonList(JsonUtils.toJson(systems)));
			// system and menus
			List<SysResource> flatData = sysResourceService.getUserMenuTreeData(userId, null);
			
			Map<Long, List<MenuNode>> sysAndMenu = new HashMap<Long, List<MenuNode>>();
			
			Map<Long, List> parentAndChildrenMap = new HashMap<Long, List>();
			MenuNode tree = new MenuNode(null, null);
			
			SysResource curSystem = null;
			for (int i=0;i<flatData.size();i++) {
				SysResource res = flatData.get(i);
				if (res.getType()==ResourceType.SYSTEM.value) {// new system level
					if (curSystem == null) {
						curSystem = res;
						continue;
					} else if (!curSystem.getId().equals(res.getId())) {// switch to next system
						sysAndMenu.put(curSystem.getId(), tree.children);
						parentAndChildrenMap = new HashMap<Long, List>();
						tree = new MenuNode(null, null);
						curSystem = res;
						continue;
					}
				}
				
				Long parentId = res.getParentId();
				Long id = res.getId();
				
				MenuNode node = new MenuNode(id, res);
				parentAndChildrenMap.put(id, node.children);
				
				if (parentId==null || parentAndChildrenMap.get(parentId)==null) { // top level
					tree.children.add(node);
				} else {
					List sameLevel = parentAndChildrenMap.get(parentId);
					sameLevel.add(node);
				}
				if (i==flatData.size()-1) { // last system
					sysAndMenu.put(curSystem.getId(), tree.children);
				}
			}
			
			attributes.put(ATTRIBUTE_MENU, (List)Collections.singletonList(JsonUtils.toJson(sysAndMenu)));
			
			// permitted operations
			/* get user operation buttons privileges */
			List<SysResource> opers = sysResourceService.findFunctionByUserId(userId, null);
			attributes.put(ATTRIBUTE_OPERATIONS, (List)Collections.singletonList(JsonUtils.toJson(opers)));
			AttributeNamedPersonImpl userAttr = new AttributeNamedPersonImpl(attributes);
			return userAttr;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(Map<String, List<Object>> query) {
		return null;
	}

	@Override
	public Set<String> getPossibleUserAttributeNames() {
		return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(ATTRIBUTE_USER, ATTRIBUTE_SYSTEMS, ATTRIBUTE_MENU, ATTRIBUTE_OPERATIONS, ATTRIBUTE_ROLES, ATTRIBUTE_AUTH_DATA)));
	}

	@Override
	public Set<String> getAvailableQueryAttributes() {
		return null;
	}

	public SysResourceService getSysResourceService() {
		return sysResourceService;
	}

	public void setSysResourceService(SysResourceService sysResourceService) {
		this.sysResourceService = sysResourceService;
	}

	public SysUserService getSysUserService() {
		return sysUserService;
	}

	public void setSysUserService(SysUserService sysUserService) {
		this.sysUserService = sysUserService;
	}

	public SysRoleService getSysRoleService() {
		return sysRoleService;
	}

	public void setSysRoleService(SysRoleService sysRoleService) {
		this.sysRoleService = sysRoleService;
	}
}
