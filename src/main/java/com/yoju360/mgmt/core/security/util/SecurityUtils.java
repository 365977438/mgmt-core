/**
 * 
 */
package com.yoju360.mgmt.core.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.yoju360.mgmt.core.security.SecurityUser;
import com.yoju360.mgmt.core.util.PropertyUtils;
import com.yoju360.mgmt.core.util.SpringContextUtils;

/**
 * @author evan
 *
 */
public final class SecurityUtils {
	private final static String SYSTEM_NAME = PropertyUtils.get("system.name").equals("")?null:PropertyUtils.get("system.name");
	private static String currentSystem = null;
	private static String userOperations = null;
	
	private SecurityUtils() {
		
	}
	
	public static void setUserOperations(String userOpers) {
		userOperations = userOpers;
	}
	
	/**
	 * 用户是否具有某项操作的权限
	 * @param operationCode
	 * @return
	 */
	public static boolean hasPermission(String operationCode) {
		if (userOperations==null || userOperations.indexOf(operationCode)<0)
			return false;
		return true;
	}
	
	/**
	 * 当前用户选择的系统
	 * @return
	 */
	public static String getCurrentSystemName() {
		if (currentSystem==null)
			return SYSTEM_NAME;
		return currentSystem;
	}
	
	public static void setCurrentSystemName(String currentSys) {
		currentSystem = currentSys;
	}
	
	/**
	 * 获取当前用户的ID
	 * @return
	 */
	
	public static Long getUserId() {
		if (getCurrentUser()!=null)
			return ((SecurityUser) getCurrentUser()).getId();
		return null;
	}
	
	/**
	 * 获取当前的用户名
	 * @return
	 */
	public static String getUsername() {
		Authentication authentication = getAuthentication();
		if (authentication != null)
			return authentication.getName();
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends User> T getCurrentUser() {
		Authentication authentication = getAuthentication();

		if (authentication == null) {
			return null;
		}

		Object principal = authentication.getPrincipal();
		if (!(principal instanceof User)) {
			return null;
		}

		return (T) principal;
	}
	/**
	 * 取得Authentication, 如当前SecurityContext为空时返回null.
	 */
	public static Authentication getAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return null;
		}

		return context.getAuthentication();
	}

}
