package com.yoju360.mgmt.core.service;

import java.util.Collection;
import java.util.List;

import com.yoju360.mgmt.core.model.BaseModel;

public interface CoreService<T extends BaseModel, E> {

	/**
	 * 保存
	 * @param entity obj_version=0新增，!=0修改
	 */
	boolean save(T entity);

	/**
	 * 保存
	 * @param operate true=新增, false=修改
	 * */
	boolean save(T entity, boolean operate);

	/**
	 * 批量新增实体
	 * 
	 * */
	void save(T[] entitys);

	/**
	 * 批量新增实体
	 * 
	 * */
	void save(Collection<T> entitys);

	/**
	 * 批量保存实体
	 * 
	 * */
	void save(Collection<T> entitys, boolean operate);

	/**
	 * 批量保存实体
	 * 
	 * */
	void save(T[] entitys, boolean operate);

	/**
	 * 删除
	 * 
	 * @param id
	 */
	void delete(Long id);

	/**
	 * 判断实体是否被其它表使用
	 * 
	 * */
	boolean isModelBeingReferenced(String targetTable, String targetColumn, Object value);

	/**
	 * 删除
	 * 
	 * @param ids
	 */
	void delete(Long ids[]);

	/**
	 * 查询单个
	 * 
	 * @param id
	 * @return
	 */
	T getModel(Long id);

	/**
	 * 获取所有实体
	 * 
	 */
	List<T> findAll();

	boolean isUniqueAmongOthers(Long selfId, String columnName, Object value);

	int countAll();

	int countByExample(E example);

	List<T> findByExamplePage(E example, int start, int count);

	List<T> findByPage(int start, int count);

	String getModuleName();

	List<T> findByExample(E example);

}