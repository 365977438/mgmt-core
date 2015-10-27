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
import com.yoju360.mgmt.security.model.SysResource;
import com.yoju360.mgmt.security.model.SysResourceExample;
import com.yoju360.mgmt.security.model.type.ResourceType;
import com.yoju360.mgmt.security.service.SysResourceService;

/**
 *
 */
@Controller
@RequestMapping(value="/sys_resource")
public class SysResourceController extends BaseController<SysResource>{
	@Autowired
	SysResourceService sysResourceService;
//	@Autowired
//	InvocationSecurityMetadataSource securityMetadataSource;
	
	@RequestMapping(value = "/index.do")
    public ModelAndView index(HttpServletRequest request) throws Exception {
		return new ModelAndView("security/sys_resource");
    }
	
	@RequestMapping(value = "/show.do")
    public String show(@RequestParam("id") Long id, ModelMap modelMap) throws Exception {
		SysResource model = sysResourceService.getModel(id);
		modelMap.addAttribute("model", model);
		if (model.getParentId()!=null && model.getParentId()!=0L) {
			SysResource parent = sysResourceService.getModel(model.getParentId());
			modelMap.addAttribute("parentName", parent.getTitle());
		}
		modelMap.addAttribute("resourceTypeOptions", ResourceType.getSelectionOptions());
		return "security/sys_resource-show";
    }
	
	@RequestMapping(value = "/edit.do")
    public String edit(@RequestParam(value="id",required=false) Long id, @RequestParam("isAdd") Boolean isAdd, ModelMap modelMap) throws Exception {
		modelMap.addAttribute("resourceTypeOptions", ResourceType.getSelectionOptions());
		if(isAdd) {
			SysResource model = new SysResource();
			if (id!=null) {
				SysResource parent = sysResourceService.getModel(id);
				model.setParentId(id);
				model.setSystemName(parent.getSystemName());
				model.setType(ResourceType.MENU.value);
				modelMap.addAttribute("parentName", parent.getTitle());
			} else {
				model.setType(ResourceType.SYSTEM.value);
			}
			modelMap.addAttribute("model", model);
		}else {
			SysResource model = sysResourceService.getModel(id);
			if (model.getParentId()!=null && model.getParentId()!=0L) {
				SysResource parent = sysResourceService.getModel(model.getParentId());
				modelMap.addAttribute("parentName", parent.getTitle());
			}
			// parent can't change?
			modelMap.addAttribute("model", model);
		}
		return "security/sys_resource-edit";
    }
	
	@RequestMapping(value = "/checkResourceCode.do")
	@ResponseBody
    public void checkResourceCode(@RequestParam(value="id", required=false) Long id, @RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (sysResourceService.isUniqueAmongOthers(id, "code", code)) {
			writeJson(true, response);
		} else {
			writeJson("资源编码已经被使用", response);
		}
    }
	
	@RequestMapping(value = "/save.do")
	@ResponseBody
    public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SysResource model = getRequestObject(request, SysResource.class, "id", sysResourceService);
		try {
			sysResourceService.save(model, model.getId()==null || model.getId()==0L);
//			securityMetadataSource.updateResource();
			writeResponse("保存成功", true, response);
		} catch (Exception e) {
			writeResponse("保存失败: " + e.getMessage(), false, response);
		}
    }
	
	@RequestMapping(value = "/del.do")
	@ResponseBody
    public void delete(@RequestParam("id")Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			sysResourceService.delete(id);
			writeResponse("删除成功", true, response);
		} catch (Exception e) {
			writeResponse("删除失败: " + e.getMessage(), false, response);
		}
    }

	@RequestMapping(value = "/list.do")
	@ResponseBody
    public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JDatatableParams p = JDatatableParams.parseFromRequest(request);
		
		JDatatableOutput out = new JDatatableOutput(SysResource.class, SysResourceExample.class);
		out.generateOutput(p, sysResourceService);
		writeJson(out, response);
	}
	
	@RequestMapping(value = "/get-tree-data.do")
	@ResponseBody
    public void getTreeData(@RequestParam("parentId")Long parentId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		TreeData ret = new TreeData();
		try {
			List<Map<String, Object>> list = sysResourceService.getTreeDataByParentId(parentId);
			List<TreeItem> data = new ArrayList<TreeItem>();
			for (Map<String, Object> map : list) {
				TreeItem ti = new TreeItem();
				ti.text = map.get("title").toString();
				ti.type = ((Long)map.get("childCount"))>0?"folder":"item";
				Map<String, Object> addition = new HashMap<String, Object>();
				addition.putAll(map);
				addition.put("children", ((Long)map.get("childCount"))>0?true:false);
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
}
