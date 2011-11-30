package br.com.pockettaxi.http;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.model.StatusCode;
import br.com.pockettaxi.model.Taxi;
import br.com.pockettaxi.utils.Position;

import com.google.android.maps.GeoPoint;

public class JsonUtil {

	public static Taxi jsonToTaxi(JSONObject json) throws JSONException{
        JSONObject taxiJson = json.getJSONObject("taxi");
        Taxi taxi = new Taxi();
        taxi.setId(taxiJson.getLong("id"));
        taxi.setCar(taxiJson.getLong("car"));
        taxi.setLatitude(taxiJson.getDouble("latitude"));
        taxi.setLongitude(taxiJson.getDouble("longitude"));
        taxi.setName(taxiJson.getString("name"));
		return taxi;
	}

	public static Client jsonToClient(JSONObject json) throws JSONException{
        JSONObject clientJson = json.getJSONObject("client");
        Client client = new Client();
        client.setId(clientJson.getLong("id"));
        client.setName(clientJson.getString("name"));       
        client.setLatitude(clientJson.getDouble("latitude"));
        client.setLongitude(clientJson.getDouble("longitude"));
        client.setAddress(clientJson.getString("address"));
       	return client;
	}

	public static GeoPoint jsonToLocation(JSONObject json) throws JSONException {
		String latitude = json.getString("latitude");
		String longitude = json.getString("longitude");
		return new Position(Double.parseDouble(latitude),Double.parseDouble(longitude));
	}

	public static StatusCode jsonToStatusCode(JSONObject json) throws JSONException {
		return StatusCode.toEnum(json.getString("statusCode"));
	}
}
