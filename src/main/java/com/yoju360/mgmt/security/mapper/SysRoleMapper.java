package com.yoju360.mgmt.security.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.security.model.SysRole;
import com.yoju360.mgmt.security.model.SysRoleExample;

public interface SysRoleMapper extends BaseMapper<SysRole, SysRoleExample> {

	List<SysRole> findByUsername(String username);

	List<SysRole> findByUserId(@Param("userId")Long userId, @Param("status")boolean status);

	List<SysRole> findByUrl(@Param("url")String url, @Param("status")boolean status);
}