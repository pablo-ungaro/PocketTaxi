package br.com.pokettaxi.client;

import static br.com.pokettaxi.utils.Constants.HOST;

import org.json.JSONException;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.http.JsonUtil;
import br.com.pockettaxi.model.TaxiRequest;
import br.com.pokettaxi.utils.Position;
import br.com.pokettaxi.utils.ImagensOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class TaxiLocationMapActivity extends MapActivity {
	private static final String CATEGORIA = "map";
	private static final int TEMPO = 2000;
	private Handler handler = new Handler();
	private boolean fimHandler = false;
	private MapView map;
	private MapController mapController;
	private Drawable iconTaxista;
	private Drawable iconClient;
	private GeoPoint clientLocation;
	private TaxiRequest request;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		init();
		setAndInitializeMap();
		setUpdateView();
		setContentView(map);
	}
	
	private void init() {
		Bundle bundle = getIntent().getExtras();
		request = (TaxiRequest) bundle.get("response");
		clientLocation = new Position(request.getClient().getLatitude(),request.getClient().getLongitude());
		iconTaxista = getResources().getDrawable(	R.drawable.icon_taxista);
		iconClient = getResources().getDrawable(	R.drawable.icon_cliente);
	}

	private void setAndInitializeMap() {
		map = new MapView(this, "0PJEwP5On_eASpW549v8Ft0VPpZCJI1dGzx89NA");
		
		map.setClickable(true);
		map.setBuiltInZoomControls(true);
		map.setSatellite(false);
		map.setStreetView(true);

		mapController = map.getController();
		mapController.setZoom(18);

		GeoPoint taxiLocation = new Position(request.getTaxi().getLatitude(),request.getTaxi().getLongitude());		

		addOverlay(iconTaxista,taxiLocation,request.getTaxi().getNome(),"Viatura: "+request.getTaxi().getViatura()+".\nLatitude      Longitude\n"+taxiLocation);
		addOverlay(iconClient,clientLocation,request.getClient().getNome(),"Eu estou aqui.\nLatitude      Longitude\n" + clientLocation);
		
		mapController.animateTo(taxiLocation);
	}
	
	private void addOverlay(Drawable icone, GeoPoint localizacaoIcone,String titulo, String texto) {
		ImagensOverlay imagem = new ImagensOverlay(icone, this);
		imagem.addOverlay(new OverlayItem(localizacaoIcone, titulo, texto));
		map.getOverlays().add(imagem);
	}

	private void setUpdateView() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				GeoPoint localizacaoAtualTaxista = getCurrentPositionOfTaxi();
				map.getOverlays().clear();

				addOverlay(iconTaxista,localizacaoAtualTaxista,request.getTaxi().getNome(),"Viatura: "+request.getTaxi().getViatura()+".\nLatitude      Longitude\n"+localizacaoAtualTaxista);
				addOverlay(iconClient,clientLocation,request.getClient().getNome(),"Eu estou aqui.\nLatitude      Longitude\n" + clientLocation);

				mapController.animateTo(localizacaoAtualTaxista);
				
				// Invalida para desenhar o map novamente
				map.invalidate();

				// Envia a mensagem depois de 2 segundos para atualizar a coordenada do táxi
				if (!fimHandler) {
					handler.postDelayed(this, TEMPO);
				} else {
					Log.i(CATEGORIA, "Activity foi destruída");
				}

			}
		}, TEMPO);
	}

	private GeoPoint getCurrentPositionOfTaxi() {
		HttpClientImpl http = new HttpClientImpl(HOST+request.getTaxi().getId()+"/location");
		http.doGet(null);
		try {
			return JsonUtil.jsonToLocation(http.getJsonResponse());
		} catch (JSONException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
			Toast.makeText(this, "Erro ao tentar atualizar localização do taxista.", Toast.LENGTH_LONG);
		}
		return null;
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
