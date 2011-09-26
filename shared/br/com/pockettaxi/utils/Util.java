package br.com.pockettaxi.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import static br.com.pockettaxi.utils.Constants.*;

public class Util {
	
	public static void showMessage(final Context ctx,Handler handler,final String msg,final int duration){
			handler.post(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(ctx, msg, duration).show();							
			}
		});		
	}
	
	public static String getUrlRequest(Long clientId){
		return new StringBuilder(HOST).append("/client/").append(clientId)
		.append("/request").toString();
	}
	
	public static String getUrlCurrentPosionOfTaxi(Long taxiId){
		return new StringBuilder(HOST).append("/taxi/").append(taxiId)
		.append("/location").toString();
	}
}
