package com.appslandia.core.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmUtils {

	public static void createInexactRepeatingBackup(Context context, Class<?> handlerClass, long triggerAtMs, long intervalMs, int requestCode) {
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, handlerClass);

		PendingIntent operation = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMs, intervalMs, operation);
	}

	public static void tryCreateInexactRepeatingBackup(Context context, Class<?> handlerClass, long triggerAtMs, long intervalMs, int requestCode) {
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, handlerClass);
		PendingIntent operation = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);

		// Not created yet?
		if (operation == null) {
			operation = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMs, intervalMs, operation);
		}
	}
}
