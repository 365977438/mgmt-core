/**
 * 
 */
package ${controllerPkg};

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
import ${modelNameFull};
import ${modelNameFull}Example;
import ${servicePkg}.${modelName}Service;

/**
 * auto generated code
 *
 */
@Controller
@RequestMapping(value="/${modelNameUncamel}")
public class ${modelName}Controller extends BaseController<${modelName}>{
	@Autowired
	${modelName}Service ${modelNameUncap}Service;
	
	@RequestMapping(value = "/index.do")
    public ModelAndView index(HttpServletRequest request) throws Exception {
		return new ModelAndView("${modelNameUncamel}");
    }
	
	@RequestMapping(value = "/show.do")
    public String show(@RequestParam("id") Long id, ModelMap modelMap) throws Exception {
		${modelName} model = ${modelNameUncap}Service.getModel(id);
		modelMap.addAttribute("model", model);
		return "${modelNameUncamel}-show";
    }
	
	@RequestMapping(value = "/edit.do")
    public String edit(@RequestParam(value = "id", required = false) Long id, ModelMap modelMap) throws Exception {
		if(id == null) {
			modelMap.addAttribute("model", null);
		}else {
			${modelName} model = ${modelNameUncap}Service.getModel(id);
			modelMap.addAttribute("model", model);
		}
		return "${modelNameUncamel}-edit";
    }
	
	@RequestMapping(value = "/save.do")
	@ResponseBody
    public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		${modelName} model = getRequestObject(request, ${modelName}.class, "${idField}", ${modelNameUncap}Service);
		try {
			${modelNameUncap}Service.save(model, model.get${idFieldCap}()==null || model.get${idFieldCap}().equals(0L));
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
			${modelNameUncap}Service.delete(ids.toArray(new Long[]{}));
			writeResponse("删除成功", true, response);
		} catch (Exception e) {
			writeResponse("删除失败: " + e.getMessage(), false, response);
		}
    }

	@RequestMapping(value = "/list.do")
	@ResponseBody
    public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JDatatableParams p = JDatatableParams.parseFromRequest(request);
		
		JDatatableOutput out = new JDatatableOutput(${modelName}.class, ${modelName}Example.class);
		out.generateOutput(p, ${modelNameUncap}Service);
		writeJson(out, response);
	}
}
