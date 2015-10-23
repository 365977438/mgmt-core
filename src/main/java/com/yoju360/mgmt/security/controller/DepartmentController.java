/**
 * 
 */
package com.yoju360.mgmt.security.controller;

import java.util.List;

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
import com.yoju360.mgmt.core.controller.JDatatableOutput;
import com.yoju360.mgmt.core.controller.JDatatableParams;
import com.yoju360.mgmt.security.model.SysDepartment;
import com.yoju360.mgmt.security.model.SysDepartmentExample;
import com.yoju360.mgmt.security.service.SysDepartmentService;

/**
 * @author evan
 *
 */
@Controller
@RequestMapping(value="/department")
public class DepartmentController extends BaseController<SysDepartment>{
	@Autowired
	SysDepartmentService sysDepartmentService;
	
	@RequestMapping(value = "/index.do")
    public ModelAndView index(HttpServletRequest request) throws Exception {
		return new ModelAndView("security/department");
    }
	
	@RequestMapping(value = "/show.do")
    public String show(@RequestParam("id") Long id, ModelMap model) throws Exception {
		SysDepartment dept = sysDepartmentService.getModel(id);
		model.addAttribute("department", dept);
		return "security/department-show";
    }
	
	@RequestMapping(value = "/show-in-tab.do")
    public String showInTab(@RequestParam(value = "id", required = false) Long id, ModelMap model) throws Exception {
		if(id == null) {
			model.addAttribute("department", null);
		}else {
			SysDepartment dept = sysDepartmentService.getModel(id);
			model.addAttribute("department", dept);
		}
		return "security/department-show-in-tab";
    }
	
	@RequestMapping(value = "/save.do")
	@ResponseBody
    public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SysDepartment dept = getRequestObject(request, SysDepartment.class, "id", sysDepartmentService);
		try {
			sysDepartmentService.save(dept, dept.getId()==null);
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
			sysDepartmentService.delete(ids.toArray(new Long[]{}));
			writeResponse("删除成功", true, response);
		} catch (Exception e) {
			writeResponse("删除失败: " + e.getMessage(), false, response);
		}
    }

	@RequestMapping(value = "/list.do")
	@ResponseBody
    public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JDatatableParams p = JDatatableParams.parseFromRequest(request);
		
		JDatatableOutput out = new JDatatableOutput(SysDepartment.class, SysDepartmentExample.class);
		out.generateOutput(p, sysDepartmentService);
		writeJson(out, response);
	}
}
