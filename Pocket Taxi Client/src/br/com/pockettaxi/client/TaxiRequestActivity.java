package br.com.pockettaxi.client;

import static br.com.pockettaxi.utils.Constants.CATEGORIA;
import static br.com.pockettaxi.utils.Constants.HOST;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
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
import br.com.pockettaxi.model.TaxiRequest;
import br.com.pockettaxi.utils.Util;
import br.com.pockettaxi.client.R;

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
		final Location posicaoAtualCliente = getMyCurrentLocation();
		Geocoder geocoder = new Geocoder(TaxiRequestActivity.this, Locale.getDefault());
		final Address address = geocoder.getFromLocation(posicaoAtualCliente.getLatitude(), posicaoAtualCliente.getLongitude(), 1).get(0);
		final Map<Object,Object> parametros = new HashMap<Object, Object>();
		parametros.put("latitude", posicaoAtualCliente.getLatitude());
		parametros.put("longitude", posicaoAtualCliente.getLongitude());
		parametros.put("adress", address.getAddressLine(0).concat(", "+address.getAddressLine(1)));
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpClientImpl http = new HttpClientImpl(HOST+"/request");
					http.doGet(parametros);
					JSONObject object = http.getJsonResponse();
		            JSONObject pedidoTaxi = object.getJSONObject("request");

					TaxiRequest response = JsonUtil.jsonToPedidoTaxi(pedidoTaxi);
					response.getClient().setLatitude(posicaoAtualCliente.getLatitude());
					response.getClient().setLongitude(posicaoAtualCliente.getLongitude());										
					response.getClient().setAddres(address.getAddressLine(0).concat(", "+address.getAddressLine(1)));
					
					openMapWithTaxiLocation(response);
					
				} catch (IllegalStateException e) {
					Log.e(CATEGORIA, e.getMessage(),e);
				} catch (IOException e) {
					Log.e(CATEGORIA, e.getMessage(),e);
					Util.showMessage(TaxiRequestActivity.this,handler,"Não foi possível conectar com o servidor.",Toast.LENGTH_LONG);					
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

	private void openMapWithTaxiLocation(final TaxiRequest response) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					Intent it = new Intent(TaxiRequestActivity.this,TaxiLocationMapActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("response", response);
					it.putExtras(bundle);
					startActivity(it);
				}
			});		
	}
}