package br.com.pockettaxi.server.model;

import javax.xml.bind.annotation.XmlRootElement;

import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.model.Taxi;

@XmlRootElement
public class Race4Json extends Response{
	private Long id;
	private Taxi taxi;
	private Client client;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
	
	public void setClient(Client client) {
		this.client = client;
	}
}
