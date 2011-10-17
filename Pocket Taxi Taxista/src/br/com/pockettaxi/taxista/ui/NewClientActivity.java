package br.com.pockettaxi.taxista.ui;

import static br.com.pockettaxi.utils.Constants.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.http.JsonUtil;
import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.taxista.R;
import br.com.pockettaxi.utils.Util;

public class NewClientActivity extends Activity{
	private Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_client);
		showDialog();		
	}

	private void showDialog() {
		Bundle bundle = getIntent().getExtras();
		final Client client = (Client) bundle.get("client");
		LayoutInflater inflater = getLayoutInflater();
		View customDialogView = inflater.inflate(R.layout.custom_dialog_layout,
		                               (ViewGroup) findViewById(R.id.layout_root));

		TextView text = (TextView) customDialogView.findViewById(R.id.txtDialog);
		text.setText(String.format(getString(R.string.dialog_text), client.getName(),client.getAddress()));
		
		new AlertDialog.Builder(this)
		.setView(customDialogView)
		.setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					accept(client);
				} catch (IllegalStateException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				} catch (IOException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
					Util.showMessage(NewClientActivity.this,handler,getString(R.string.connect_server),Toast.LENGTH_LONG);					
				} catch (URISyntaxException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				} catch (JSONException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				}
			}
		})
		.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		})
		.create()
		.show();	
	}
	
	private void accept(Client client) throws IllegalStateException, IOException, URISyntaxException, JSONException{
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Location myCurrentPosition = getMyCurrentLocation();
		Map<Object,Object> parameters = new HashMap<Object, Object>();
		parameters.put("latitude", myCurrentPosition.getLatitude());
		parameters.put("longitude", myCurrentPosition.getLongitude());		
		HttpClientImpl http = new HttpClientImpl(Util.getUrlAcceptClient(settings.getLong("login", -1),client.getId()));
		
		JSONObject resp = http.doGet(parameters);

		processResponse(resp,client);
	}
	
	private void processResponse(JSONObject resp, Client client) throws JSONException {
		switch (JsonUtil.jsonToStatusCode(resp)) {
			case OK:
					stopService(new Intent("CHECKER_CLIENT_SERVICE"));				
					startService(new Intent("SET_CURRENT_POSITION_SERVICE"));
					openRouteNavigation(client.getLatitude(), client.getLongitude());
				break;
	
			case TO_LATER:
					Log.i(CATEGORIA, "Outro táxi aceitou a corrida primeiro.");
					Util.showSimpleDialog(NewClientActivity.this, handler, R.string.to_later);
				break;
		}
	}
	
	private void openRouteNavigation(Double latitude, Double longitude) {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+latitude+ ","+longitude)); 
		startActivity(i);	
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
}
