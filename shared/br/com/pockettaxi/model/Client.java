package br.com.pockettaxi.model;

import java.io.Serializable;

public class Client implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String nome;
	private Double latitude;
	private Double longitude;
	private String address;
	
	public Client() {
		super();
	}
	
	public Client(Long id, String nome) {
		super();
		this.id = id;
		this.nome = nome;
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
	
	@Override
	public String toString() {
		return nome;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
