package br.com.pockettaxi.client;

import static br.com.pockettaxi.utils.Constants.CATEGORIA;
import static br.com.pockettaxi.utils.Constants.PREFS_NAME;
import static br.com.pockettaxi.utils.Util.getUrlRequest;
import greendroid.app.GDActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.http.JsonUtil;
import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.model.Taxi;
import br.com.pockettaxi.utils.Util;

public class TaxiRequestActivity extends GDActivity {
	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 addActionBarItem(getActionBar()
	                .newActionBarItem(NormalActionBarItem.class)
	                .setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_bar_info)), R.id.action_bar_view_info);

		addContentView(LayoutInflater.from(this).inflate(R.layout.dashboard, new FrameLayout(this)), new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		 //bind();
	}

	private void bind() {
		final Button button = (Button) findViewById(R.id.button);
		
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText input = (EditText)findViewById(R.id.login);
				String login = input.getText().toString();
				if(loginIsValid(login)){
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				    SharedPreferences.Editor editor = settings.edit();
				    editor.putLong("login", Long.parseLong(login));
				    editor.commit();
					findTaxi(Long.parseLong(input.getText().toString()));	
				}else{
				 	Util.showSimpleDialog(TaxiRequestActivity.this,handler,R.string.login_invalid);
				}
			}
		});
	}
	

	private boolean loginIsValid(String login) {
		try{
			Long.parseLong(login);
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}

	private void findTaxi(final Long cliendId){		
		new Thread(new Runnable() {
			ProgressDialog loading = ProgressDialog.show(TaxiRequestActivity.this,
					null,getString(R.string.message_pdialog_locate_taxi),true, false);
			@Override
			public void run() {
				try {					
					Location myCurrentPosition = getMyCurrentLocation();
					Geocoder geocoder = new Geocoder(TaxiRequestActivity.this, Locale.getDefault());
					Map<Object,Object> parameters = new HashMap<Object, Object>();
					Address address = geocoder.getFromLocation(myCurrentPosition.getLatitude(), myCurrentPosition.getLongitude(), 1).get(0);
	
					parameters.put("latitude", myCurrentPosition.getLatitude());
					parameters.put("longitude", myCurrentPosition.getLongitude());
					parameters.put("address", createAddress(address));
					
					HttpClientImpl http = new HttpClientImpl(getUrlRequest(cliendId));
					JSONObject resp = http.doGet(parameters);
					
					processResponse(resp);
		
				} catch (IllegalStateException e) {
					Log.e(CATEGORIA, e.getMessage(),e);
				} catch (IOException e) {
					Log.e(CATEGORIA, e.getMessage(),e);
					Util.showMessage(TaxiRequestActivity.this,handler,getString(R.string.connect_server),Toast.LENGTH_LONG);					
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

	private void processResponse(JSONObject resp) throws JSONException {
		switch(JsonUtil.jsonToStatusCode(resp)){
		 case OK:
				Taxi taxi = JsonUtil.jsonToTaxi(resp);
				Client client = JsonUtil.jsonToClient(resp);
				
				openMapWithTaxiLocation(taxi,client);	
			 break;
		 case INVALID_USER:
			 	Util.showSimpleDialog(TaxiRequestActivity.this,handler,R.string.user_not_registered);
			 break;
		 case TAXI_NOT_FOUND:
			 	Util.showSimpleDialog(TaxiRequestActivity.this,handler,R.string.taxi_not_found);
			 break;
		}		
	}
	
	private String createAddress(Address address) {
		return address.getAddressLine(0).concat(", "+address.getAddressLine(1));
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

	private void openMapWithTaxiLocation(final Taxi taxi,final Client client) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					Intent it = new Intent(TaxiRequestActivity.this,TaxiLocationMapActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("taxi", taxi);
					bundle.putSerializable("client", client);
					it.putExtras(bundle);
					startActivity(it);
				}
			});		
	}

	    @Override
	    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
	        switch (item.getItemId()) {
	            case R.id.action_bar_view_info:
	                startActivity(new Intent(this, InfoTabActivity.class));
	                return true;

	            default:
	                return super.onHandleActionBarItemClick(item, position);
	        }
	    }
	    
	    public void onActionOneClick(View v) {
	    	LayoutInflater inflater = getLayoutInflater();
			final View customDialogView = inflater.inflate(R.layout.login_custom_dialog,
			                               (ViewGroup) findViewById(R.id.lg_custom));
			
			new AlertDialog.Builder(this)
			.setView(customDialogView)
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					EditText input = (EditText) customDialogView.findViewById(R.id.login);
					login(input.getText().toString());
				}
			})
			.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			})
			.setTitle("Login")
			.create()
			.show();	
	    }
	    

		private void login(String login) {			
			if(loginIsValid(login)){
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putLong("login", Long.parseLong(login));
			    editor.commit();
				findTaxi(Long.parseLong(login));	
			}else{
			 	Util.showSimpleDialog(TaxiRequestActivity.this,handler,R.string.login_invalid);
			}			
		}
}