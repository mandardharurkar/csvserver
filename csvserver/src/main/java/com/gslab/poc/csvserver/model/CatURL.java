package com.gslab.poc.csvserver.model;

public class CatURL extends BeanBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String clientId;

	private String categoryId;

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = (categoryId==null)?null:categoryId.trim();
	}

	private String url;
	private String status = "CACHE-READ";

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public CatURL() {
	}

	public CatURL(String catid, String url) {
		super();
		this.categoryId = (catid==null)?null:catid.trim();
		this.url = url;
	}

	public CatURL(String catid, String url,String status , String appName) {
		super();
		this.categoryId = (catid==null)?null:catid.trim();
		this.url = url;
		this.status=status;
		this.clientId=appName;
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String toString() {
		return "CatURL [categoryId=" + categoryId + ", url=" + url + "]";
	}

}
