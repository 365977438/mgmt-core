/**
 * 
 */
package com.yoju360.mgmt.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.yoju360.mgmt.core.mapper.BaseMapper;
import com.yoju360.mgmt.core.model.BaseModel;
import com.yoju360.mgmt.core.security.util.SecurityUtils;

/**
 * Base Serivce class for save/update object.<br/>
 * 
 * <b>Override {@link #getOptimisticCheckExample(BaseModel)} for optimistic for lock checking, must set id and obj version as criteria!</b>
 * 
 * @param T Model class
 * @param E ModelExample class
 * 
 * @author evan
 *
 */
@Transactional
public abstract class CoreService<T extends BaseModel, E> {
	public static final String DELETE_FAILURE = "删除失败";

	public static final String LIST_FAILURE = "获取列表失败";
	
	public static final String SAVE_FAILURE = "保存失败";

	@Autowired
	SqlSession sqlSession;

	/**
	 * 保存
	 * @param entity obj_version=0新增，!=0修改
	 */
	public boolean save(T entity) {
		return save(entity, entity.getObjVersion()==null || 0==entity.getObjVersion());
	}

	/**
	 * 保存
	 * @param operate true=新增, false=修改
	 * */
	public boolean save(T entity, boolean operate) {
		try {
			if (operate) {
				beforeSave(entity, operate);
				entity.setIsDeleted(false);
				entity.setCreateTime(new Date());
				entity.setCreatedBy(SecurityUtils.getUsername());
				entity.setObjVersion(1);
				return getMapper().add(entity)==1;
			} else {
				beforeSave(entity);
				
				E example = getOptimisticCheckExample(entity);
				
				if (entity.getIsDeleted()==null)
					entity.setIsDeleted(false);
				entity.setUpdateTime(new Date());
				entity.setUpdatedBy(SecurityUtils.getUsername());
				if (entity.getObjVersion()==null)
					entity.setObjVersion(2);
				else
					entity.setObjVersion(entity.getObjVersion()+1);
				
				if (example != null)
					return getMapper().updateByExample(entity, example)==1;
				else
					return getMapper().update(entity) == 1;
			}
		} catch (Exception e) {
			throw new ServiceException(SAVE_FAILURE + ": " + e.getMessage(), e);
		}
	}
	
	/**
	 * NOTE: return the example object for optimistic for lock checking, must set id and obj version as criteria!
	 * 
	 * @param entity
	 * @return
	 */
	protected E getOptimisticCheckExample(T entity) {
		return null;
	}

	/**
	 * 批量新增实体
	 * 
	 * */
	public void save(T[] entitys) {
		for (T entity : entitys) {
			save(entity, true);
		}
	}

	/**
	 * 批量新增实体
	 * 
	 * */
	public void save(Collection<T> entitys) {
		for (T entity : entitys) {
			save(entity, true);
		}
	}

	/**
	 * 批量保存实体
	 * 
	 * */
	public void save(Collection<T> entitys, boolean operate) {
		for (T entity : entitys) {
			save(entity, operate);
		}
	}

	/**
	 * 批量保存实体
	 * 
	 * */
	public void save(T[] entitys, boolean operate) {
		for (T entity : entitys) {
			save(entity, operate);
		}
	}

	protected void beforeSave(T t, boolean operate) {

	}

	
	/**
	 * 删除
	 * 
	 * @param id
	 */
	public void delete(Long id) {
		try {
			beforeDelete(id);
			getMapper().delete(id);
		} catch (Exception e) {
			throw new ServiceException(DELETE_FAILURE + ": " + e.getMessage(), e);
		}
	}

	protected void beforeDelete(Long id) {

	}

	/**
	 * 判断实体是否被其它表使用
	 * 
	 * */
	public boolean isModelBeingReferenced(String targetTable, String targetColumn, Object value) {
		Object ret = sqlSession.selectOne("select 1 from " + targetTable + " where " + targetColumn + " = " + value);
		return ret != null;
	}

	/**
	 * 删除
	 * 
	 * @param ids
	 */
	public void delete(Long ids[]) {
		try {
			for (Long id : ids) {
				delete(id);
			}
		} catch (Exception e) {
			throw new ServiceException(DELETE_FAILURE, e);
		}
	}

	/**
	 * 查询单个
	 * 
	 * @param id
	 * @return
	 */
	public T getModel(Long id) {
		return getMapper().get(id);
	}


	/**
	 * 获取所有实体
	 * 
	 */
	public List<T> findAll() {
		try {
			return getMapper().selectAll();
		} catch (Exception e) {
			throw new ServiceException(LIST_FAILURE, e);
		}
	}


	protected void beforeSave(T entity) {
	}


	public boolean isUniqueAmongOthers(Long selfId, String columnName, Object value) {
		return getMapper().isUniqueAmongOthers(selfId, columnName, value);
	}


	public int countAll() {
		return getMapper().count();
	}
	
    public int countByExample(E example) {
    	return getMapper().countByExample(example);
    }
    
    public List<T> findByExamplePage(E example, int start, int count) {
    	return getMapper().selectByExamplePage(example, start, count);
    }
    
    public List<T> findByPage(int start, int count) {
    	return getMapper().selectByPage(start, count);
    }
    
	protected abstract BaseMapper<T, E> getMapper();

	public abstract String getModuleName();

	public List<T> findByExample(E example) {
		return getMapper().selectByExample(example);
	}
}
