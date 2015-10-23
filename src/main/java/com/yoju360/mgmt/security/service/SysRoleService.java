package com.yoju360.mgmt.security.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.core.service.CoreService;
import com.yoju360.mgmt.core.service.ServiceException;
import com.yoju360.mgmt.security.mapper.SysRoleMapper;
import com.yoju360.mgmt.security.model.SysRole;
import com.yoju360.mgmt.security.model.SysRoleExample;

/**
 * 角色管理
 * 
 * 
 */
@Service
@Transactional
public class SysRoleService extends CoreService<SysRole, SysRoleExample>{

	@Autowired
	private SysRoleMapper sysRoleMapper;

	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	SysRoleResourceService sysRoleResourceService;
	
	/**/
	@Override
	protected void beforeSave(SysRole entity, boolean operate) {
		if (!isUniqueAmongOthers(entity.getId(), "code", entity.getCode())) {
			throw new ServiceException("角色代码已经存在");
		}
		if (!isUniqueAmongOthers(entity.getId(), "title", entity.getTitle())) {
			throw new ServiceException("角色名称已经存在");
		}
	}

	protected void beforeDelete(SysRole entity) {
		if ("admin".equals(entity.getCode())) {
			throw new ServiceException("不能删除管理员角色");
		}
		sysUserRoleService.deleteByRoleId(entity.getId());
		sysRoleResourceService.deleteByRoleId(entity.getId());
	}
	
	public SysRole findByRoleCode(String roleCode) {
		SysRoleExample e = new SysRoleExample();
		e.createCriteria().andCodeEqualTo(roleCode);
		List<SysRole> lst = sysRoleMapper.selectByExample(e);
		return lst.size()>0?lst.get(0):null;
	}
	
	/**
	 * 获取用户的角色
	 * @param username
	 * @return
	 */
	public List<SysRole> findByUsername(String username) {
		try {
			return sysRoleMapper.findByUsername(username);
		} catch (Exception e) {
			throw new ServiceException("获取角色失败", e);
		}
	}
	
	public List<SysRole> findByUserId(Long userId) {
		try {
			return sysRoleMapper.findByUserId(userId, true);
		} catch (Exception e) {
			throw new ServiceException("获取角色失败", e);
		}
	}

	public List<SysRole> findByUrl(String url) {
		try {
			return sysRoleMapper.findByUrl(url, true);
		} catch (Exception e) {
			throw new ServiceException("获取角色失败", e);
		}
	}

	public Set<GrantedAuthority> findGrantedAuthorityByUsername(String username) {
		try {
			List<SysRole> list = sysRoleMapper.findByUsername(username);
			Set<GrantedAuthority> set = new HashSet<GrantedAuthority>();
			for (SysRole role : list) {
				set.add(new SimpleGrantedAuthority(role.getTitle()));
			}
			return set;
		} catch (Exception e) {
			throw new ServiceException("获取权限失败", e);
		}
	}

	public Set<GrantedAuthority> findGrantedAuthorityByUserId(Long userId,Set<GrantedAuthority> grantedAuths) {
		try {
			List<SysRole> list = sysRoleMapper.findByUserId(userId, true);
			for (SysRole role : list) {
				grantedAuths.add(new SimpleGrantedAuthority(role.getId().toString()));
			}
			return grantedAuths;
		} catch (Exception e) {
			throw new ServiceException("获取权限失败", e);
		}
	}

	@Override
	protected BaseMapper<SysRole, SysRoleExample> getMapper() {
		return sysRoleMapper;
	}

	@Override
	public String getModuleName() {
		return "角色管理";
	}

}
