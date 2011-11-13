package br.com.pockettaxi.server.model;

import javax.xml.bind.annotation.XmlRootElement;

import br.com.pockettaxi.model.Taxi;

@XmlRootElement
public class Taxi4Json extends Response{	
	private Taxi taxi;
	
	public Taxi4Json(){
		super();
	}
	
	public Taxi4Json(Taxi taxi){
		super();
		this.taxi = taxi;
	}

	public Taxi getTaxi() {
		return taxi;
	}

	public void setTaxi(Taxi taxi) {
		this.taxi = taxi;
	}
}
