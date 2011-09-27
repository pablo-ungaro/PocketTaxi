package br.com.pockettaxi.client;

import static br.com.pockettaxi.utils.Constants.CATEGORIA;
import static br.com.pockettaxi.utils.Util.getUrlRequest;
import static br.com.pockettaxi.utils.Util.showMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.http.JsonUtil;
import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.model.Taxi;
import br.com.pockettaxi.utils.Util;

public class TaxiRequestActivity extends Activity {
	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.solicita_taxi);
		bind();
	}

	private void bind() {
		final Button button = (Button) findViewById(R.id.button);
		
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {			
				findTaxi();						
			}
		});
	}

	private void findTaxi(){		
		new Thread(new Runnable() {
			ProgressDialog loading = ProgressDialog.show(TaxiRequestActivity.this,
					"Localizando táxi","Por favor aguarde enquanto estamos localizando o seu táxi.",true, true);
			@Override
			public void run() {
				try {					
					Location myActualPosition = getMyCurrentLocation();
					Geocoder geocoder = new Geocoder(TaxiRequestActivity.this, Locale.getDefault());
					Map<Object,Object> parameters = new HashMap<Object, Object>();
					Address address = geocoder.getFromLocation(myActualPosition.getLatitude(), myActualPosition.getLongitude(), 1).get(0);
	
					parameters.put("latitude", myActualPosition.getLatitude());
					parameters.put("longitude", myActualPosition.getLongitude());
					parameters.put("address", createAddress(address));
					
					HttpClientImpl http = new HttpClientImpl(getUrlRequest(1L));
					JSONObject resp = http.doGet(parameters);
					
					processResponse(resp);
		
				} catch (IllegalStateException e) {
					Log.e(CATEGORIA, e.getMessage(),e);
				} catch (IOException e) {
					Log.e(CATEGORIA, e.getMessage(),e);
					showMessage(TaxiRequestActivity.this,handler,"Não foi possível conectar com o servidor.",Toast.LENGTH_LONG);					
				} catch (URISyntaxException e) {
					Log.e(CATEGORIA, e.getMessage(),e);
				} catch (JSONException e) {
					Log.e(CATEGORIA, e.getMessage(),e);
				}finally{
					loading.dismiss();
				}
			}			
		}).start();
	}

	private void processResponse(JSONObject resp) throws JSONException {
		switch(JsonUtil.jsonToStatusCode(resp)){
		 case OK:
				Taxi taxi = JsonUtil.jsonToTaxi(resp);
				Client client = JsonUtil.jsonToClient(resp);
				
				openMapWithTaxiLocation(taxi,client);	
			 break;
		 case INVALID_USER:
			 	Util.showSimpleDialog(TaxiRequestActivity.this,handler,R.string.user_not_registered);
			 break;
		 case TAXI_NOT_FOUND:
			 	Util.showSimpleDialog(TaxiRequestActivity.this,handler,R.string.taxi_not_found);
			 break;
		}		
	}
	
	private String createAddress(Address address) {
		return address.getAddressLine(0).concat(", "+address.getAddressLine(1));
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

	private void openMapWithTaxiLocation(final Taxi taxi,final Client client) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					Intent it = new Intent(TaxiRequestActivity.this,TaxiLocationMapActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("taxi", taxi);
					bundle.putSerializable("client", client);
					it.putExtras(bundle);
					startActivity(it);
				}
			});		
	}
}