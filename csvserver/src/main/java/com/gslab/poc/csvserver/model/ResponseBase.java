package com.gslab.poc.csvserver.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Base for the response object which contains the status.
 * 
 * 
 * 
 */

@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
public class ResponseBase extends BeanBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Status status = new Status();

	private BeanBase payload;
	@JsonInclude(Include.NON_EMPTY)
	private List<? extends BeanBase> payloadList;

	public ResponseBase() {
	}

	public ResponseBase(BeanBase payload) {
		this.payload = payload;
	}

	
	public ResponseBase(List<? extends BeanBase> payloadList) {
		this.payloadList = payloadList;
	}

	
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public BeanBase getPayload() {
		return payload;
	}

	public void setPayload(BeanBase payload) {
		this.payload = payload;
	}

	public List<? extends BeanBase> getPayloadList() {
		return payloadList;
	}

	public void setPayloadList(List<? extends BeanBase> payloadList) {
		this.payloadList = payloadList;
	}

	
}
