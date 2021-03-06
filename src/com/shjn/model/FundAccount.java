package com.shjn.model;
// Generated 2017-8-23 17:10:04 by Hibernate Tools 4.3.1.Final

import java.util.Date;

/**
 * FundAccount generated by hbm2java
 */
public class FundAccount implements java.io.Serializable {

	private Integer id;
	private String fundAccountId;
	private String name;
	private String manager;
	private String type;
	private String registerCode;
	private Date registerDate;
	private String url;
	private Date createTimestamp;
	private Date updateTimestamp;

	public FundAccount() {
	}

	public FundAccount(Date createTimestamp, Date updateTimestamp) {
		this.createTimestamp = createTimestamp;
		this.updateTimestamp = updateTimestamp;
	}

	public FundAccount(String fundAccountId, String name, String manager, String type, String registerCode,
			Date registerDate, String url, Date createTimestamp, Date updateTimestamp) {
		this.fundAccountId = fundAccountId;
		this.name = name;
		this.manager = manager;
		this.type = type;
		this.registerCode = registerCode;
		this.registerDate = registerDate;
		this.url = url;
		this.createTimestamp = createTimestamp;
		this.updateTimestamp = updateTimestamp;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFundAccountId() {
		return this.fundAccountId;
	}

	public void setFundAccountId(String fundAccountId) {
		this.fundAccountId = fundAccountId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManager() {
		return this.manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRegisterCode() {
		return this.registerCode;
	}

	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}

	public Date getRegisterDate() {
		return this.registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
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
