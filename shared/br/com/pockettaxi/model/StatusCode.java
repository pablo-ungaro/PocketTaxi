package br.com.pockettaxi.model;

public enum StatusCode {
	OK,
	INVALID_USER,
	TO_LATER,
	TAXI_NOT_FOUND,
	QUEUE_EMPTY;
	
	public static StatusCode toEnum(String value){
		for (StatusCode statusCode : StatusCode.values()) {
			if(statusCode.toString().equals(value)){
				return statusCode;
			}
		}
		return null;
	}
}
