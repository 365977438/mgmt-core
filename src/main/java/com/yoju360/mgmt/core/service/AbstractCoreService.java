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
public abstract class AbstractCoreService<T extends BaseModel, E> implements CoreService<T, E> {
	public static final String DELETE_FAILURE = "删除失败";

	public static final String LIST_FAILURE = "获取列表失败";
	
	public static final String SAVE_FAILURE = "保存失败";

	@Autowired
	SqlSession sqlSession;

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#save(T)
	 */
	@Override
	public boolean save(T entity) {
		return save(entity, entity.getObjVersion()==null || 0==entity.getObjVersion());
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#save(T, boolean)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#save(T[])
	 */
	@Override
	public void save(T[] entitys) {
		for (T entity : entitys) {
			save(entity, true);
		}
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#save(java.util.Collection)
	 */
	@Override
	public void save(Collection<T> entitys) {
		for (T entity : entitys) {
			save(entity, true);
		}
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#save(java.util.Collection, boolean)
	 */
	@Override
	public void save(Collection<T> entitys, boolean operate) {
		for (T entity : entitys) {
			save(entity, operate);
		}
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#save(T[], boolean)
	 */
	@Override
	public void save(T[] entitys, boolean operate) {
		for (T entity : entitys) {
			save(entity, operate);
		}
	}

	protected void beforeSave(T t, boolean operate) {

	}

	
	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#delete(java.lang.Long)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#isModelBeingReferenced(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean isModelBeingReferenced(String targetTable, String targetColumn, Object value) {
		Object ret = sqlSession.selectOne("select 1 from " + targetTable + " where " + targetColumn + " = " + value);
		return ret != null;
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#delete(java.lang.Long[])
	 */
	@Override
	public void delete(Long ids[]) {
		try {
			for (Long id : ids) {
				delete(id);
			}
		} catch (Exception e) {
			throw new ServiceException(DELETE_FAILURE, e);
		}
	}

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#getModel(java.lang.Long)
	 */
	@Override
	public T getModel(Long id) {
		return getMapper().get(id);
	}


	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#findAll()
	 */
	@Override
	public List<T> findAll() {
		try {
			return getMapper().selectAll();
		} catch (Exception e) {
			throw new ServiceException(LIST_FAILURE, e);
		}
	}


	protected void beforeSave(T entity) {
	}


	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#isUniqueAmongOthers(java.lang.Long, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean isUniqueAmongOthers(Long selfId, String columnName, Object value) {
		return getMapper().isUniqueAmongOthers(selfId, columnName, value);
	}


	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#countAll()
	 */
	@Override
	public int countAll() {
		return getMapper().count();
	}
	
    /* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#countByExample(E)
	 */
    @Override
	public int countByExample(E example) {
    	return getMapper().countByExample(example);
    }
    
    /* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#findByExamplePage(E, int, int)
	 */
    @Override
	public List<T> findByExamplePage(E example, int start, int count) {
    	return getMapper().selectByExamplePage(example, start, count);
    }
    
    /* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#findByPage(int, int)
	 */
    @Override
	public List<T> findByPage(int start, int count) {
    	return getMapper().selectByPage(start, count);
    }
    
	protected abstract BaseMapper<T, E> getMapper();

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#getModuleName()
	 */
	@Override
	public abstract String getModuleName();

	/* (non-Javadoc)
	 * @see com.yoju360.mgmt.core.service.CoreService#findByExample(E)
	 */
	@Override
	public List<T> findByExample(E example) {
		return getMapper().selectByExample(example);
	}
}
