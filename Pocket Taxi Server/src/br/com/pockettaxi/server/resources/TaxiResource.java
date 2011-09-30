package br.com.pockettaxi.server.resources;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import br.com.pockettaxi.db.DataBase;
import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.model.StatusCode;
import br.com.pockettaxi.model.Taxi;
import br.com.pockettaxi.server.model.Queue;
import br.com.pockettaxi.server.model.Taxi4Json;

import com.sun.jersey.spi.resource.Singleton;

@Path("/taxi")
@Singleton
public class TaxiResource {
	private DataBase db = new DataBase();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{taxiId}/accept/client/{clientId}")
	public String acceptRace(@PathParam("taxiId") Long taxiId,@PathParam("clientId") Long clientId) {
		System.out.println("accept -> Taxista aceitou a corrida");

		Taxi taxi = db.findTaxiById(taxiId);
		Client client = db.findClientById(clientId);
		
		if((!Queue.clients.isEmpty()) && 
				(Queue.clients.contains(client)) && 
					(Queue.clients.peek().getId().equals(clientId)) ){
			Queue.taxis.add(taxi);//Add um táxi na fila
			return StatusCode.OK.toString();
		}

		return StatusCode.TO_LATER.toString();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/location")
	public Taxi4Json getActualPosition(@PathParam("id") Long taxiId) {
		System.out.println("location -> Enviando localização atual do taxista para o cliente.");
		System.out.println(String.format("Latitude: %f - Longitude: %f", db.findTaxiById(taxiId).getLatitude(),db.findTaxiById(taxiId).getLongitude()));
		return new Taxi4Json(db.findTaxiById(taxiId));
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/set-location")
	public Taxi4Json setActualPosition(@PathParam("id") Long taxiId, 
											@QueryParam("latitude") Double latitude,
												@QueryParam("longitude") Double longitude) {
		
		System.out.println("set-location - > Atualizando posição do taxista " + taxiId);
		
		db.findTaxiById(taxiId).setLatitude(latitude);
		db.findTaxiById(taxiId).setLongitude(longitude);
		db.findTaxiById(taxiId).setLastUpdate(new Date());

		Taxi4Json resp = new Taxi4Json();
		resp.setMessage("Posição atualizada com sucesso.");
		resp.setStatusCode(StatusCode.OK);
		
		return resp;
	}
}
