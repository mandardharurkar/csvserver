package com.gslab.poc.csvserver.model;

/**
 * Common status block that is used in all our WS API responses.
 * 
 *
 * 
 */

public class Status extends BeanBase {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private Integer statusCode = 200;
	
	
	private String errorKey="";

	
	private String errorMessage="SUCCESS";

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String statusMessage) {
		this.errorMessage = statusMessage;
	}

}
