package com.yoju360.mgmt.core.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.Converter;

import com.alibaba.fastjson.JSONArray;

public class ListConverter implements Converter {
	@Override
	public Object convert(Class type, Object value) {
		JSONArray tempJsonArray = (JSONArray)value;
		List<Object> list = new ArrayList<Object>();
		for(Object t : tempJsonArray) {
			list.add(t);
		}
		return list;
	}
}
