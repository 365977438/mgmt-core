package com.yoju360.mgmt.security.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.yoju360.mgmt.core.model.BaseModel;

public class SysRole extends BaseModel {
    private Long id;

    private String code;

    private String title;

    private String description;

    private Boolean status;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createdBy;
    @JsonIgnore
    private Boolean isDeleted;
    @JsonIgnore
    private Integer objVersion;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

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