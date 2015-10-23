/**
 * 
 */
package com.yoju360.mgmt.core.controller;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.yoju360.mgmt.core.controller.JDatatableParams.Column;
import com.yoju360.mgmt.core.controller.JDatatableParams.Search;
import com.yoju360.mgmt.core.model.ModelExampleHelper;
import com.yoju360.mgmt.core.service.CoreService;

/**
 * @author evan
 *
 */
@SuppressWarnings("rawtypes")
public class JDatatableOutput {
	private static final Logger logger = LoggerFactory.getLogger(JDatatableOutput.class);
	private int draw;
	private int recordsTotal;
	private int recordsFiltered;
	private List<?> data;
	private String error;
	
	private Class<?> modelClazz;
	private Class<?> exampleClazz;
	
	public JDatatableOutput() {
		
	}
	
	public JDatatableOutput(Class<?> modelClazz, Class<?> exampleClazz) {
		this.modelClazz = modelClazz;
		this.exampleClazz = exampleClazz;
	}
	
	@SuppressWarnings("unchecked")
	public void generateOutput(JDatatableParams p, CoreService service, Object example) {
		try {
			if (p.getOrders().size()>0 && example != null) {
				ModelExampleHelper.addOrder2Example(p.getOrderFields(), example);
			}
			draw = p.getDraw();
			recordsTotal = service.countAll();
			if (example != null) {
				recordsFiltered = service.countByExample(example);
				if (p.getLength()>0)
					data = service.findByExamplePage(example, p.getStart(), p.getLength());
				else
					data = service.findByExample(example);
			} else {
				recordsFiltered = recordsTotal;
				data = service.findByPage(p.getStart(), p.getLength());
			}
		} catch (Exception e) {
			logger.error("Failed to get jquery datatable output", e);
			error = "获取数据失败: " + e.getMessage();
		}
	}
	
	public void generateOutput(JDatatableParams p, CoreService service) {
		generateOutput(p, service, false);
	}
	
	@SuppressWarnings("unchecked")
	public void generateAndOutput(JDatatableParams p, CoreService service, boolean andSearch){
		try {
			Object example = null;
			
			if (p.getOrders().size()>0) {
				example = exampleClazz.newInstance();
				ModelExampleHelper.addOrder2Example(p.getOrderFields(), example);
			}
			
			if (p.getSearch()!=null && p.getSearch().value.length()>0) {
				if (example == null) 
					example = exampleClazz.newInstance();
				Object model = modelClazz.newInstance();
				Map<String, String> fieldsOperator = setModelAttributesAndOperators(model, p);
				ModelExampleHelper.model2Example(model, example, fieldsOperator, andSearch);
				
			}
			
			if(p.getColumns() != null && !p.getColumns().isEmpty()){
				boolean isColumns = false;
				for (Column col : p.getColumns()) {
					String val = col.searchValue;
					if(!StringUtils.isEmpty(val)){
						isColumns = true;
					}
				}
				if(isColumns){
					//处理and语句
					Object model = modelClazz.newInstance();
					Map<String, String> fieldsMap = setModelAttributes(model, p);
					ModelExampleHelper.andModel2Example(model, example, fieldsMap, andSearch);
				}
			}
			draw = p.getDraw();
			recordsTotal = service.countAll();
			if (example != null) {
				recordsFiltered = service.countByExample(example);
				if (p.getLength()>0)
					data = service.findByExamplePage(example, p.getStart(), p.getLength());
				else
					data = service.findByExample(example);
			} else {
				recordsFiltered = recordsTotal;
				if (p.getLength()>0)
					data = service.findByPage(p.getStart(), p.getLength());
				else
					data = service.findAll();
			}
		} catch (Exception e) {
			logger.error("Failed to get jquery datatable output", e);
			error = "获取数据失败: " + e.getMessage();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void generateOutput(JDatatableParams p, CoreService service, boolean andSearch) {
		try {
			Object example = null;
			
			if (p.getOrders().size()>0) {
				example = exampleClazz.newInstance();
				ModelExampleHelper.addOrder2Example(p.getOrderFields(), example);
			}
			
			if (p.getSearch()!=null && p.getSearch().value.length()>0) {
				if (example == null) 
					example = exampleClazz.newInstance();
				Object model = modelClazz.newInstance();
				Map<String, String> fieldsOperator = setModelAttributesAndOperators(model, p);
				ModelExampleHelper.model2Example(model, example, fieldsOperator, andSearch);
			}
			
			draw = p.getDraw();
			recordsTotal = service.countAll();
			if (example != null) {
				recordsFiltered = service.countByExample(example);
				if (p.getLength()>0)
					data = service.findByExamplePage(example, p.getStart(), p.getLength());
				else
					data = service.findByExample(example);
			} else {
				recordsFiltered = recordsTotal;
				if (p.getLength()>0)
					data = service.findByPage(p.getStart(), p.getLength());
				else
					data = service.findAll();
			}
		} catch (Exception e) {
			logger.error("Failed to get jquery datatable output", e);
			error = "获取数据失败: " + e.getMessage();
		}
		
	}
	
	public Map<String, String> setModelAttributesAndOperators(Object model,
			JDatatableParams p) throws IntrospectionException {
		Map<String, String> ret = new HashMap<String, String>();
		Search s = p.getSearch();
		String v = s.value;
		BeanInfo info = Introspector.getBeanInfo(model.getClass());
		for (Column c : p.getColumns()) {
			if (v.length()==0)
				continue;
			if (c.searchable) {
				// set field value, try best to convert v type to bean prop type
				try {
					PropertyDescriptor pd = getBeanProp(info, c.data);
					if (pd!=null) {
						Class<?> targetType = pd.getPropertyType();
						Object actVal = convert(v, targetType);
						if (actVal != null) {
							pd.getWriteMethod().invoke(model, actVal);
							if (targetType.equals(java.lang.String.class)) { // depends on model field type
								ret.put(c.data, ModelExampleHelper.LIKE);
							} else {
								ret.put(c.data, ModelExampleHelper.EQ);
							}
						}
					}
				} catch (Exception e) {
					logger.error("Failed to set " + c.data + " with " + v, e);
					continue;
				}
			}
		}

		return ret;
	}
	
	public Map<String, String> setModelAttributes(Object model,JDatatableParams p) throws IntrospectionException{
		Map<String, String> ret = new HashMap<String, String>();
		BeanInfo info = Introspector.getBeanInfo(model.getClass());
		for (Column c : p.getColumns()) {
			String searchVal =  c.searchValue;
			if (searchVal.length()==0)
				continue;
			if (!StringUtils.isEmpty(searchVal)) {
				// set field value, try best to convert v type to bean prop type
				try {
					PropertyDescriptor pd = getBeanProp(info, c.data);
					if (pd!=null) {
						Class<?> targetType = pd.getPropertyType();
						Object actVal = convert(searchVal, targetType);
						if (actVal != null) {
							pd.getWriteMethod().invoke(model, actVal);
							if (targetType.equals(java.lang.String.class)) { // depends on model field type
								ret.put(c.data, ModelExampleHelper.LIKE);
							} else {
								ret.put(c.data, ModelExampleHelper.EQ);
							}
						}
					}
				} catch (Exception e) {
					logger.error("Failed to set " + c.data + " with " + searchVal, e);
					continue;
				}
			}
		}

		return ret;
	}
	
	private Object convert(String v, Class<?> clazz) {
		try {
			return ConvertUtils.convert(v, clazz);
		} catch (Exception e){
			// ignore, allow some field failure
			if (logger.isDebugEnabled())
				logger.debug("Failed converting " + v + " to " + clazz);
		}
		return null;
	}

	private PropertyDescriptor getBeanProp(BeanInfo info, String name) {
	    for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
	    	if (pd.getName().equals(name))
	    		return pd;
	    }
	    return null;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
