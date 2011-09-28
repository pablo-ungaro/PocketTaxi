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
import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.http.JsonUtil;
import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.taxista.R;
import br.com.pockettaxi.taxista.ui.HomeActivity;
import br.com.pockettaxi.utils.Util;

public class CheckerClientService extends Service {
	private Handler handler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					checkIfHasClient();
				} catch (IllegalStateException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				} catch (IOException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				} catch (URISyntaxException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				} catch (JSONException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				}
				handler.postDelayed(this, POLLING);
			}

		}, POLLING);
	}

	private void checkIfHasClient() throws IllegalStateException, IOException,
			URISyntaxException, JSONException {
		HttpClientImpl http = new HttpClientImpl(
				Util.getUrlSendCurrentPosionOfTaxi(1L));
		JSONObject resp = http.doGet(null);

		processResponse(resp);
	}

	private void processResponse(JSONObject resp) throws JSONException {
		switch (JsonUtil.jsonToStatusCode(resp)) {
		case OK:
			Client client = JsonUtil.jsonToClient(resp);
			showNotification();
			break;

		case QUEUE_EMPTY:
			Log.e(CATEGORIA, "Nenhum cliente na fila");
			break;
		}
	}

	public void showNotification() {
		int icon = R.drawable.smile;
		long when = System.currentTimeMillis();
		PendingIntent p = PendingIntent.getActivity(this, 0, new Intent(this,HomeActivity.class), 0);

		Notification notification = new Notification(icon, "testebar", when);
		notification.setLatestEventInfo(CheckerClientService.this, "title",	"msg",p);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(0, notification);

		Util.playNotificationSound(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
