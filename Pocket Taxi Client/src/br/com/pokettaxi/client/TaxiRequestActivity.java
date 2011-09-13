package br.com.pokettaxi.client;

import static br.com.pokettaxi.utils.Constants.CATEGORIA;
import static br.com.pokettaxi.utils.Constants.HOST;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.http.JsonUtil;
import br.com.pockettaxi.model.TaxiRequest;

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
				findTaxi();
			}
		});
	}

	private void findTaxi() {
		final Location posicaoAtualCliente = getMyCurrentLocation();
		final Map<Object,Object> parametros = new HashMap<Object, Object>();
		parametros.put("latitude", posicaoAtualCliente.getLatitude());
		parametros.put("longitude", posicaoAtualCliente.getLongitude());
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpClientImpl http = new HttpClientImpl(HOST+"request");
					http.doGet(parametros);
					JSONObject object = http.getJsonResponse();
		            JSONObject pedidoTaxi = object.getJSONObject("request");

					TaxiRequest response = JsonUtil.jsonToPedidoTaxi(pedidoTaxi);
					response.getClient().setLatitude(posicaoAtualCliente.getLatitude());
					response.getClient().setLongitude(posicaoAtualCliente.getLongitude());
					openMapWithTaxiLocation(response);
				} catch (Exception e) {
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