package br.com.pockettaxi.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import static br.com.pockettaxi.utils.Constants.*;

import android.util.Log;

public class HttpClientImpl{
	private String url;
	private HttpClient httpclient = new DefaultHttpClient();

	public HttpClientImpl(String url) {
		this.url = url;
	}

	public final byte[] downloadImagem() {
		try {
			HttpGet httpget = new HttpGet(url);

			Log.i(CATEGORIA, "downloadImagem -> " + httpget.getURI());

			HttpResponse response = httpclient.execute(httpget);

			Log.i(CATEGORIA, "----------------------------------------");
			Log.i(CATEGORIA, String.valueOf(response.getStatusLine()));
			Log.i(CATEGORIA, "----------------------------------------");

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				Log.i(CATEGORIA, "Lendo jsonResponse...");
				InputStream in = entity.getContent();
				byte[] bytes = readBytes(in);
				Log.i(CATEGORIA, "Resposta: " + bytes);
				return bytes;
			}
		} catch (Exception e) {
			Log.e(CATEGORIA, e.getMessage(), e);
		}
		return null;
	}
	
	public JSONObject doGet(Map<Object,Object> parametros) throws IllegalStateException, IOException, URISyntaxException, JSONException{
		HttpGet httpget = null;
		
		if(parametros == null){
			httpget = new HttpGet(url);
		}else{
			List<NameValuePair> params = mapToNameValuePair(parametros); 
			String query = URLEncodedUtils.format(params, "UTF-8");
			URI uri = URIUtils.createURI(null, url, -1, null, query, null);
			httpget = new HttpGet(uri);
		}
		Log.i(CATEGORIA, "GET " + httpget.getURI());
		HttpResponse response = httpclient.execute(httpget);

		Log.i(CATEGORIA, "----------------------------------------");
		Log.i(CATEGORIA, String.valueOf(response.getStatusLine()));
		Log.i(CATEGORIA, "----------------------------------------");

		return processRequest(response);
	}



	public JSONObject doPost(Map<Object,Object> parametros) throws IOException, IllegalStateException, JSONException {
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> params = mapToNameValuePair(parametros);
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			Log.i(CATEGORIA, "POST" + httpPost.getURI());

			HttpResponse response = httpclient.execute(httpPost);

			Log.i(CATEGORIA, "----------------------------------------");
			Log.i(CATEGORIA, String.valueOf(response.getStatusLine()));
			Log.i(CATEGORIA, "----------------------------------------");

			return processRequest(response);
	}
	
	private JSONObject processRequest(HttpResponse response) throws IllegalStateException, IOException, JSONException {
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			InputStream in = entity.getContent();
			String resposta = readString(in);
			Log.i(CATEGORIA, "Resposta: " + resposta);
			return (JSONObject) new JSONTokener(resposta).nextValue();
		}
		
		return null;
	}
	
	private byte[] readBytes(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}

			byte[] bytes = bos.toByteArray();
			return bytes;
		} finally {
			bos.close();
		}
	}

	private String readString(InputStream in) throws IOException {
		byte[] bytes = readBytes(in);
		String text = new String(bytes);
		return text;
	}

	private List<NameValuePair> mapToNameValuePair(Map<Object,Object> parametros) throws IOException {
		if (parametros == null || parametros.size() == 0) {
			return null;
		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Iterator<Object> e = (Iterator<Object>) parametros.keySet().iterator();
		
		while (e.hasNext()) {
			String name = (String) e.next();
			Object value = parametros.get(name);
			params.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		return params;
	}
}
