package br.com.pockettaxi.utils;

import android.content.Context;
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
	
}
