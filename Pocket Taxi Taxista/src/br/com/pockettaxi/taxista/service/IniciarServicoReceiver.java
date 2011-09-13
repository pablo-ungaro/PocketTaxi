package br.com.pockettaxi.taxista.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * BroadcastReceiver que executa quando o SO termina de iniciar
 * 
 * Para isto um IntentFilter foi configurado para a ação:
 * 
 * "android.intent.action.BOOT_COMPLETED"
 * 
 * @author pablo.ungaro
 * 
 */
public class IniciarServicoReceiver extends BroadcastReceiver {
	private static final String CATEGORIA = "taxi";

	@Override
	public void onReceive(Context contexto, Intent intent) {
		Log.i(CATEGORIA, "IniciarServicoReceiver > O sistema operacional foi iniciado com sucesso.");
		Log.i(CATEGORIA, "IniciarServicoReceiver > Iniciando o servico de download...");

		// Agendar o serviço para daqui a 15 segundos
		Log.i(CATEGORIA, "IniciarServicoReceiver > Agendando o download da imagem...");
		agendar(contexto, 15);

		Log.i(CATEGORIA, "IniciarServicoReceiver > Fim.");
	}

	// Agenda o alarme/serviço para executar daqui a X segundos
	private void agendar(Context contexto, int segundos) {
		Intent it = new Intent("DOWNLOAD_IMAGEM");
		PendingIntent p = PendingIntent.getService(contexto, 0, it, 0);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.add(Calendar.SECOND, segundos);
		AlarmManager alarme = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
		long tempo = c.getTimeInMillis();
		alarme.set(AlarmManager.RTC_WAKEUP, tempo, p);
	}
}
