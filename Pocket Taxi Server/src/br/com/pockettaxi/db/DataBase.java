package br.com.pockettaxi.db;

import java.util.HashMap;
import java.util.Map;

import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.model.Taxi;

public class DataBase {
	private Map<Long,Client> clients = new HashMap<Long,Client>();
	private Map<Long,Taxi> taxis = new HashMap<Long,Taxi>();

	public DataBase(){
		clients.put(1L, new Client(1L, "Pablo",null,null,null));
		clients.put(2L, new Client(2L, "Alfonso",null,null,null));
		clients.put(3L, new Client(3L, "Teixeira",null,null,null));
		clients.put(4L, new Client(4L, "Ungaro",null,null,null));

		taxis.put(1L, new Taxi(1L, "Regina", 01L, -22.89326153817288, -43.12362680382098));
		taxis.put(2L, new Taxi(2L, "Maria", 02L, null, null));
		taxis.put(3L, new Taxi(3L, "Ainá", 03L, null, null));
		taxis.put(4L, new Taxi(4L, "Sara", 04L, null, null));
	}

	public Taxi findTaxiById(Long id){
		return taxis.get(id);
	}
	
	public Client findClientById(Long id){
		return clients.get(id);
	}
}
