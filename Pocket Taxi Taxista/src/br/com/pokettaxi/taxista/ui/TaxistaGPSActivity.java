package br.com.pokettaxi.taxista.ui;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import br.com.pokettaxi.taxista.utils.Coordenada;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class TaxistaGPSActivity extends MapActivity implements LocationListener {
	private MapController mapController;
	private MapView mapa;
	private LocationManager locManager;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		configurarEinicilizarMapa();
		setContentView(mapa);
	}

	private void configurarEinicilizarMapa() {
		mapa = new MapView(this, "0PJEwP5On_eASpW549v8Ft0VPpZCJI1dGzx89NA");
		
		mapa.setClickable(true);
		mapa.setBuiltInZoomControls(true);
		mapa.setSatellite(false);
		mapa.setStreetView(true);

		mapController = mapa.getController();
		mapController.setZoom(18);

		MyLocationOverlay mlo = new MyLocationOverlay(this, mapa);
		mlo.enableMyLocation();
		mapa.getOverlays().add(mlo);

		// Centraliza o mapa na última localização conhecida
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (loc == null) {
			loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		
		Coordenada ponto = new Coordenada(loc);

		mapController.animateTo(ponto);

		// GPS listener
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);		
	}

	public void onLocationChanged(Location location) {
		GeoPoint geoPoint = new Coordenada(location);
		
		// Anima o mapa até a nova localização
		mapController.animateTo(geoPoint);

		// Invalida para desenhar o mapa novamente
		mapa.invalidate();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Remove o listener para não ficar rodando depois de sair
		locManager.removeUpdates(this);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
