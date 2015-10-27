package com.yoju360.mgmt.security.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.core.service.AbstractCoreService;
import com.yoju360.mgmt.core.service.ServiceException;
import com.yoju360.mgmt.security.mapper.SysRoleResourceMapper;
import com.yoju360.mgmt.security.model.SysRoleResource;
import com.yoju360.mgmt.security.model.SysRoleResourceExample;

@Service("sysRoleResourceService")
@Transactional
public class SysRoleResourceServiceImpl extends AbstractCoreService<SysRoleResource, SysRoleResourceExample> implements SysRoleResourceService {

	@Autowired
	private SysRoleResourceMapper sysRoleResourceMapper;

	public void save(Long roleId, List<Long> selected) {
		try {
			List<SysRoleResource> old = findByRoleId(roleId); // old selected
			List<SysRoleResource> create = getCreateList(roleId, selected, old);
			List<SysRoleResource> remove = getRemoveList(selected, old);
			if (!create.isEmpty()) {
				save(create.toArray(new SysRoleResource[0]));
			}
			for (SysRoleResource permission : remove) {
				delete(permission.getId());
			}
		} catch (Exception e) {
			throw new ServiceException("保存资源失败", e);
		}

	}

	private List<SysRoleResource> getRemoveList(List<Long> resourceIds, List<SysRoleResource> exists) {
		List<SysRoleResource> remove = new LinkedList<SysRoleResource>();
		boolean existsPermission = false;
		for (SysRoleResource permission : exists) {
			for (Long resourceid : resourceIds) {
				if (permission.getResourceId().equals(resourceid)) {
					existsPermission = true;
					break;
				}
			}
			if (existsPermission) {
				existsPermission = false;
			} else {
				remove.add(permission);
			}
		}
		return remove;
	}

	private List<SysRoleResource> getCreateList(Long roleId, List<Long> resourceIds, List<SysRoleResource> exists) {
		List<SysRoleResource> create = new LinkedList<SysRoleResource>();
		if (resourceIds.size() == 0) {
			return create;
		}
		
		boolean existsPermission = false;
		for (Long resourceid : resourceIds) {
			for (SysRoleResource permission : exists) {
				if (permission.getResourceId().equals(resourceid)) {
					existsPermission = true;
					break;
				}
			}
			if (existsPermission) {
				existsPermission = false;
				continue;
			} else {
				SysRoleResource permission = new SysRoleResource();
				permission.setRoleId(roleId);
				permission.setResourceId(resourceid);
				create.add(permission);
			}
		}
		return create;
	}

	@Override
	protected BaseMapper<SysRoleResource, SysRoleResourceExample> getMapper() {
		return sysRoleResourceMapper;
	}

	@Override
	public String getModuleName() {
		return "权限管理";
	}

	public void delete(List<SysRoleResource> spList) {
		for (SysRoleResource srr : spList) {
			delete(srr.getId());
		}
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.security.service.SysRoleResourceService#findByResourceId(java.lang.Long)
	 */
	@Override
	public List<SysRoleResource> findByResourceId(Long id) {
		SysRoleResourceExample e = new SysRoleResourceExample();
		e.createCriteria().andResourceIdEqualTo(id);
		return sysRoleResourceMapper.selectByExample(e);
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.security.service.SysRoleResourceService#findByRoleId(java.lang.Long)
	 */
	@Override
	public List<SysRoleResource> findByRoleId(Long roleId) {
		SysRoleResourceExample e = new SysRoleResourceExample();
		e.createCriteria().andRoleIdEqualTo(roleId);
		return sysRoleResourceMapper.selectByExample(e);
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.security.service.SysRoleResourceService#deleteByRoleId(java.lang.Long)
	 */
	@Override
	public void deleteByRoleId(Long id) {
		SysRoleResourceExample e = new SysRoleResourceExample();
		e.createCriteria().andRoleIdEqualTo(id);
		sysRoleResourceMapper.deleteByExample(e);
	}
}
