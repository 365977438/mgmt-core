package com.yoju360.mgmt.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.core.service.CoreService;
import com.yoju360.mgmt.core.service.ServiceException;
import com.yoju360.mgmt.security.mapper.SysDepartmentMapper;
import com.yoju360.mgmt.security.model.SysDepartment;
import com.yoju360.mgmt.security.model.SysDepartmentExample;
import com.yoju360.mgmt.security.model.SysUser;

@Service
@Transactional
public class SysDepartmentService extends CoreService<SysDepartment, SysDepartmentExample> {

	@Autowired
	private SysDepartmentMapper sysDepartmentMapper;

	@Autowired
	private SysUserService sysUserService;

	@Override
	protected BaseMapper<SysDepartment, SysDepartmentExample> getMapper() {
		return sysDepartmentMapper;
	}

	protected void beforeSave(SysDepartment entity, boolean operate) {
		if (!isUniqueAmongOthers(entity.getId(), "code", entity.getCode())) {
			throw new ServiceException("部门代码已经存在");
		}
		if (!isUniqueAmongOthers(entity.getId(), "title", entity.getTitle())) {
			throw new ServiceException("部门名称已经存在");
		}
	}

	protected void beforeDelete(Long id) {
		List<SysUser> list = sysUserService.findByDepartmentId(id);
		for (SysUser user : list) {
			user.setDepartmentId(null);
			sysUserService.save(user);
		}
	}

	@Override
	public String getModuleName() {
		return "部门管理";
	}
}
