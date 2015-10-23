/**
 * 
 */
package com.yoju360.mgmt.core.model;

import java.util.Date;

/**
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
