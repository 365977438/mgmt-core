/**
 * 
 */
package com.yoju360.mgmt.core.controller;

import java.util.List;
import java.util.Map;

/**
 * @author evan
 *
 */
public class FueluxTree {
	public static class TreeItem {
		public String text;
		public String type; // folder, item
		public Map additionalParameters; // id, type, etc
	}
	
	public static class TreeData {
		public String status = "OK"; // ERR
		public String message; // ERR message
		public List data;
	}
}
