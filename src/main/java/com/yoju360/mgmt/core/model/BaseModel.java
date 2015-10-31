/**
 * 
 */
package com.yoju360.mgmt.core.model;

import java.util.Date;

/**
 * 一般实体的基类。除特殊情况外，实体必须继承该类。
 * <p>
 * BaseModel提供所有实体(表)必须包含的6个字段。这些字段具有记录创建、修改时间/人员，实现软删除，记录修改版本控制等功能。
 * 这些字段的作用非常重要。
 * </p>
 * <p>
 * 基本属性(字段)及说明：
 * <ul>
 *   <li>createTime(create_time): 记录创建时间，不可改变</li>
 *   <li>createdBy(created_by): 创建记录的系统用户，不可改变</li>
 *   <li>updateTime(update_time): 记录更新时间，每次修改时由数据库自动更新为CURRENT_TIMESTAMP</li>
 *   <li>updatedBy(updated_by): 修改记录的系统用户</li>
 *   <li>isDeleted(is_deleted): 记录是否已删除，为了数据安全与审查需要，所有的记录均非物理删除。表查询时统一加上is_deleted = false的条件。已被删除的记录可定时转移到历史数据库。</li>
 *   <li>objVersion(obj_version): 记录的版本号，为了控制记录的并发修改问题。每次修改记录版本号加一。如果保存时版本号与拿出记录时不同则记录已经被其他用户修改过，拒绝修改。详细可参考Hibernate的乐观锁与版本控制机制。</li>
 * </ul>
 * </p>
 * @author evan
 *
 */
public abstract class BaseModel {
	private Date createTime;

    private String createdBy;

    private Boolean isDeleted;

    private Integer objVersion;

    private Date updateTime;

    private String updatedBy;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getObjVersion() {
		return objVersion;
	}

	public void setObjVersion(Integer objVersion) {
		this.objVersion = objVersion;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
}
