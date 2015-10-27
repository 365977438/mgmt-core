package com.yoju360.mgmt.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.yoju360.mgmt.core.security.SecurityUser;
import com.yoju360.mgmt.core.util.JsonUtils;
import com.yoju360.mgmt.security.CasInvocationSecurityMetadataSource;
import com.yoju360.mgmt.security.CasUserAttributeDao;
import com.yoju360.mgmt.security.model.MenuNode;
import com.yoju360.mgmt.security.model.SysResource;
import com.yoju360.mgmt.security.model.SysUser;

/**
 * Populates the {@link org.springframework.security.core.GrantedAuthority}s for a user by reading a list of attributes that were returned as
 * part of the CAS response.
 * @author Evan Wu
 */
public final class CasUserDetailsService extends AbstractCasAssertionUserDetailsService {

    private static final String NON_EXISTENT_PASSWORD_VALUE = "NO_PASSWORD";

    @Override
    protected UserDetails loadUserDetails(final Assertion assertion) {
		try {
			//-- 暂时全部设为true. --//
			boolean enabled = true;
			boolean accountNonExpired = true;
			boolean credentialsNonExpired = true;
			boolean accountNonLocked = true;
	
			String value = assertion.getPrincipal().getAttributes().get(CasUserAttributeDao.ATTRIBUTE_USER).toString();
			SysUser user = JsonUtils.toObject(value, SysUser.class);
			value = assertion.getPrincipal().getAttributes().get(CasUserAttributeDao.ATTRIBUTE_ROLES).toString();
			List<Map> authMaps = JsonUtils.toList(value, Map.class);
			List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
			for (Map m : authMaps) {
				grantedAuths.add(new SimpleGrantedAuthority(m.get("authority").toString()));
			}
			SecurityUser userDetails = new SecurityUser(user.getId(), user.getFullName(), NON_EXISTENT_PASSWORD_VALUE, enabled,
					accountNonExpired, credentialsNonExpired, accountNonLocked, grantedAuths, user);
			userDetails.setUsername(user.getUsername());
	
			
			// other attributes for auth purpose
	        for (final String attribute : assertion.getPrincipal().getAttributes().keySet()) {
	            value = assertion.getPrincipal().getAttributes().get(attribute).toString();
	
	            if (value == null) {
	                continue;
	            }
	
	            if (attribute.equals(CasUserAttributeDao.ATTRIBUTE_SYSTEMS)) {
	            	userDetails.setSystems(JsonUtils.toList(value, SysResource.class));
	            } else if (attribute.equals(CasUserAttributeDao.ATTRIBUTE_MENU)) {
	            	Map<String, List> menuData = JsonUtils.toMap(value, String.class, List.class);
	            	Map<Long, List<MenuNode>> sysAndMenu = new HashMap<Long, List<MenuNode>>();
	            	for (String key : menuData.keySet()) {
	            		List<Map> val = menuData.get(key);
	            		List<MenuNode> mn = new ArrayList<MenuNode>();
	            		for (Map m : val) {
	            			mn.add((MenuNode)JsonUtils.toObject(JsonUtils.toJson(m), MenuNode.class));
	            		}
	            		sysAndMenu.put(Long.valueOf(key), mn);
	            	}
	            	userDetails.setMenu(sysAndMenu);
	            } else if (attribute.equals(CasUserAttributeDao.ATTRIBUTE_OPERATIONS)) {
	            	userDetails.setOperations(JsonUtils.toList(value, SysResource.class));
	            }
	        }
	        // authorization data
	        value = assertion.getPrincipal().getAttributes().get(CasUserAttributeDao.ATTRIBUTE_AUTH_DATA).toString();
	        Map<String, List> res = JsonUtils.toMap(value, String.class, List.class);
	        Map<String, Collection<ConfigAttribute>> resourceMap = new HashMap<String, Collection<ConfigAttribute>>();
	        for (String url : res.keySet()) {
	        	List<Map> rs = res.get(url);
	        	Collection<ConfigAttribute> set = new HashSet<ConfigAttribute>();
	        	for (Map obj : rs) {
	        		set.add(new SecurityConfig(obj.get("attribute").toString()));
	        	}
	        	resourceMap.put(url, set);
	        }
	        CasInvocationSecurityMetadataSource.setResourceMap(resourceMap);
	        return userDetails;
		} catch (Exception e) {
        	throw new RuntimeException(e);
        }
    }
}
