package com.shjn.model;
// Generated 2017-8-23 17:10:04 by Hibernate Tools 4.3.1.Final

import java.util.Date;

/**
 * QhDetail generated by hbm2java
 */
public class QhDetail implements java.io.Serializable {

	private Integer id;
	private Long mpiId;
	private String mpiProductCode;
	private String aoiName;
	private String trusteeName;
	private Date mpiCreateDate;
	private String investmentType;
	private Double raiseScale;
	private Boolean structured;
	private Integer principalsNumber;
	private Date createTimestamp;
	private Date updateTimestamp;

	public QhDetail() {
	}

	public QhDetail(Date createTimestamp, Date updateTimestamp) {
		this.createTimestamp = createTimestamp;
		this.updateTimestamp = updateTimestamp;
	}

	public QhDetail(Long mpiId, String mpiProductCode, String aoiName, String trusteeName, Date mpiCreateDate,
			String investmentType, Double raiseScale, Boolean structured, Integer principalsNumber,
			Date createTimestamp, Date updateTimestamp) {
		this.mpiId = mpiId;
		this.mpiProductCode = mpiProductCode;
		this.aoiName = aoiName;
		this.trusteeName = trusteeName;
		this.mpiCreateDate = mpiCreateDate;
		this.investmentType = investmentType;
		this.raiseScale = raiseScale;
		this.structured = structured;
		this.principalsNumber = principalsNumber;
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

	public String getAoiName() {
		return this.aoiName;
	}

	public void setAoiName(String aoiName) {
		this.aoiName = aoiName;
	}

	public String getTrusteeName() {
		return this.trusteeName;
	}

	public void setTrusteeName(String trusteeName) {
		this.trusteeName = trusteeName;
	}

	public Date getMpiCreateDate() {
		return this.mpiCreateDate;
	}

	public void setMpiCreateDate(Date mpiCreateDate) {
		this.mpiCreateDate = mpiCreateDate;
	}

	public String getInvestmentType() {
		return this.investmentType;
	}

	public void setInvestmentType(String investmentType) {
		this.investmentType = investmentType;
	}

	public Double getRaiseScale() {
		return this.raiseScale;
	}

	public void setRaiseScale(Double raiseScale) {
		this.raiseScale = raiseScale;
	}

	public Boolean getStructured() {
		return this.structured;
	}

	public void setStructured(Boolean structured) {
		this.structured = structured;
	}

	public Integer getPrincipalsNumber() {
		return this.principalsNumber;
	}

	public void setPrincipalsNumber(Integer principalsNumber) {
		this.principalsNumber = principalsNumber;
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
