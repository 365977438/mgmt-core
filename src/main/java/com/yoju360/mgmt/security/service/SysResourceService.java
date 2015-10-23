package com.yoju360.mgmt.security.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.core.security.util.SecurityUtils;
import com.yoju360.mgmt.core.service.CoreService;
import com.yoju360.mgmt.core.service.ServiceException;
import com.yoju360.mgmt.security.mapper.SysResourceMapper;
import com.yoju360.mgmt.security.model.SysResource;
import com.yoju360.mgmt.security.model.SysResourceExample;
import com.yoju360.mgmt.security.model.SysRoleResource;
import com.yoju360.mgmt.security.model.type.ResourceType;

@Service
@Transactional
public class SysResourceService extends CoreService<SysResource, SysResourceExample>{

	@Autowired
	private SysResourceMapper sysResourceMapper;
	@Autowired
	private SysRoleResourceService sysRoleResourceService;

	/**
	 * 保存权限
	 * 
	 * @param entity
	 */
	protected void beforeSave(SysResource entity, boolean operate) {
		if (!isUniqueAmongOthers(entity.getId(), "code", entity.getCode())) {
			throw new ServiceException("资源编码已经存在");
		}

		if (entity.getSystemName()==null && entity.getParentId()!=null) {
			SysResource parent = sysResourceMapper.get(entity.getParentId());
			entity.setSystemName(parent.getSystemName());
		}
	}

	/**
	 * 删除权限
	 * 
	 * @param entity
	 */
	@Override
	protected void beforeDelete(Long id) {
		try {
			/*
			// 查询出所有父类资源ID为当前要删除的资源ID的子资源
			SysResource entity = sysResourceMapper.get(id);
			List<SysResource> subList = findByParentId(entity.getId());

			// 将所有子资源的父类ID指定当前要删除的资源的父资源ID
			for (SysResource sub : subList) {
				sub.setParentId(entity.getParentId());
				save(sub);
			}
			*/
			// 获得已关联到当前要删除的资源的角色权限映射实体集合
			List<SysRoleResource> spList = sysRoleResourceService.findByResourceId(id);
			sysRoleResourceService.delete(spList);

		} catch (Exception e) {
			throw new ServiceException("删除资源失败", e);
		}
	}

	public List<SysResource> findByParentId(Long id) {
		SysResourceExample e = new SysResourceExample();
		e.createCriteria().andParentIdEqualTo(id);
		return sysResourceMapper.selectByExample(e);
	}

	/**
	 * 根据资源编码获取类型map
	 * 
	 * */
	public Map<Object, Object> getTypeMap(Long resourceId, boolean operate) {
		try {
			Map<Object, Object> result = new HashMap<Object, Object>();
			// 如果Operate：true则新增，false则修改
			if (operate) {
				// 新增操作
				// 首先判断是否传入了ID
				// 如ID未传，则直接是系统
				if (resourceId == null) {
					result.put(ResourceType.SYSTEM.value, ResourceType.SYSTEM.getDesc());
				} else {
					SysResource resource = sysResourceMapper.get(resourceId);
					// 如系统：菜单，功能
					if (resource.getType() == ResourceType.SYSTEM.value) {
						result.put(ResourceType.MENU.value, ResourceType.MENU.getDesc());
						result.put(ResourceType.FUNCTION.value, ResourceType.FUNCTION.getDesc());
					}
					// 如菜单：菜单，功能
					if (resource.getType() == ResourceType.MENU.value) {
						result.put(ResourceType.MENU.value, ResourceType.MENU.getDesc());
						result.put(ResourceType.FUNCTION.value, ResourceType.FUNCTION.getDesc());
					}
					// 如功能：功能
					if (resource.getType() == ResourceType.FUNCTION.value) {
						result.put(ResourceType.FUNCTION.value, ResourceType.FUNCTION.getDesc());
					}
				}
			} else {
				// 修改操作
				// 获得要修改的资源实体
				SysResource resource = sysResourceMapper.get(resourceId);

				// 获得被修改的资源的所有子资源
				List<SysResource> subList = findByParentId(resourceId);

				if (subList.size() > 0) {
					// 有子类
					List<Short> typeList = new ArrayList<Short>();
					for (SysResource entity : subList) {
						typeList.add(entity.getType());
					}
					int max = typeList.get(0);
					for (int i = 0; i < typeList.size(); i++) {
						if (typeList.get(i) > max) {
							max = typeList.get(i);
						}
					}
					// 如系统：系统
					if (resource.getType() == ResourceType.SYSTEM.value) {
						result.put(ResourceType.SYSTEM.value, ResourceType.SYSTEM.getDesc());
					}
					// 如菜单：菜单；再判断其子类是否全部是功能，如是则可改成功能，否则只能是菜单
					if (resource.getType() == ResourceType.MENU.value) {
						result.put(ResourceType.MENU.value, ResourceType.MENU.getDesc());
						if (max == ResourceType.FUNCTION.value) {
							result.put(ResourceType.FUNCTION.value, ResourceType.FUNCTION.getDesc());
						}
					}
					// 如功能：功能，再判断其子类是否有，如有
					if (resource.getType() == ResourceType.FUNCTION.value) {
						result.put(ResourceType.FUNCTION.value, ResourceType.FUNCTION.getDesc());
						if (max == ResourceType.FUNCTION.value) {
							result.put(ResourceType.MENU.value, ResourceType.MENU.getDesc());
						}
					}
				} else {
					// 无子类
					// 则如本身资源类型是系统：显示系统
					// 否则显示菜单和功能
					if (resource.getType() == ResourceType.SYSTEM.value) {
						result.put(ResourceType.SYSTEM.value, ResourceType.SYSTEM.getDesc());
					} else {
						result.put(ResourceType.MENU.value, ResourceType.MENU.getDesc());
						result.put(ResourceType.FUNCTION.value, ResourceType.FUNCTION.getDesc());
					}
				}
			}

			return result;
		} catch (Exception e) {
			throw new ServiceException("资源保存失败: " + e.getMessage(), e);
		}
	}

	public List<SysResource> findBySystemName() {
		try {
			SysResourceExample ex = new SysResourceExample();
			if (SecurityUtils.getCurrentSystemName()==null)
				ex.createCriteria().andSystemNameIsNull();
			else
				ex.createCriteria().andSystemNameEqualTo(SecurityUtils.getCurrentSystemName());
			return sysResourceMapper.selectByExample(ex);
		} catch (Exception e) {
			throw new ServiceException("查找系统下的资源失败: " + e.getMessage(), e);
		}
	}
	/**
	 * 根据用户角色查找相应的菜单
	 * 
	 * @param username
	 * @param systemValue
	 * @param menuValue
	 * @return
	 */
	public List<SysResource> findMenuByUsername(String username) {
		try {
			return sysResourceMapper.findMenuByUsername(username, SecurityUtils.getCurrentSystemName());
		} catch (Exception e) {
			throw new ServiceException("根据用户名获取菜单失败: " + e.getMessage(), e);
		}
	}

	public List<SysResource> findMenuByUserId(Long userId) {
		try {
			// 使用Spring Data获得数据
			//List<Map> giveMap = sysResourceRepository.findMenuByGiveUserIdAndSystem(userId, ResourceType.SYSTEM.value,
			//		ResourceType.MENU.value, APP_ID);
			List<SysResource> map = sysResourceMapper.findMenuByUserIdAndSystem(userId, SecurityUtils.getCurrentSystemName(), true);
			//map.addAll(giveMap);
			return map;
		} catch (Exception e) {
			throw new ServiceException("根据用户ID查找菜单失败: " + e.getMessage(), e);
		}
	}

	public List<SysResource> findFunctionByUserId(Long userId, String systemName) {
		try {
			//List<Map> giveMap = sysResourceMapper.findFunctionByGiveUserId(userId, ResourceType.FUNCTION.value);
			List<SysResource> map = sysResourceMapper.findFunctionByUserId(userId, systemName, ResourceType.FUNCTION.value,
					true);
			//map.addAll(giveMap);
			return map;
		} catch (Exception e) {
			throw new ServiceException("根据用户ID查找操作权限失败: " + e.getMessage(), e);
		}
	}

	public List<SysResource> findSystemByUserId(Long userId) {
		try {
			//List<Map> giveMap = sysResourceMapper.findFunctionByGiveUserId(userId, ResourceType.SYSTEM.value);
			List<SysResource> map = sysResourceMapper.findFunctionByUserId(userId, SecurityUtils.getCurrentSystemName(), ResourceType.SYSTEM.value,
					true);
			//map.addAll(giveMap);
			return map;
		} catch (Exception e) {
			throw new ServiceException("根据用户查找系统名称失败: " + e.getMessage(), e);
		}
	}

	@Override
	protected BaseMapper<SysResource, SysResourceExample> getMapper() {
		return sysResourceMapper;
	}

	@Override
	public String getModuleName() {
		return "权限管理";
	}

	/**
	 * id, code, title, type, parentId, url, childCount
	 * @param parentId
	 * @return
	 */
	public List<Map<String, Object>> getTreeDataByParentId(Long parentId) {
		if (parentId == null)
			parentId = 0L;
		return sysResourceMapper.getTreeDataByParentId(parentId, null);//manage all
	}
	
	/**
	 * id, code, title, type, parentId, url, childCount, granted
	 * @param parentId
	 * @return
	 */
	public List<Map<String, Object>> getAuthorizedTreeData(Long parentId, Long roleId) {
		if (parentId == null)
			parentId = 0L;
		return sysResourceMapper.getAuthorizedTreeData(parentId, roleId, null);
	}
	
	public List<SysResource> getUserMenuTreeData(Long userId, String systemName) {
		return sysResourceMapper.getUserMenuTreeData(userId, systemName);
	}
	
	public List<SysResource> getSystems(Long userId) {
		return sysResourceMapper.getSystems(userId);
	}
}
