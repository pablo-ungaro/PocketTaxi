package br.com.pockettaxi.server.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.model.Taxi;

public class Queue {
	public static BlockingQueue<Client> clients = new LinkedBlockingQueue<Client>();
	public static BlockingQueue<Taxi> taxis = new LinkedBlockingQueue<Taxi>();
}
