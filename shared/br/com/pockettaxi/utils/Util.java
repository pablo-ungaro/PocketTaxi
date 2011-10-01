package br.com.pockettaxi.utils;

import static br.com.pockettaxi.utils.Constants.HOST;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

public class Util {
	
	public static void showMessage(final Context ctx,Handler handler,final String msg,final int duration){
	    handler.post(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(ctx, msg, duration).show();							
			}
		});		
	}
	
	public static void showSimpleDialog(final Context ctx,Handler handler,final int message){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
			 	new AlertDialog.Builder(ctx)
			 		.setMessage(message)
			 		.setPositiveButton("OK", null).show();
			}
		});
	}
	
	public static String getUrlAcceptClient(Long taxiId,Long clientId){
		return new StringBuilder(HOST).append("/taxi/").append(taxiId)
		.append("/accept").append("/client/").append(clientId).toString();
	}
	
	public static String getUrlRequest(Long clientId){
		return new StringBuilder(HOST).append("/client/").append(clientId)
		.append("/request").toString();
	}
	
	public static String getUrlHasClient(){
		return new StringBuilder(HOST).append("/client")
		.append("/has-client").toString();
	}
	
	public static String getUrlCurrentPosionOfTaxi(Long taxiId){
		return new StringBuilder(HOST).append("/taxi/").append(taxiId)
		.append("/location").toString();
	}
	
	public static String getUrlSendCurrentPosionOfTaxi(Long taxiId){
		return new StringBuilder(HOST).append("/taxi/").append(taxiId)
		.append("/set-location").toString();
	}
	
	   public static void playNotificationSound(Context context) {
	       Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	       if (uri != null) {
	           Ringtone rt = RingtoneManager.getRingtone(context, uri);
	           if (rt != null) {
	               rt.setStreamType(AudioManager.STREAM_NOTIFICATION);
	               rt.play();
	           }
	       }
	   }
}
