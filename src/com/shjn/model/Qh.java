package com.shjn.model;
// Generated 2017-8-23 17:10:04 by Hibernate Tools 4.3.1.Final

import java.util.Date;

/**
 * Qh generated by hbm2java
 */
public class Qh implements java.io.Serializable {

	private Integer id;
	private Long mpiId;
	private String mpiProductCode;
	private String mpiName;
	private String aoiName;
	private Date mpiCreateDate;
	private Boolean containClassification;
	private Boolean containStructured;
	private Date createTimestamp;
	private Date updateTimestamp;

	public Qh() {
	}

	public Qh(Date createTimestamp, Date updateTimestamp) {
		this.createTimestamp = createTimestamp;
		this.updateTimestamp = updateTimestamp;
	}

	public Qh(Long mpiId, String mpiProductCode, String mpiName, String aoiName, Date mpiCreateDate,
			Boolean containClassification, Boolean containStructured, Date createTimestamp, Date updateTimestamp) {
		this.mpiId = mpiId;
		this.mpiProductCode = mpiProductCode;
		this.mpiName = mpiName;
		this.aoiName = aoiName;
		this.mpiCreateDate = mpiCreateDate;
		this.containClassification = containClassification;
		this.containStructured = containStructured;
		this.createTimestamp = createTimestamp;
		this.updateTimestamp = updateTimestamp;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getMpiId() {
		return this.mpiId;
	}

	public void setMpiId(Long mpiId) {
		this.mpiId = mpiId;
	}

	public String getMpiProductCode() {
		return this.mpiProductCode;
	}

	public void setMpiProductCode(String mpiProductCode) {
		this.mpiProductCode = mpiProductCode;
	}

	public String getMpiName() {
		return this.mpiName;
	}

	public void setMpiName(String mpiName) {
		this.mpiName = mpiName;
	}

	public String getAoiName() {
		return this.aoiName;
	}

	public void setAoiName(String aoiName) {
		this.aoiName = aoiName;
	}

	public Date getMpiCreateDate() {
		return this.mpiCreateDate;
	}

	public void setMpiCreateDate(Date mpiCreateDate) {
		this.mpiCreateDate = mpiCreateDate;
	}

	public Boolean getContainClassification() {
		return this.containClassification;
	}

	public void setContainClassification(Boolean containClassification) {
		this.containClassification = containClassification;
	}

	public Boolean getContainStructured() {
		return this.containStructured;
	}

	public void setContainStructured(Boolean containStructured) {
		this.containStructured = containStructured;
	}

	public Date getCreateTimestamp() {
		return this.createTimestamp;
	}

	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Date getUpdateTimestamp() {
		return this.updateTimestamp;
	}

	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

}