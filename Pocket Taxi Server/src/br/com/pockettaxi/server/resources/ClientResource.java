package br.com.pockettaxi.server.resources;

import static br.com.pockettaxi.utils.Constants.TIMEOUT;

import java.util.concurrent.TimeUnit;

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
import br.com.pockettaxi.server.model.Client4Json;
import br.com.pockettaxi.server.model.Queue;
import br.com.pockettaxi.server.model.Race4Json;

import com.sun.jersey.spi.resource.Singleton;

@Path("/client")
@Singleton
public class ClientResource {
	private DataBase db = new DataBase();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/request")
	public Race4Json taxiRequest(@PathParam("id") Long clientId,
										@QueryParam("latitude") Double latitude,
											@QueryParam("longitude") Double longitude,
												@QueryParam("address") String address) {
		
		System.out.println("Cliente " + clientId +" solicitou um táxi.Aguardando...");
		
		Race4Json resp = new Race4Json();		
		Client client = db.findClientById(clientId);
		
		if(client == null){
			resp.setStatusCode(StatusCode.INVALID_USER);
			resp.setMessage("Usuário inválido.");
			return resp;
		}
		
		client.setLongitude(longitude);
		client.setLatitude(latitude);
		client.setAddress(address);

		try {
			Queue.clients.put(client);//adiciona um cliente na fila
			
			//espera por 1min algum táxi da fila responder.Se responder tira ele da fila
			Taxi taxi = Queue.taxis.poll(TIMEOUT, TimeUnit.SECONDS);
			
			if (taxi != null) {//Se algum táxi respondeu
				resp.setClient(Queue.clients.take());//retira o cliente da fila
				resp.setTaxi(taxi);
				resp.setStatusCode(StatusCode.OK);
				resp.setMessage("Corrida iniciada");
			}else{
				resp.setStatusCode(StatusCode.TAXI_NOT_FOUND);
				resp.setMessage("Nenhum táxi respondeu a tempo.");
				Queue.clients.remove();
			}
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}			
		return resp;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/has-client")
	public Client4Json getClientInQueue() {
		System.out.println("Enviando informações do cliente para o taxista");
		
		Client4Json resp = new Client4Json();
		Client first = Queue.clients.peek();
		
		if(first == null){
			resp.setStatusCode(StatusCode.QUEUE_EMPTY);
			resp.setMessage("Nenhum cliente solicitando táxi no momento.");
			return resp;
		}
		
		resp.setClient(first);
		resp.setStatusCode(StatusCode.OK);
		
		return resp;
	}
}