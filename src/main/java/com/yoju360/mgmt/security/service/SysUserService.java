package com.yoju360.mgmt.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.core.service.CoreService;
import com.yoju360.mgmt.core.service.ServiceException;
import com.yoju360.mgmt.core.util.HashUtils;
import com.yoju360.mgmt.security.mapper.SysUserMapper;
import com.yoju360.mgmt.security.model.SysUser;
import com.yoju360.mgmt.security.model.SysUserExample;
import com.yoju360.mgmt.security.model.SysUserRole;

@Service
@Transactional
public class SysUserService extends CoreService<SysUser, SysUserExample> {

	@Autowired
	private SysUserMapper sysUserMapper;

	@Autowired
	private SysUserRoleService sysUserRoleService;

	/**
	 * 保存用户修改密码
	 * 
	 * @param user
	 */
	public void modifyPwd(Long userId, String oldPwd, String pwd) {
		try {
			SysUser entity=sysUserMapper.get(userId);
			entity = findByIdAndAuthenticator(entity.getUsername(),
					HashUtils.getMD5(entity.getUsername() + oldPwd)
					.toUpperCase());
			if (entity != null) {
				String newPwd = entity.getUsername() + pwd;
				entity.setAuthenticator(HashUtils.getMD5(newPwd).toUpperCase());
			} else {
				throw new ServiceException("原密码不正确");
			}
			sysUserMapper.update(entity);
		} catch (Exception e) {
			throw new ServiceException("修改密码失败: " + e.getMessage(), e);
		}
	}

	private SysUser findByIdAndAuthenticator(String username, String upperCase) {
		SysUserExample e = new SysUserExample();
		e.createCriteria().andUsernameEqualTo(username).andAuthenticatorEqualTo(upperCase);
		List<SysUser> lst = sysUserMapper.selectByExample(e);
		return lst!=null?lst.get(0):null;
	}

	protected void beforeSave(SysUser entity, boolean operate) {
		if (!isUniqueAmongOthers(entity.getId(),"username", entity.getUsername())) {
			throw new ServiceException("用户名已经存在");
		}
//		if (operate) {
//			String newPwd = entity.getUsername() + entity.getAuthenticator();
//			entity.setAuthenticator(HashUtils.getMD5(newPwd).toUpperCase());
//		}
	}

	protected void beforeDelete(SysUser entity) {
		if ("admin".equals(entity.getUsername())) {
			throw new ServiceException("不能删除admin用户");
			
		}
		List<SysUserRole> list = sysUserRoleService.findByUserId(entity.getId());
		sysUserRoleService.delete(list);
	}

	/**
	 * 修改密码
	 * 
	 * @param entity
	 */
	public void updatePwd(Long userId, String pwd) {
		try {
			SysUser user = sysUserMapper.get(userId);
			String newPwd = user.getUsername() + pwd;
			user.setAuthenticator(HashUtils.getMD5(newPwd).toUpperCase());
			sysUserMapper.update(user);
		} catch (Exception e) {
			throw new ServiceException("修改密码失败: " + e.getMessage(), e);
		}
	}

	/**
	 * 根据用户名查找用户
	 * 
	 * @param name
	 *            用户名
	 * @return SysUser
	 */
	public SysUser findByUsername(String username) {
		try {
			SysUserExample e = new SysUserExample();
			e.createCriteria().andUsernameEqualTo(username);
			List<SysUser> lst = sysUserMapper.selectByExample(e);
			return lst.size()>0?lst.get(0):null;
		} catch (Exception e) {
			throw new ServiceException("获取用户失败: " + e.getMessage(), e);
		}
	}
	
	/**
	 * 根据用户获取其他所有用户列表
	 * 获得小于当前用户的权限的用户信息 TODO ??
	 * 
	 * @return
	 */
	
	public List<SysUser> findByUserId(Long userId){
		try {
			return sysUserMapper.findManagedUserByUserId(userId);
		} catch (Exception e) {
			throw new ServiceException("获取用户失败: " + e.getMessage(), e);
		}
	}

	@Override
	protected BaseMapper<SysUser, SysUserExample> getMapper() {
		return sysUserMapper;
	}

	@Override
	public String getModuleName() {
		return "用户管理";
	}

	public List<SysUser> findByDepartmentId(Long id) {
		try {
			SysUserExample e = new SysUserExample();
			e.createCriteria().andDepartmentIdEqualTo(id);
			List<SysUser> lst = sysUserMapper.selectByExample(e);
			return lst;
		} catch (Exception e) {
			throw new ServiceException("获取用户失败: " + e.getMessage(), e);
		}
	}
}
