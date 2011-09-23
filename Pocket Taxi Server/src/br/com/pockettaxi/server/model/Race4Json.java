package br.com.pockettaxi.server.model;

import javax.xml.bind.annotation.XmlRootElement;

import br.com.pockettaxi.model.Race;

@XmlRootElement
public class Race4Json extends Response{
	private Race race;

	public Race getRace() {
		return race;
	}

	public void setRequest(Race race) {
		this.race = race;
	}
}
