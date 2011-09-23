package br.com.pockettaxi.taxista.ui;

import br.com.pockettaxi.taxista.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Tela que apenas exibe uma imagem
 * 
 * @author ricardo
 * 
 */
public class VisualizarImagem extends Activity {
	private static final String CATEGORIA = "livro";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(new TextView(this));

		new AlertDialog.Builder(this)
		.setPositiveButton("Sim", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		})
		.setNegativeButton("Cancelar", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		})
		.setTitle("Solicitação de táxi")
		.setCancelable(true)
		.setMessage("Deseja aceitar a corrida?")
		.create().show();
		
		//Log.i(CATEGORIA, "VisualizarImagem > Cancelando a notifica��o...");
		//NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		//nm.cancel(R.string.app_name);
	}
}