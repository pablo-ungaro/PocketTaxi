package br.com.pockettaxi.model;

import java.io.Serializable;

public class TaxiRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private Taxi taxi;
	private Client client;
	
	public TaxiRequest() {
		super();
	}
		
	public TaxiRequest(Taxi taxi, Client client) {
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
}
