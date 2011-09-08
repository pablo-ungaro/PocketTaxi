package br.com.pokettaxi.client;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import br.com.pockettaxi.client.model.PedidoTaxi;
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
	private MapView mapa;
	private MapController mapController;
	private Drawable iconeTaxista;
	private Drawable iconeCliente;
	private GeoPoint localizacaoCliente;
	PedidoTaxi pedido;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		init();
		inicializarEconfigurarMapa();
		configurarAtualizacaoDaTela();
		setContentView(mapa);
	}
	
	private void init() {
		Bundle bundle = getIntent().getExtras();
		pedido = (PedidoTaxi) bundle.get("resposta");
		localizacaoCliente = new Coordenada(pedido.getCliente().getLatitude(),pedido.getCliente().getLongitude());
		iconeTaxista = getResources().getDrawable(	R.drawable.icon_taxista);
		iconeCliente = getResources().getDrawable(	R.drawable.icon_cliente);
	}

	private void inicializarEconfigurarMapa() {
		mapa = new MapView(this, "0PJEwP5On_eASpW549v8Ft0VPpZCJI1dGzx89NA");
		
		mapa.setClickable(true);
		mapa.setBuiltInZoomControls(true);
		mapa.setSatellite(false);
		mapa.setStreetView(true);

		mapController = mapa.getController();
		mapController.setZoom(18);

		GeoPoint localizacaoTaxi = new Coordenada(pedido.getTaxi().getLatitude(),pedido.getTaxi().getLongitude());		

		addOverlay(iconeTaxista,localizacaoTaxi,pedido.getTaxi().getNome(),"Viatura: "+pedido.getTaxi().getViatura()+".\nLatitude      Longitude\n"+localizacaoTaxi);
		addOverlay(iconeCliente,localizacaoCliente,pedido.getCliente().getNome(),"Eu estou aqui.\nLatitude      Longitude\n" + localizacaoCliente);
		
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

				addOverlay(iconeTaxista,localizacaoAtualTaxista,pedido.getTaxi().getNome(),"Viatura: "+pedido.getTaxi().getViatura()+".\nLatitude      Longitude\n"+localizacaoAtualTaxista);
				addOverlay(iconeCliente,localizacaoCliente,pedido.getCliente().getNome(),"Eu estou aqui.\nLatitude      Longitude\n" + localizacaoCliente);

				mapController.animateTo(localizacaoAtualTaxista);
				
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

	// Retorna o próximo ponto para mover o mapa
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
