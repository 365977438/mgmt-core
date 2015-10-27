/**
 * 
 */
package com.yoju360.mgmt.security.model;

import java.util.ArrayList;
import java.util.List;

import com.yoju360.mgmt.security.model.SysResource;

/**
 * @author evan
 *
 */
public class MenuNode {
	public MenuNode() {
		
	}
	public MenuNode(Long id, SysResource data) {
		this.id = id;
		this.data = data;
	}
	
	public Long id;
	public SysResource data;
	public List<MenuNode> children=new ArrayList<MenuNode>();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public SysResource getData() {
		return data;
	}
	public void setData(SysResource data) {
		this.data = data;
	}
	public List<MenuNode> getChildren() {
		return children;
	}
	public void setChildren(List<MenuNode> children) {
		this.children = children;
	}
}
