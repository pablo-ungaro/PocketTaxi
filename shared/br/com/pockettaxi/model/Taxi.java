package br.com.pockettaxi.model;

import java.io.Serializable;
import java.util.Date;

public class Taxi implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String nome;
	private Long viatura;
	private Double latitude;
	private Double longitude;
	private Date lastUpdate;
	
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
	
	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Taxi other = (Taxi) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
