package br.com.pockettaxi.server.model;

import br.com.pockettaxi.model.StatusCode;

public abstract class Response {
	protected StatusCode statusCode;
	protected String message;
	
	public StatusCode getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
