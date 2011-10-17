package br.com.pockettaxi.client;

import static br.com.pockettaxi.utils.Constants.*;
import static br.com.pockettaxi.utils.Util.getUrlCurrentPosionOfTaxi;
import static br.com.pockettaxi.utils.Util.showMessage;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.http.JsonUtil;
import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.model.Taxi;
import br.com.pockettaxi.utils.ImagensOverlay;
import br.com.pockettaxi.utils.Position;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class TaxiLocationMapActivity extends MapActivity {
	private Handler handler = new Handler();
	private boolean fimHandler = false;
	private boolean isFocused =false;
	private MapView map;
	private MapController mapController;
	private Drawable iconTaxista;
	private Drawable iconClient;
	private GeoPoint clientLocation;
	private Taxi taxi;
	private Client client;

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
		taxi = (Taxi) bundle.get("taxi");
		client = (Client) bundle.get("client");
		clientLocation = new Position(client.getLatitude(),client.getLongitude());
		iconTaxista = getResources().getDrawable(R.drawable.icon_taxista);
		iconClient = getResources().getDrawable(R.drawable.icon_cliente);
	}

	private void setAndInitializeMap() {
		map = new MapView(this, "0PJEwP5On_eCCZ0HaNwxjXknV8fqOTraG4NDz5A");
		
		map.setClickable(true);
		map.setBuiltInZoomControls(true);
		map.setSatellite(false);
		map.setStreetView(true);

		mapController = map.getController();
		mapController.setZoom(18);

		GeoPoint taxiLocation = new Position(taxi.getLatitude(),taxi.getLongitude());		

		addOverlay(iconTaxista,taxiLocation,taxi.getName(),"Viatura: "+taxi.getCar()+".\nLatitude      Longitude\n"+taxiLocation);
		addOverlay(iconClient,clientLocation,client.getName(),"Eu estou aqui.\nLatitude      Longitude\n" + clientLocation);
		
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
				if(isFocused){
					GeoPoint localizacaoAtualTaxista = getCurrentPositionOfTaxi();
					map.getOverlays().clear();
	
					addOverlay(iconTaxista,localizacaoAtualTaxista,taxi.getName(),"Viatura: "+taxi.getCar()+".\nLatitude      Longitude\n"+localizacaoAtualTaxista);
					addOverlay(iconClient,clientLocation,taxi.getName(),"Eu estou aqui.\nLatitude      Longitude\n" + clientLocation);
	
					mapController.animateTo(localizacaoAtualTaxista);
					
					// Invalida para desenhar o map novamente
					map.invalidate();
				}
				// Envia a mensagem depois de 10S segundos para atualizar a coordenada do táxi
				if (!fimHandler) {
					handler.postDelayed(this, POLLING);
				} else {
					Log.i(CATEGORIA, "Activity foi destruída");
				}

			}
		}, POLLING);
	}

	private GeoPoint getCurrentPositionOfTaxi() {
		try {
			HttpClientImpl http = new HttpClientImpl(getUrlCurrentPosionOfTaxi(taxi.getId()));
			JSONObject resp = http.doGet(null);
			Taxi taxi = JsonUtil.jsonToTaxi(resp);
			return new Position(taxi.getLatitude(),taxi.getLongitude());
		} catch (JSONException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
		} catch (IllegalStateException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
		} catch (IOException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
			showMessage(this,handler,getString(R.string.connect_server),Toast.LENGTH_LONG);					
		} catch (URISyntaxException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
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
	
	@Override
	protected void onPause() {
		super.onPause();
		isFocused = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isFocused = true;
	}
}
