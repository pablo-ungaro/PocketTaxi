package br.com.pockettaxi.taxista.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import br.com.pockettaxi.taxista.R;

public class HomeActivity extends Activity {    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		Button btn = (Button)findViewById(R.id.btnStartService);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startService(new Intent("CHECKER_CLIENT_SERVICE"));
			}
		});
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    stopService(new Intent("CHECKER_CLIENT_SERVICE"));
	    stopService(new Intent("SET_CURRENT_POSITION_SERVICE"));
	}

}