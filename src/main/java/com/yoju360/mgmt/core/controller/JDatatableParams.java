/**
 * 
 */
package com.yoju360.mgmt.core.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author evan
 *
 */
public class JDatatableParams {
	public static class Column {
		public int index;
		public String name;
		public String data;
		public boolean searchable;
		public boolean orderable;
		public String searchValue;
		public boolean searchRegex;
	}
	
	public static class Order {
		public int index;
		public int columnIndex;
		public String dir;
	}
	
	public static class Search {
		public String value;
		public boolean regex;
	}
	
	private ArrayList<Column> columns = new ArrayList<Column>();
	private ArrayList<Order> orders = new ArrayList<Order>();
	private Search search;
	
	/** page start */
	private int start;
	/** page size */
	private int length;
	/** request seq num */
	private int draw;
	
	public JDatatableParams() {
	}
	
	public Map<String, String> getOrderFields() {
		Map<String, String> map = new HashMap<String, String>();
		for (Order o : orders) {
			Column col = columns.get(o.columnIndex);
			map.put(col.data, o.dir);
		}
		return map;
	}
	
	@SuppressWarnings("rawtypes")
	public static JDatatableParams parseFromRequest(HttpServletRequest request) {
		JDatatableParams p = new JDatatableParams();
		Enumeration names = request.getParameterNames();
		Map<Integer, Column> colMap = new HashMap<Integer, Column>();
		Map<Integer, Order> ordMap = new HashMap<Integer, Order>();
		while (names.hasMoreElements()) {
			String key = (String)names.nextElement();
			
			if (key.startsWith("columns")) {
				int index = Integer.parseInt(key.substring(8, key.indexOf("]")));
				Column c = null;
				if (colMap.get(index)==null) {
					c = new Column();
					c.index = index;
					colMap.put(index, c);
				} else {
					c = colMap.get(index);
				}
				if (key.endsWith("[data]"))
					c.data = request.getParameter(key);
				if (key.endsWith("[name]"))
					c.name = request.getParameter(key);
				if (key.endsWith("[searchable]"))
					c.searchable = Boolean.parseBoolean(request.getParameter(key));
				if (key.endsWith("[orderable]"))
					c.orderable = Boolean.parseBoolean(request.getParameter(key));
				if (key.endsWith("[search][value]"))
					c.searchValue = request.getParameter(key);
				if (key.endsWith("[search][regex]"))
					c.searchRegex = Boolean.parseBoolean(request.getParameter(key));
			} else if (key.startsWith("order")) {
				int index = Integer.parseInt(key.substring(6, key.indexOf("]")));
				Order o = null;
				if (ordMap.get(index)==null) {
					o = new Order();
					o.index = index;
					ordMap.put(index, o);
				} else {
					o = ordMap.get(index);
				}
				if (key.endsWith("[column]"))
					o.columnIndex = Integer.parseInt(request.getParameter(key));
				if (key.endsWith("[dir]"))
					o.dir = request.getParameter(key);
			} else if (key.startsWith("search")) {
				if (p.search==null)
					p.search = new Search();
				
				if (key.equals("search[value]"))
					p.search.value = request.getParameter(key);
				if (key.equals("search[regex]"))
					p.search.regex = Boolean.parseBoolean(request.getParameter(key));
			}
		}
		p.columns.addAll(colMap.values());
		Collections.sort(p.columns, new Comparator<Column>(){
			@Override
			public int compare(Column o1, Column o2) {
				return o1.index - o2.index;
			}
		});
		p.orders.addAll(ordMap.values());
		Collections.sort(p.orders, new Comparator<Order>(){
			@Override
			public int compare(Order o1, Order o2) {
				return o1.index - o2.index;
			}
		});
		p.setStart(Integer.parseInt(request.getParameter("start")));
		p.setLength(Integer.parseInt(request.getParameter("length")));
		p.setDraw(Integer.parseInt(request.getParameter("draw")));
		return p;
	}
	
	public ArrayList<Column> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<Order> orders) {
		this.orders = orders;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	
}
