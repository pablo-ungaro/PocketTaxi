package br.com.pockettaxi.model;

import java.io.Serializable;

public class Race implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private Taxi taxi;
	private Client client;
	
	public Race() {
		super();
	}
	
	public Race(Long id, Taxi taxi, Client client) {
		super();
		this.id = id;
		this.taxi = taxi;
		this.client = client;
	}

	public Race(Taxi taxi, Client client) {
		this.taxi = taxi;
		this.client = client;
	}

	public Taxi getTaxi() {
		return taxi;
	}
	
	public void setTaxi(Taxi taxi) {
		this.taxi = taxi;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setClient(Client cliente) {
		this.client = cliente;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	
}
