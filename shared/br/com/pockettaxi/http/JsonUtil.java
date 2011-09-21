package br.com.pockettaxi.http;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.model.TaxiRequest;
import br.com.pockettaxi.model.Taxi;
import br.com.pockettaxi.utils.Position;

public class JsonUtil {

	public static TaxiRequest jsonToPedidoTaxi(JSONObject json) throws JSONException {
		Client cliente = getCliente(json);
		Taxi taxi = getTaxi(json);
		
		TaxiRequest resp = new TaxiRequest(taxi, cliente);
		return resp;
	}

	private static Taxi getTaxi(JSONObject json) throws JSONException {
        JSONObject taxiJson = json.getJSONObject("taxi");
		return new Taxi(taxiJson.getLong("id"), taxiJson.getString("nome"), taxiJson.getLong("viatura"),taxiJson.getDouble("latitude"),taxiJson.getDouble("longitude"));
	}

	private static Client getCliente(JSONObject json) throws JSONException {
        JSONObject clienteJson = json.getJSONObject("client");
       	return new Client(clienteJson.getLong("id"),clienteJson.getString("nome"));
	}

	public static GeoPoint jsonToLocation(JSONObject json) throws JSONException {
		String latitude = json.getString("latitude");
		String longitude = json.getString("longitude");
		return new Position(Double.parseDouble(latitude),Double.parseDouble(longitude));
	}

	public static Client jsonToClient(JSONObject jsonRoot) throws JSONException {
        Client c = getCliente(jsonRoot);
		c.setAddres(jsonRoot.getJSONObject("client").getString("addres"));
		return c;
	}

}
