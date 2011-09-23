package br.com.pockettaxi.taxista.service;

import static br.com.pockettaxi.utils.Constants.HOST;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import org.json.JSONException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import br.com.pockettaxi.http.HttpClientImpl;
import br.com.pockettaxi.http.JsonUtil;
import br.com.pockettaxi.model.Client;
import br.com.pockettaxi.taxista.R;
import br.com.pockettaxi.taxista.ui.VisualizarImagem;

/**
 * Serviço que faz o download de uma imagem e cria uma notificação
 * 
 * @author pablo.ungaro
 * 
 */
public class RecebedorDeSolicitacaoService extends Service {
	private final String CATEGORIA = "livro";
	private static final String URL = "http://winxlinux.com/wp-content/uploads/2009/06/wallpapers-22.jpg";
	private Client client;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(CATEGORIA, "RecebedorDeSolicitacaoService > onCreate()");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(CATEGORIA, "RecebedorDeSolicitacaoService > onStart()");
		getInfoClient();
	}

	private void getInfoClient() {
		try {
			HttpClientImpl http = new HttpClientImpl(HOST+"/infoClient");

			http.doGet(null);
			this.client = new Client();//JsonUtil.jsonToClient(http.getJsonResponse());
			client.setNome("Nome cliente");
			client.setAddres("rua nao sei o que nao sei o q lah");
			
			showNotification(client);
			
		} catch (IllegalStateException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
			Toast.makeText(this, "Erro ao tentar atualizar localização do taxista.", Toast.LENGTH_LONG);
		} catch (IOException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
		} catch (URISyntaxException e) {
			Log.e(CATEGORIA, e.getMessage(),e);}
//		} catch (JSONException e) {
//			Log.e(CATEGORIA, e.getMessage(),e);
//		}	
	}

	private void showNotification(Client client) {
		String messageBar = "Nova solicitação de taxi.";
		String titulo = "Client: "+ client.getNome();
		String mensagem = "Endereço: "+client.getAddres();
		Class<? extends Activity> activity = VisualizarImagem.class;

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Notification notificationBar = new Notification(R.drawable.smile, messageBar, System.currentTimeMillis());

		// PendingIntent para executar a Activity se o usuário selecionar a notificação
		Intent intentMensagem = new Intent(this, activity);
		intentMensagem.putExtra("client", client);
		PendingIntent p = PendingIntent.getActivity(this, 0, intentMensagem, 0);

		// Informações
		notificationBar.setLatestEventInfo(this, titulo, mensagem, p);

		// Precisa de permissão: <uses-permission android:name="android.permission.VIBRATE" />
		// espera 100ms e vibra por 250ms, depois espera por 100 ms e vibra por 500ms.
		notificationBar.vibrate = new long[] { 100, 250, 100, 500 };

		// id (número único) que identifica esta notificação. Mesmo id utilizado para cancelar
		nm.notify(R.string.app_name, notificationBar);		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(CATEGORIA, "RecebedorDeSolicitacaoService > onDestroy()");
	}

}