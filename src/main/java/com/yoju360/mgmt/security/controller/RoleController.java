/**
 * 
 */
package com.yoju360.mgmt.security.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.servlet.ModelAndView;

import com.yoju360.mgmt.core.controller.BaseController;
import com.yoju360.mgmt.core.controller.FueluxTree.TreeData;
import com.yoju360.mgmt.core.controller.FueluxTree.TreeItem;
import com.yoju360.mgmt.core.controller.JDatatableOutput;
import com.yoju360.mgmt.core.controller.JDatatableParams;
import com.yoju360.mgmt.security.InvocationSecurityMetadataSource;
import com.yoju360.mgmt.security.model.SysRole;
import com.yoju360.mgmt.security.model.SysRoleExample;
import com.yoju360.mgmt.security.service.SysResourceService;
import com.yoju360.mgmt.security.service.SysRoleResourceService;
import com.yoju360.mgmt.security.service.SysRoleService;

/**
 * @author evan
 *
 */
@Controller
@RequestMapping(value="/role")
public class RoleController extends BaseController<SysRole>{
	@Autowired
	SysRoleService sysRoleService;
	@Autowired
	SysResourceService sysResourceService;
	@Autowired
	SysRoleResourceService sysRoleResourceService;
	@Autowired
	InvocationSecurityMetadataSource securityMetadataSource;
	
	@RequestMapping(value = "/index.do")
    public ModelAndView index(HttpServletRequest request) throws Exception {
		return new ModelAndView("security/role");
    }
	
	@RequestMapping(value = "/show.do")
    public String show(@RequestParam("id") Long id, ModelMap model) throws Exception {
		SysRole dept = sysRoleService.getModel(id);
		model.addAttribute("role", dept);
		return "security/role-show";
    }
	
	@RequestMapping(value = "/edit.do")
    public String edit(@RequestParam(value = "id", required = false) Long id, ModelMap model) throws Exception {
		if(id == null) {
			model.addAttribute("role", null);
		}else {
			SysRole dept = sysRoleService.getModel(id);
			model.addAttribute("role", dept);
		}
		return "security/role-edit";
    }
	
	@RequestMapping(value = "/authorize.do")
    public String authorize(@RequestParam("id") Long id, ModelMap modelMap) throws Exception {
		SysRole model = sysRoleService.getModel(id);
		modelMap.addAttribute("model", model);
		return "security/role-authorize";
    }
	
	@RequestMapping(value = "/get-tree-data.do")
	@ResponseBody
    public void getAuthorizedTreeData(@RequestParam("parentId")Long parentId, @RequestParam("roleId") Long roleId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		TreeData ret = new TreeData();
		try {
			List<Map<String, Object>> list = sysResourceService.getAuthorizedTreeData(parentId, roleId);
			List<TreeItem> data = new ArrayList<TreeItem>();
			for (Map<String, Object> map : list) {
				TreeItem ti = new TreeItem();
				ti.text = map.get("title").toString();
				ti.type = ((Long)map.get("childCount"))>0?"folder":"item";
				Map<String, Object> addition = new HashMap<String, Object>();
				addition.putAll(map);
				addition.put("children", ((Long)map.get("childCount"))>0?true:false);
				if (((Long)map.get("granted"))>0)
					addition.put("item-selected", true);
				ti.additionalParameters = addition;
				data.add(ti);
			}
			ret.data = data;
			writeJson(ret, response);
		} catch (Exception e) {
			ret.status = "ERR";
			ret.message = e.getMessage();
			writeJson(ret, response);
		}
    }
	
	@RequestMapping(value = "/save-auth.do")
	@ResponseBody
    public void saveAuthorization(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			//depends on the tree is fully loaded! If some are invisible yet, only handle the visible part
			List<Long> selected = getRequestArray(request, "granted", Long.class);
			sysRoleResourceService.save(id, selected);
			securityMetadataSource.updateResource();
			writeResponse("保存成功", true, response);
		} catch (Exception e) {
			writeResponse("保存失败: " + e.getMessage(), false, response);
		}
    }
	
	@RequestMapping(value = "/save.do")
	@ResponseBody
    public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SysRole dept = getRequestObject(request, SysRole.class, "id", sysRoleService);
		try {
			sysRoleService.save(dept, dept.getId()==null);
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
			sysRoleService.delete(ids.toArray(new Long[]{}));
			writeResponse("删除成功", true, response);
		} catch (Exception e) {
			writeResponse("删除失败: " + e.getMessage(), false, response);
		}
    }

	@RequestMapping(value = "/list.do")
	@ResponseBody
    public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JDatatableParams p = JDatatableParams.parseFromRequest(request);
		
		JDatatableOutput out = new JDatatableOutput(SysRole.class, SysRoleExample.class);
		out.generateOutput(p, sysRoleService);
		writeJson(out, response);
	}
}
