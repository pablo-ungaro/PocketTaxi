package br.com.pockettaxi.taxista.service;

import static br.com.pockettaxi.utils.Constants.CATEGORIA;
import static br.com.pockettaxi.utils.Constants.POLLING;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.http.JsonUtil;
import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.taxista.R;
import br.com.pockettaxi.taxista.ui.NewClientActivity;
import br.com.pockettaxi.utils.Util;

public class CheckerClientService extends Service {
	private Handler handler = new Handler();
	private boolean isActive = false;
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);		
	}
	
	@Override
	public void onCreate() {
		stopService(new Intent("SET_CURRENT_POSITION_SERVICE"));		
		isActive = true;
        showNotification();

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					checkIfHasClient();
				} catch (IllegalStateException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				} catch (IOException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
					Util.showMessage(CheckerClientService.this,handler,getString(R.string.connect_server),Toast.LENGTH_LONG);
					stopSelf();
				} catch (URISyntaxException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				} catch (JSONException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				}
				if(isActive)handler.postDelayed(this, POLLING);
			}

		}, POLLING);
	}

	private void checkIfHasClient() throws IllegalStateException, IOException,URISyntaxException, JSONException {
		HttpClientImpl http = new HttpClientImpl(Util.getUrlHasClient());
		JSONObject resp = http.doGet(null);

		processResponse(resp);
	}

	private void processResponse(JSONObject resp) throws JSONException {
		switch (JsonUtil.jsonToStatusCode(resp)) {
			case OK:
					Client client = JsonUtil.jsonToClient(resp);
					Log.i(CATEGORIA, client.getName() + " é o primeiro da fila.");
					Intent newClientActivty = new Intent(this,NewClientActivity.class);
					newClientActivty.putExtra("client", client);
					showNewClientNotification(client,newClientActivty);
				break;
	
			case QUEUE_EMPTY:
					Log.i(CATEGORIA, "Nenhum cliente na fila");
					NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					nm.cancel(R.string.notification_new_client_id);
				break;
		}
	}

	private void showNewClientNotification(Client client, Intent intent ) {
		int icon = R.drawable.new_client;
		long when = System.currentTimeMillis();
		PendingIntent p = PendingIntent.getActivity(this, 0, intent, 0);

		Notification notification = new Notification(icon,getString(R.string.new_client), when);
		notification.setLatestEventInfo(CheckerClientService.this, client.getName(),client.getAddress(),p);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(R.string.notification_new_client_id, notification);	
	}

	public void showNotification() {
		int icon = R.drawable.icon_has_client;
		long when = System.currentTimeMillis();
		String messageBar = getString(R.string.service_checker_client_msg);
		String title = getString(R.string.notification_title);
		String message = messageBar;
		
		PendingIntent p = PendingIntent.getActivity(this, 0, null, 0);

		Notification notification = new Notification(icon, messageBar, when);
		notification.setLatestEventInfo(CheckerClientService.this, title, message,p);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(R.string.notification_checker_id, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(R.string.notification_new_client_id);
		nm.cancel(R.string.notification_checker_id);
		isActive = false;
	}
	
}
