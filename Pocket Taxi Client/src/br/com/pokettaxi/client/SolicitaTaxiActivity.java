package br.com.pokettaxi.client;

import static br.com.pokettaxi.client.client.utils.Constants.CATEGORIA;
import static br.com.pokettaxi.client.client.utils.Constants.HOST;

import java.util.HashMap;
import java.util.Map;

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
import br.com.pockettaxi.client.model.PedidoTaxi;
import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.http.JsonUtil;

public class SolicitaTaxiActivity extends Activity {
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
				SolicitaTaxiActivity.this.loading = ProgressDialog
						.show(SolicitaTaxiActivity.this,
								"Localizando táxi",
								"Por favor aguarde enquanto estamos localizando o seu táxi.",
								true, true);
				localizarTaxi();
			}
		});
	}

	private void localizarTaxi() {
		final Location posicaoAtualCliente = pegarPosicaoAtualCliente();
		final Map<Object,Object> parametros = new HashMap<Object, Object>();
		parametros.put("latitude", posicaoAtualCliente.getLatitude());
		parametros.put("longitude", posicaoAtualCliente.getLongitude());
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpClientImpl http = new HttpClientImpl(HOST);
					http.doGet(parametros);
					PedidoTaxi resposta = JsonUtil.jsonToPedidoTaxi(http.json());
					resposta.getCliente().setLatitude(posicaoAtualCliente.getLatitude());
					resposta.getCliente().setLongitude(posicaoAtualCliente.getLongitude());
					abreMapaComLocalizacaoDoTaxi(resposta);
				} catch (Exception e) {
					Log.e(CATEGORIA, e.getMessage(),e);
				}
			}
			
		}).start();
	}

	private Location pegarPosicaoAtualCliente() {
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location posicaoAtual = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (posicaoAtual == null) {
			posicaoAtual = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(posicaoAtual == null){
				posicaoAtual = new Location(LocationManager.GPS_PROVIDER);
			}
		}
	
		return posicaoAtual;
	}

	private void abreMapaComLocalizacaoDoTaxi(final PedidoTaxi resposta) {
		try {
			handler.post(new Runnable() {

				@Override
				public void run() {
					Intent it = new Intent(SolicitaTaxiActivity.this,LocalizacaoTaxistaMapActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("resposta", resposta);
					it.putExtras(bundle);
					startActivity(it);
				}
			});
		} finally {
			loading.dismiss();
		}
	}
}