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

	/**
	 * 该实体是否唯一
	 * @param selfId 实体Id
	 * @param columnName 用于鉴别唯一性的字段名
	 * @param value 唯一值
	 * @return
	 */
	boolean isUniqueAmongOthers(Long selfId, String columnName, Object value);
	/**
	 * 所有记录总数(未删除)
	 * @return
	 */
	int countAll();
	/**
	 * 由条件过滤后的记录数
	 * @param example
	 * @return
	 */
	int countByExample(E example);
	/**
	 * 按Example来查找实体列表，并分页返回
	 * @param example
	 * @param start
	 * @param count
	 * @return
	 */
	List<T> findByExamplePage(E example, int start, int count);
	/**
	 * 分页返回实体
	 * @param start
	 * @param count
	 * @return
	 */
	List<T> findByPage(int start, int count);
	/**
	 * 该实体管理模块的名称
	 * @return
	 */
	String getModuleName();
	/**
	 * 根据条件查找实体列表
	 * @param example
	 * @return
	 */
	List<T> findByExample(E example);

}