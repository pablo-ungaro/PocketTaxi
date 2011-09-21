package br.com.pockettaxi.taxista.service;

import static br.com.pockettaxi.utils.Constants.CATEGORIA;
import static br.com.pockettaxi.utils.Constants.HOST;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
			this.client = JsonUtil.jsonToClient(http.getJsonResponse());
			Toast.makeText(this, client.getAddres(), Toast.LENGTH_LONG).show();

		} catch (IllegalStateException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
			Toast.makeText(this, "Erro ao tentar atualizar localização do taxista.", Toast.LENGTH_LONG);
		} catch (IOException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
		} catch (URISyntaxException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
		} catch (JSONException e) {
			Log.e(CATEGORIA, e.getMessage(),e);
		}	
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(CATEGORIA, "RecebedorDeSolicitacaoService > onDestroy()");
	}

	private void downloadImagem(final String urlImg) {
		new Thread() {
			@Override
			public void run() {

				try {
					Log.i(CATEGORIA, "RecebedorDeSolicitacaoService > Buscando imagem...");

					// Cria a URL
					URL u = new URL(URL);

					HttpURLConnection connection = (HttpURLConnection) u.openConnection();
					// Configura a requisição como "get"
					connection.setRequestProperty("Request-Method", "GET");
					connection.setDoInput(true);
					connection.setDoOutput(false);

					connection.connect();

					InputStream in = connection.getInputStream();

					Log.i(CATEGORIA, "RecebedorDeSolicitacaoService > Lendo imagem...");

					// String arquivo = readBufferedString(sb, in);
					byte[] bytesImagem = readBytes(in);

					Log.i(CATEGORIA, "RecebedorDeSolicitacaoService > Imagem lida com sucesso!");

					connection.disconnect();

					Log.i(CATEGORIA, "RecebedorDeSolicitacaoService > Criando notifica��o...");

					criarNotificacao(bytesImagem);

					Log.i(CATEGORIA, "RecebedorDeSolicitacaoService > Notifica��o criada com sucesso.");

					stopSelf();

				} catch (IOException e) {
					Log.e(CATEGORIA, e.getMessage(), e);
				}
			}
		}.start();
	}

	private byte[] readBytes(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
			}
			byte[] bytes = bos.toByteArray();
			return bytes;
		} finally {
			bos.close();
			in.close();
		}
	}

	// Exibe a notifica��o
	protected void criarNotificacao(byte[] bytesImagem) {

		String mensagemBarraStatus = "Fim do download.";
		String titulo = "Download completo.";
		String mensagem = "Visualizar imagem do download.";
		Class<?> activity = VisualizarImagem.class;

		// Service
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Notification notificacao = new Notification(R.drawable.smile, mensagemBarraStatus, System.currentTimeMillis());

		// PendingIntent para executar a Activity se o usu�rio selecionar a notifica��o
		Intent intentMensagem = new Intent(this, activity);
		intentMensagem.putExtra("imagem", bytesImagem);
		PendingIntent p = PendingIntent.getActivity(this, 0, intentMensagem, 0);

		// Informa��es
		notificacao.setLatestEventInfo(this, titulo, mensagem, p);

		// Precisa de permiss�o: <uses-permission android:name="android.permission.VIBRATE" />
		// espera 100ms e vibra por 250ms, depois espera por 100 ms e vibra por 500ms.
		notificacao.vibrate = new long[] { 100, 250, 100, 500 };

		// id (n�mero �nico) que identifica esta notifica��o. Mesmo id utilizado para cancelar
		nm.notify(R.string.app_name, notificacao);
	}
}