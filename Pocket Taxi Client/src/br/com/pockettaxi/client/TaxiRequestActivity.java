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

public class TaxiRequestActivity extends Activity {
	private ProgressDialog loading;
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
				TaxiRequestActivity.this.loading = ProgressDialog
						.show(TaxiRequestActivity.this,
								"Localizando táxi",
								"Por favor aguarde enquanto estamos localizando o seu táxi.",
								true, true);
				try {
					findTaxi();
				} catch (IOException e) {
					Log.e(CATEGORIA, e.getMessage(),e);
				}			
			}
		});
	}

	private void findTaxi() throws IOException {
		final Location myActualPosition = getMyCurrentLocation();
		Geocoder geocoder = new Geocoder(TaxiRequestActivity.this, Locale.getDefault());
		final Address address = geocoder.getFromLocation(myActualPosition.getLatitude(), myActualPosition.getLongitude(), 1).get(0);
		final Map<Object,Object> parameters = new HashMap<Object, Object>();
		
		parameters.put("latitude", myActualPosition.getLatitude());
		parameters.put("longitude", myActualPosition.getLongitude());
		parameters.put("address", createAddress(address));
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpClientImpl http = new HttpClientImpl(getUrlRequest(1L));
					JSONObject resp = http.doGet(parameters);

					Taxi taxi = JsonUtil.jsonToTaxi(resp);
					Client client = JsonUtil.jsonToClient(resp);
					
					openMapWithTaxiLocation(taxi,client);
					
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