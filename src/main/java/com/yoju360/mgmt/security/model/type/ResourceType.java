package com.yoju360.mgmt.security.model.type;

import java.util.HashMap;
import java.util.Map;


public enum ResourceType {
	SYSTEM((short)0, "系统"), MENU((short)1, "菜单"), FUNCTION((short)2, "操作");

	public final short value;

	private final String desc;

	private ResourceType(short value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public static String getDesc(short type) {
		for (ResourceType enumType : ResourceType.values()) {
			if (enumType.value == type) {
				return enumType.getDesc();
			}
		}
		return "" + type;
	}
	
	public static Map<String, String> getSelectionOptions() {
		Map<String, String> options = new HashMap<String, String>();
		for (ResourceType enumType : ResourceType.values()) {
			options.put(enumType.value+"", enumType.getDesc());
		}
		return options;
	}
}