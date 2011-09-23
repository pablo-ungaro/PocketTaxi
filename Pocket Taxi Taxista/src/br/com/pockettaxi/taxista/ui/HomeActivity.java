package br.com.pockettaxi.taxista.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import br.com.pockettaxi.taxista.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class HomeActivity extends Activity {    
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}
	
	@Override
	protected void onStart() {
		super.onStart();         
	
		
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=-22.971319,-43.031617")); 
		startActivity(i);
//		 new AlertDialog.Builder( this )
//         .setTitle( "Nova solicitação" )
//         .setMessage( "Novo cliente no endereço: bla bla bla. Aceitar corrida?" )
//         .setPositiveButton( "Sim", new DialogInterface.OnClickListener() {
//             public void onClick(DialogInterface dialog, int which) {
//                 Log.d("AlertDialog", "Positive");
//                 startActivity(new Intent(HomeActivity.this,TaxistaGPSActivity.class));
//             }
//         })
//         .setNegativeButton( "Não", new DialogInterface.OnClickListener() {
//             public void onClick(DialogInterface dialog, int which) {
//                 Log.d("AlertDialog","Negative");
//                 finish();
//             }
//         })
//         .show();
	}

private InputStream getConnection(String url) {
    InputStream is = null;
    try {
            URLConnection conn = new URL(url).openConnection();
            is = conn.getInputStream();
    } catch (MalformedURLException e) {
            e.printStackTrace();
    } catch (IOException e) {
            e.printStackTrace();
    }
    return is;
}

}