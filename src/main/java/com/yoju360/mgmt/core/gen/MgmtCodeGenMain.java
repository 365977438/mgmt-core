/**
 * 
 */
package com.yoju360.mgmt.core.gen;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * @author evan
 *
 */
public class MgmtCodeGenMain {
	static String regex = "([a-z])([A-Z]+)";
	static String replacement = "$1_$2";
	static String CONTROLLER_TEMPLATE = "model-controller-java.ftl";
	static String VIEW_LIST_TEMPLATE = "model-list-jsp.jsp";
	static String VIEW_SHOW_TEMPLATE = "model-show-jsp.jsp";
	static String VIEW_EDIT_TEMPLATE = "model-edit-jsp.jsp";
	
	private static Configuration cfg;
	private static String toDir;
	/**
	 * @param args
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		cfg = new Configuration();
		cfg.setDefaultEncoding("utf-8");
		cfg.setClassForTemplateLoading(MgmtCodeGenMain.class, "");
		//cfg.setDirectoryForTemplateLoading(baseFile);
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	
		String configFile = args.length ==0? "/codegen_config.xml": args[0];
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(MgmtCodeGenMain.class.getResourceAsStream(configFile));
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
		
		Element root = doc.getDocumentElement();
		if (!root.getNodeName().equals("codegen")) {
			System.out.println("Wrong codege config file!");
			System.exit(1);
		}
		
		if (root.getAttribute("toDir")==null) {
			System.out.println("'toDir' is required for 'codegen' element");
			System.exit(1);
		}
		
		toDir = root.getAttribute("toDir");
		
		NodeList nList = doc.getElementsByTagName("model");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Element modelEle = (Element)nList.item(temp);
			String modelNameFull = modelEle.getAttribute("class");
			String modelName = modelNameFull.substring(modelNameFull.lastIndexOf(".")+1);
			String servicePkg = modelEle.getAttribute("servicePkg");
			String controllerPkg = modelEle.getAttribute("controllerPkg");
			String modelNameUncap = StringUtils.uncapitalize(modelName);
			String modelNameUncamel = modelName.replaceAll(regex, replacement).toLowerCase();
			String idField = modelEle.getAttribute("idField");
			String idFieldCap = StringUtils.capitalize(idField);
			String modelDesc = modelEle.getAttribute("name");
			String mgmtCode = modelEle.getAttribute("mgmtCode");
			
			System.out.println("Processing model: " + modelNameFull);
			
			// 1. generate controller code
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("modelNameFull", modelNameFull);
			dataMap.put("modelName", modelName);
			dataMap.put("servicePkg", servicePkg);
			dataMap.put("controllerPkg", controllerPkg);
			dataMap.put("modelNameUncap", modelNameUncap);
			dataMap.put("modelNameUncamel", modelNameUncamel);
			dataMap.put("idFieldCap", idFieldCap);
			dataMap.put("idField", idField);
			dataMap.put("mgmtCode", mgmtCode);
			
			writeTemp(dataMap, CONTROLLER_TEMPLATE, modelName + "Controller.java");
			
			// 2. generate freemarker jsp code
			// list 
			List<ModelField> modelFields = new ArrayList<ModelField>();
			Class<?> modelCls = Class.forName(modelNameFull);
			BeanInfo info = Introspector.getBeanInfo(modelCls);
		    for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
		    	ModelField field = new ModelField();
		    	field.setName(pd.getName());
		    	if (pd.getName().equals(idField))
		    		field.setId(true);
		    	field.setType(pd.getPropertyType().getName());
		    	modelFields.add(field);
		    }
		    dataMap.put("idField", idField);
		    dataMap.put("modelDesc", modelDesc);
		    dataMap.put("modelFields", modelFields);
		    
		    writeTemp(dataMap, VIEW_LIST_TEMPLATE, modelNameUncamel + ".jsp");
		    
			// 3. generate show
		    writeTemp(dataMap, VIEW_SHOW_TEMPLATE, modelNameUncamel + "-show.jsp");
		    
		    // 4. generate edit
		    writeTemp(dataMap, VIEW_EDIT_TEMPLATE, modelNameUncamel + "-edit.jsp");
		}
	}

	/**
	 * Write output base on template
	 * @param dataMap
	 * @param template
	 * @param outfile
	 */
	public static void writeTemp(Map<String, Object> dataMap,String template ,String outfile) {
		String resultStr = "";
		try {
			Template temp = cfg.getTemplate(template  ,"utf-8");
			Writer out = new CharArrayWriter();
			temp.process(dataMap, out);
			resultStr =  out.toString();
			out.flush();
			out.close();
			
			File outFile = new File(toDir + "/" + outfile);
			if(!outFile.getParentFile().exists()){
				outFile.getParentFile().mkdirs();
			}
			FileUtils.writeStringToFile(outFile, resultStr, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
