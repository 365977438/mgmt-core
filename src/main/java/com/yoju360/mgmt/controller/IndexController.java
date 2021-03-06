/**
 * 
 */
package com.yoju360.mgmt.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yoju360.mgmt.core.security.SecurityUser;
import com.yoju360.mgmt.core.security.util.SecurityUtils;
import com.yoju360.mgmt.core.util.PropertyUtils;
import com.yoju360.mgmt.security.model.MenuNode;
import com.yoju360.mgmt.security.model.SysResource;

/**
 * @author evan
 *
 */
@Controller
@RequestMapping("/index")
public class IndexController {
	private final static String SYSTEM_NAME = PropertyUtils.get("system.name").equals("")?null:PropertyUtils.get("system.name");
	
	public static String getCookieValue(String cookieName, HttpServletRequest request) {
		String value = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			int i = 0;
			boolean cookieExists = false;
			while (!cookieExists && i < cookies.length) {
				if (cookies[i].getName().equals(cookieName)) {
					cookieExists = true;
					value = cookies[i].getValue();
				} else {
					i++;
				}
			}
		}
		return value;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String mainPage(HttpServletRequest request, HttpServletResponse response, /*@CookieValue(value="CURRENT_SYSTEM", required=false) String curSys,*/ ModelMap model) {
		SecurityUser su = (SecurityUser)SecurityUtils.getCurrentUser();
		
		model.addAttribute("user", su==null?null:su.getUserObject());
		
		SysResource currentSystem = null;
		List<SysResource> systems = su.getSystems();
		
		model.put("systems", systems);
		String selectedSystem = null; // 最终要显示的系统
		
//		if (curSys != null) {
//			selectedSystem = curSys;
//		} else 
		if (SYSTEM_NAME != null) {
			selectedSystem = SYSTEM_NAME;
		}
		
		if (selectedSystem!=null) { // find by code
			for (SysResource sys : systems) {
				if (sys.getCode().equals(selectedSystem)) {
					currentSystem = sys;
					break;
				}
			}
		} else { // use the first
			currentSystem = (systems != null && systems.size() > 0) ? systems.get(0):null;
			selectedSystem = currentSystem!=null?currentSystem.getCode():null;
		}
		
		SecurityUtils.setCurrentSystemName(selectedSystem);
		
		model.put("currentSystem", currentSystem);
		
		if (currentSystem!=null) {
			List<MenuNode> sysMenu = su.getMenu().get(currentSystem.getId());
			model.put("menu", sysMenu);
		}
		
		// get user operation buttons privileges
		List<SysResource> opers = su.getOperations();
		StringBuilder sb = new StringBuilder();
		for (SysResource res : opers) {
			sb.append(res.getCode());
			sb.append(",");
		}
		String operations = sb.toString();
		SecurityUtils.setUserOperations(operations);
		model.put("userOperations", operations);
		
		/*
		if (curSys == null || (SYSTEM_NAME != null && curSys.equals(SYSTEM_NAME))) { // 优先访问当前系统
			return "main";
		} else if (currentSystem!=null && currentSystem.getUrl().startsWith("http")) { // 须跳转到新系统
			return "redirect:" + currentSystem.getUrl();
		} else { // 不可以显示别的系统的菜单，重置cookie
			Cookie cookie=new Cookie("CURRENT_SYSTEM",selectedSystem);
			cookie.setMaxAge(-1);
			response.addCookie(cookie);
			return "redirect:#";
		}
		*/
		return "main";
	}
}
