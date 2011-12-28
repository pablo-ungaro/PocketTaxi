package br.com.pockettaxi.taxista.ui;

import static br.com.pockettaxi.utils.Constants.PREFS_NAME;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import br.com.pockettaxi.taxista.R;
import br.com.pockettaxi.utils.Util;

public class HomeActivity extends Activity {
	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home);
	}

	public void showLogin(final View v) {
		LayoutInflater inflater = getLayoutInflater();
		final View customDialogView = inflater.inflate(
				R.layout.login_custom_dialog,
				(ViewGroup) findViewById(R.id.lg_custom));

		new AlertDialog.Builder(this)
				.setView(customDialogView)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						EditText input = (EditText) customDialogView
								.findViewById(R.id.login);
						login(input.getText().toString());
					}

				})
				.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
							}
						}).setTitle("Login").create().show();
	}

	private void login(String login) {
		if (Util.loginIsValid(login)) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putLong("login", Long.parseLong(login));
			editor.commit();
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (!locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				new AlertDialog.Builder(this)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										arg0.dismiss();
									}
								}).setMessage("GPS is disabled").create()
						.show();
				return;
			} else {
				findViewById(R.id.viewParent).setVisibility(
						View.INVISIBLE);
				findViewById(R.id.txt_info_service).setVisibility(
						View.VISIBLE);
				startService(new Intent("CHECKER_CLIENT_SERVICE"));
			}
		} else {
			Util.showSimpleDialog(HomeActivity.this, handler,
					R.string.login_invalid);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(new Intent("CHECKER_CLIENT_SERVICE"));
		stopService(new Intent("SET_CURRENT_POSITION_SERVICE"));
	}

}