/**
 * 
 */
package com.yoju360.mgmt.security.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.yoju360.mgmt.core.controller.BaseController;
import com.yoju360.mgmt.core.controller.JDatatableOutput;
import com.yoju360.mgmt.core.controller.JDatatableParams;
import com.yoju360.mgmt.core.security.util.SecurityUtils;
import com.yoju360.mgmt.core.util.HashUtils;
import com.yoju360.mgmt.security.model.SysDepartment;
import com.yoju360.mgmt.security.model.SysUser;
import com.yoju360.mgmt.security.model.SysUserExample;
import com.yoju360.mgmt.security.model.SysUserRole;
import com.yoju360.mgmt.security.service.SysDepartmentService;
import com.yoju360.mgmt.security.service.SysUserRoleService;
import com.yoju360.mgmt.security.service.SysUserService;

/**
 * @author evan
 *
 */
@Controller
@RequestMapping(value="/sys_user")
public class UserController extends BaseController<SysUser>{
	@Autowired
	SysUserService sysUserService;
	@Autowired
	SysDepartmentService sysDepartmentService;
	@Autowired
	SysUserRoleService sysUserRoleService;
	
	private Map<String, String> deptOptions;
	
	@RequestMapping(value = "/index.do")
    public String index(HttpServletRequest request, ModelMap model) throws Exception {
		initDepartmentOptions();
		model.addAttribute("deptOptions", JSON.toJSONString(deptOptions));
		return "security/user";
    }
	
	private void initDepartmentOptions() {
		deptOptions = new LinkedHashMap<String, String>();
		List<SysDepartment> depts = sysDepartmentService.findAll();
		for (SysDepartment dept : depts) {
			deptOptions.put(dept.getId().toString(), dept.getTitle());
		}
	}
	
	@RequestMapping(value = "/show.do")
    public String show(@RequestParam("id") Long id, ModelMap model) throws Exception {
		SysUser dept = sysUserService.getModel(id);
		dept.setAuthenticator("UNCHANGED");
		model.addAttribute("user", dept);
		SysDepartment d = sysDepartmentService.getModel(dept.getDepartmentId());
		model.addAttribute("department", d!=null?d.getTitle():null);
		return "security/user-show";
    }
	
	@RequestMapping(value = "/profile.do")
    public String profile(ModelMap model) throws Exception {
		SysUser dept = sysUserService.getModel(SecurityUtils.getUserId());
		model.addAttribute("oldAuthenticator", dept.getAuthenticator());
		dept.setAuthenticator("UNCHANGED");
		model.addAttribute("user", dept);
		SysDepartment d = sysDepartmentService.getModel(dept.getDepartmentId());
		model.addAttribute("department", d!=null?d.getTitle():null);
		return "security/user-profile";
    }
	
	@RequestMapping(value = "/choose-role.do")
    public String chooseRole(@RequestParam("id") Long id, ModelMap model) throws Exception {
		SysUser dept = sysUserService.getModel(id);
		dept.setAuthenticator("UNCHANGED");
		model.addAttribute("user", dept);
		List<SysUserRole> roles = sysUserRoleService.findByUserId(id);
		model.addAttribute("roles", roles);
		return "security/user-choose-role";
    }
	
	@RequestMapping(value = "/save-roles.do")
	@ResponseBody
    public void saveRoles(@RequestParam("id")Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			List<Long> selected = getRequestArray(request, Long.class);
			sysUserRoleService.saveUserRoleShip(selected, id);
			writeResponse("保存成功", true, response);
		} catch (Exception e) {
			writeResponse("保存失败: " + e.getMessage(), false, response);
		}
    }
	
	@RequestMapping(value = "/edit.do")
    public String edit(@RequestParam(value = "id", required = false) Long id, ModelMap model) throws Exception {
		initDepartmentOptions();
		model.addAttribute("deptOptions", deptOptions);
		
		if(id == null) {
			model.addAttribute("user", new SysUser());
		}else {
			SysUser dept = sysUserService.getModel(id);
			model.addAttribute("oldAuthenticator", dept.getAuthenticator());
			dept.setAuthenticator("UNCHANGED");
			model.addAttribute("user", dept);
		}
		return "security/user-edit";
    }
	
	@RequestMapping(value = "/checkUsername.do")
	@ResponseBody
    public void checkUsername(@RequestParam("username")String username, @RequestParam("id")Long id, HttpServletResponse response) throws Exception {
		if (sysUserService.isUniqueAmongOthers(id, "username", username))
			writeJson(Boolean.TRUE, response);
		else 
			writeJson("用户名已经存在", response);
    }
	
	@RequestMapping(value = "/save.do")
	@ResponseBody
    public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SysUser dept = getRequestObject(request, SysUser.class, "id", sysUserService);
		String oldAuthenticator = (String)getRequestObject(request, Map.class).get("oldAuthenticator");
		try {
			dept.setFullName(dept.getLastName() + dept.getFirstName());
			if (dept.getAuthenticator() != null && !dept.getAuthenticator().equals("UNCHANGED")) {
				String newPwd = dept.getUsername() + dept.getAuthenticator();
				dept.setAuthenticator(HashUtils.getMD5(newPwd));
			} else {
				dept.setAuthenticator(oldAuthenticator);
			}
				
			sysUserService.save(dept, dept.getId()==null||dept.getId().equals(0L));
			writeResponse("保存成功", true, response);
		} catch (Exception e) {
			writeResponse("保存失败: " + e.getMessage(), false, response);
		}
    }
	
	@RequestMapping(value = "/del.do")
	@ResponseBody
    public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Long> ids = getRequestArray(request, Long.class);
		try {
			sysUserService.delete(ids.toArray(new Long[]{}));
			writeResponse("删除成功", true, response);
		} catch (Exception e) {
			writeResponse("删除失败: " + e.getMessage(), false, response);
		}
    }

	@RequestMapping(value = "/list.do")
	@ResponseBody
    public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JDatatableParams p = JDatatableParams.parseFromRequest(request);
		
		JDatatableOutput out = new JDatatableOutput(SysUser.class, SysUserExample.class);
		out.generateOutput(p, sysUserService);
		writeJson(out, response);
	}
}
