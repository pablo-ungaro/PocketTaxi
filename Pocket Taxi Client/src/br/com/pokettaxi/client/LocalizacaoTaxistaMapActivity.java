package br.com.pokettaxi.client;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import br.com.pockettaxi.client.model.PedidoTaxi;
import br.com.pokettaxi.client.R;
import br.com.pokettaxi.client.client.utils.Coordenada;
import br.com.pokettaxi.client.client.utils.ImagensOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class LocalizacaoTaxistaMapActivity extends MapActivity {
	private static final String CATEGORIA = "map";
	private static final int TEMPO = 2000;
	private int indice = 0;
	private Handler handler = new Handler();
	private boolean fimHandler = false;
	// Matriz de coordenadas Latitude e Longitude de um caminho para exibir no Map
//	private double[][] coordenadas = new double[][] { 
//			{ -22.906183, -43.178244 }, { -25.443156, -49.280859 },
//			{ -25.443099, -49.280698 }, { -25.443050, -49.280548 },
//			{ -25.442953, -49.280344 }, { -25.442904, -49.280129 },
//			{ -25.442827, -49.279979 }, { -25.442770, -49.279830 },
//			{ -25.442692, -49.279626 }, { -25.442595, -49.279444 },
//			{ -25.442546, -49.279272 }, { -25.442498, -49.279132 },
//			{ -25.442440, -49.278971 }, { -25.442352, -49.278768 },
//			{ -25.442285, -49.278574 }, { -25.442207, -49.278403 },
//			{ -25.442130, -49.278220 }
//	};
	private MapView mapa;
	private MapController mapController;
	private LocationManager locManager;
	private Drawable iconeTaxista;
	private Drawable iconeCliente;
	private GeoPoint localizacaoCliente;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		iconeTaxista = getResources().getDrawable(	R.drawable.icon_taxista);
		iconeCliente = getResources().getDrawable(	R.drawable.icon_cliente);
		inicializarEconfigurarMapa();
		configurarAtualizacaoDaTela();
		setContentView(mapa);
	}

	private void inicializarEconfigurarMapa() {
		mapa = new MapView(this, "0PJEwP5On_eASpW549v8Ft0VPpZCJI1dGzx89NA");
		
		mapa.setClickable(true);
		mapa.setBuiltInZoomControls(true);
		mapa.setSatellite(false);
		mapa.setStreetView(true);

		mapController = mapa.getController();
		mapController.setZoom(18);

		// Centraliza o mapa na última localização conhecida
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (loc == null) {
			loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(loc == null){
				loc = new Location(LocationManager.GPS_PROVIDER);
			}
		}
		
		GeoPoint localizacaoTaxi = getProximoPonto();		
		localizacaoCliente = new Coordenada(loc);
		
		addOverlay(iconeTaxista,localizacaoTaxi,"Umberto Gomes","Viatura: 026.\nLatitude      Longitude\n"+localizacaoTaxi);
		addOverlay(iconeCliente,localizacaoCliente,"Pablo Alfonso","Eu estou aqui.\nLatitude      Longitude\n" + localizacaoCliente);
		
		mapController.animateTo(localizacaoTaxi);
	}
	
	private void addOverlay(Drawable icone, GeoPoint localizacaoIcone,String titulo, String texto) {
		ImagensOverlay imagem = new ImagensOverlay(icone, this);
		imagem.addOverlay(new OverlayItem(localizacaoIcone, titulo, texto));
		mapa.getOverlays().add(imagem);
	}

	private void configurarAtualizacaoDaTela() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				GeoPoint localizacaoAtualTaxista = getProximoPonto();
				mapa.getOverlays().clear();

				addOverlay(iconeTaxista,localizacaoAtualTaxista,"Umberto Gomes","Viatura: 026.\nLatitude      Longitude\n"+localizacaoAtualTaxista);
				addOverlay(iconeCliente,localizacaoCliente,"Pablo Alfonso","Eu estou aqui.\nLatitude      Longitude\n" + localizacaoCliente);

				mapController.animateTo(localizacaoAtualTaxista);
				mapController.setZoom(18);
				
				// Invalida para desenhar o mapa novamente
				mapa.invalidate();

				// Envia a mensagem depois de 2 segundos para atualizar a coordenada do táxi
				if (!fimHandler) {
					handler.postDelayed(this, TEMPO);
				} else {
					Log.i(CATEGORIA, "Activity foi destruída");
				}

			}
		}, TEMPO);
	}

	// Retorna o prximo ponto para mover o mapa
	private GeoPoint getProximoPonto() {
		double latitude = (PedidoTaxi.coordenadas[indice][1]);
		double longitude = (PedidoTaxi.coordenadas[indice][0]);

		GeoPoint p = new Coordenada(latitude, longitude);
		indice++;
		if (indice == PedidoTaxi.coordenadas.length) {
			indice = 0;
		}
		return p;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		fimHandler = true;
	}

}
