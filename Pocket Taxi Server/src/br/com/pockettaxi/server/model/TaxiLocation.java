package br.com.pockettaxi.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaxiLocation {	
	private Double latitude;
	private Double longitude;	
	
	public TaxiLocation(){
		super();
	}
	
	public TaxiLocation(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
