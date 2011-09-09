package br.com.pockettaxi.model;

import java.io.Serializable;

public class Taxi implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String nome;
	private Long viatura;
	private Double latitude;
	private Double longitude;
	
	public Taxi() {
		super();
	}
	
		
	public Taxi(Long id, String nome, Long viatura,Double latitude, Double longitude) {
		this.id = id;
		this.nome = nome;
		this.viatura = viatura;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Long getViatura() {
		return viatura;
	}
	
	public void setViatura(Long viatura) {
		this.viatura = viatura;
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
