package br.com.pockettaxi.taxista.service;

import static br.com.pockettaxi.utils.Constants.CATEGORIA;
import static br.com.pockettaxi.utils.Constants.POLLING;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.utils.Util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SetCurrentPositionService extends Service {
	private Handler handler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					setMyPosition();
				} catch (IllegalStateException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				} catch (IOException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				} catch (URISyntaxException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				} catch (JSONException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				}
				handler.postDelayed(this, POLLING);
			}

		}, POLLING);
	}

	private void setMyPosition() throws IllegalStateException, IOException, URISyntaxException, JSONException {
		Location myCurrentPosition = getMyCurrentLocation();
		Map<Object,Object> parameters = new HashMap<Object, Object>();
		parameters.put("latitude", myCurrentPosition.getLatitude());
		parameters.put("longitude", myCurrentPosition.getLongitude());
		
		HttpClientImpl http = new HttpClientImpl(Util.getUrlSendCurrentPosionOfTaxi(1L));
		JSONObject resp = http.doGet(parameters);		
	}
	
	private Location getMyCurrentLocation() {
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location currentLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (currentLocation == null) {
			currentLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(currentLocation == null){
				currentLocation = new Location(LocationManager.GPS_PROVIDER);
			}
		}
	
		return currentLocation;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
