package br.com.pokettaxi.client;

import br.com.pokettaxi.client.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
		new Thread(new Runnable() {

			@Override
			public void run() {
				abreMapaComLocalizacaoDoTaxi();
			}
			
		}).start();
	}

	private void abreMapaComLocalizacaoDoTaxi() {
		try {
			handler.post(new Runnable() {

				@Override
				public void run() {
					startActivity(new Intent(SolicitaTaxiActivity.this,
							LocalizacaoTaxistaMapActivity.class));
				}
			});
		} finally {
			loading.dismiss();
		}
	}
}