package com.yoju360.mgmt.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface BaseMapper<T, E> {
	T get(Long id);
	int count();
    int countByExample(@Param("example")E example);
    int add(T entity);
    int delete(Long id);
    int deleteByExample(@Param("example")E example);
    int update(T entity);
    List<T> selectAll();
    List<T> selectByExample(@Param("example")E example);
    List<T> selectByPage(@Param("start")int start, @Param("count")int count);
    List<T> selectByExamplePage(@Param("example")E example, @Param("start")int start, @Param("count")int count);
    int updateByExample(@Param("record")T record, @Param("example")E example);
	boolean isUniqueAmongOthers(@Param("id")Long id,@Param("columnName")String columnName, @Param("value")Object value);
}