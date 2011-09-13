package br.com.pockettaxi.server.model;

import javax.xml.bind.annotation.XmlRootElement;

import br.com.pockettaxi.model.TaxiRequest;

@XmlRootElement
public class ModelAdapter {
	private TaxiRequest request;

	public TaxiRequest getRequest() {
		return request;
	}

	public void setRequest(TaxiRequest request) {
		this.request = request;
	}
}
