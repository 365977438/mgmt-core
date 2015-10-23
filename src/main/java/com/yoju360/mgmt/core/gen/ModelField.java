package com.yoju360.mgmt.core.gen;


public class ModelField {

	private String table;

	private String name;

	private String desc;

	private String type;

	private int length;

	private boolean nullable;

	private boolean id;

	private boolean unique;

	public String getFreemarkerVariable() {
		return "${(model." + name + ")!}";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public String getTable() {
		return table;
	}

	public boolean isId() {
		return id;
	}

	public long getMin() {
		return 0;
	}

	public long getMax() {
		return Long.MAX_VALUE;
	}

	public void setId(boolean id) {
		this.id = id;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getDesc() {
		if (desc!=null && desc.length()>0) {
			if (this.isEnum()) {
				String[] values = getSplitDesc();
				if (split(values[0]).length > 1) {
					return getMethodName();
				} else {
					return values[0];
				}
			} else {
				return desc;
			}
		} else {
			return getName();
		}
	}

	public String[] split(String value) {
		String[] values = value.split("：");
		if (values.length == 1) {
			values = value.split(":");
		}
		return values;
	}

	public String[] getSplitDesc() {
		if (desc==null || desc.length()<=0) {
			return new String[] { "" };
		}
		String[] values = desc.split("\r\n");
		if (values.length == 1) {
			values = desc.split("\n");
		}
		return values;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getType() {
		if (isEnum()) {
			return type.equals("String") ? "String" : "int";
		} else {
			return type;
		}

	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isRadio() {
		if (isEnum()) {
			String[] enums = getSplitDesc();
			String[] enumEntry = split(enums[0]);
			if (enumEntry.length == 1) {
				return enums.length == 3;
			} else {
				return enums.length == 2;
			}
		} else {
			return false;
		}
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMethodName() {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public String getEnumFieldName(){
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public boolean isEnum() {
		if (desc==null || desc.length()<=0) {
			return false;
		} else {
			return getSplitDesc().length > 1 && split(getSplitDesc()[1]).length > 1;
		}
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getValidate() {
		String msg = "";
		if (!isNullable()) {
			msg = "required='true'";
			msg = String.format(msg, getDesc());
		}
		if (getType().equals("String")) {
			long min = getMin() > 0 ? getMin() : 0;
			long max = getLength();
			msg += " class='easyui-textbox' validType='length[%d,%d]'";
			msg = String.format(msg, min, max, getDesc(), min, max);
		} else if (getType().equals("Long") || getType().equals("Integer") || getType().equals("int") || getType().equals("Double")) {
			long min = getMin();
			long max = getMax();
			msg += " class='easyui-numberbox' min='%d' max='%d'";
			msg = String.format(msg, min, max, getDesc(), min, max);
		}

		return msg;
	}
}
