package br.com.pockettaxi.server.model;

import javax.xml.bind.annotation.XmlRootElement;

import br.com.pockettaxi.model.Race;

@XmlRootElement
public class ModelAdapter {
	private Race request;

	public Race getRequest() {
		return request;
	}

	public void setRequest(Race request) {
		this.request = request;
	}
}
