package br.com.pockettaxi.taxista.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import br.com.pockettaxi.taxista.R;

public class HomeActivity extends Activity {    	
	//Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=-22.971319,-43.031617")); 
	//startActivity(i);
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		Button btn = (Button)findViewById(R.id.btnStartService);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();         
	}

}