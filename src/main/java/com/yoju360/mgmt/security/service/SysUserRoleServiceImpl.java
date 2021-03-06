package com.yoju360.mgmt.security.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.core.service.AbstractCoreService;
import com.yoju360.mgmt.core.service.ServiceException;
import com.yoju360.mgmt.security.mapper.SysUserRoleMapper;
import com.yoju360.mgmt.security.model.SysUserRole;
import com.yoju360.mgmt.security.model.SysUserRoleExample;

/**
 * @Desc 用户角色关联业务类
 */
@Service("sysUserRoleService")
@Transactional
public class SysUserRoleServiceImpl extends AbstractCoreService<SysUserRole, SysUserRoleExample> implements SysUserRoleService {

	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;

	@Override
	protected BaseMapper<SysUserRole, SysUserRoleExample> getMapper() {
		return sysUserRoleMapper;
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.security.service.SysUserRoleService#saveUserRoleShip(java.util.List, java.lang.Long)
	 */
	@Override
	public void saveUserRoleShip(List<Long> roleIds, Long userId) {
		try {
			List<SysUserRole> exists = findByUserId(userId);
			List<SysUserRole> create = getCreateList(userId, roleIds, exists);
			List<SysUserRole> remove = getRemoveList(roleIds, exists);
			save(create.toArray(new SysUserRole[0]));
			for (SysUserRole membership : remove) {
				delete(membership.getId());
			}
		} catch (Exception e) {
			throw new ServiceException("用户角色关联失败",e);
		}
	}


	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.security.service.SysUserRoleService#findByUserId(java.lang.Long)
	 */
	@Override
	public List<SysUserRole> findByUserId(Long userId) {
		SysUserRoleExample e = new SysUserRoleExample();
		e.createCriteria().andUserIdEqualTo(userId);
		return sysUserRoleMapper.selectByExample(e);
	}

	/**
	 * 组装用户与角色的集合
	 * 
	 * @param userId
	 * @param roleIds
	 * @param exists
	 * @return
	 */
	private List<SysUserRole> getCreateList(Long userId, List<Long> roleIds, List<SysUserRole> exists) {
		List<SysUserRole> create = new ArrayList<SysUserRole>();
		boolean existsMembership = false;
		for (Long roleId : roleIds) {
			for (SysUserRole membership : exists) {
				if (membership.getRoleId().equals(roleId)) {
					existsMembership = true;
					break;
				}
			}
			if (existsMembership) {
				existsMembership = false;
			} else {
				SysUserRole membership = new SysUserRole();
				membership.setRoleId(roleId);
				membership.setUserId(userId);
				create.add(membership);
			}
		}
		return create;
	}

	/**
	 * 删除已存在的角色
	 * 
	 * @param roleId
	 * @param resourceIds
	 * @param exists
	 * @return
	 */
	private List<SysUserRole> getRemoveList(List<Long> roleIds, List<SysUserRole> exists) {
		List<SysUserRole> remove = new LinkedList<SysUserRole>();
		boolean existsMembership = false;
		for (SysUserRole membership : exists) {
			for (Long roleId : roleIds) {
				if (membership.getRoleId().equals(roleId)) {
					existsMembership = true;
					break;
				}
			}
			if (existsMembership) {
				existsMembership = false;
			} else {
				remove.add(membership);
			}
		}
		return remove;
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.security.service.SysUserRoleService#getUserIdByRole(java.lang.Long)
	 */
	@Override
	public List<SysUserRole> getUserIdByRole(Long roleId) {
		SysUserRoleExample e = new SysUserRoleExample();
		e.createCriteria().andRoleIdEqualTo(roleId);
		return sysUserRoleMapper.selectByExample(e);
	}

	@Override
	public String getModuleName() {
		return "角色管理";
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.security.service.SysUserRoleService#delete(java.util.List)
	 */
	@Override
	public void delete(List<SysUserRole> list) {
		for (SysUserRole sur : list) {
			delete(sur.getId());
		}
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.security.service.SysUserRoleService#deleteByRoleId(java.lang.Long)
	 */
	@Override
	public void deleteByRoleId(Long id) {
		SysUserRoleExample e = new SysUserRoleExample();
		e.createCriteria().andRoleIdEqualTo(id);
		sysUserRoleMapper.deleteByExample(e);
	}
}
