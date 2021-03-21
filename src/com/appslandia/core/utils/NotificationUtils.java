package com.appslandia.core.utils;

import com.appslandia.phrasebuilder.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationUtils {

	public static final int NOTIFICATION_ID_TEST_MESSAGE = 10000;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void notifyTestMessage(Context context, String title, String message) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// @formatter:off
		Notification.Builder builder = new Notification.Builder(context)
						.setSmallIcon(R.drawable.ic_lib_notification_default)
						.setContentTitle(title)
						.setContentText(message)
						.setAutoCancel(true);
		// @formatter:on

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			manager.notify(NOTIFICATION_ID_TEST_MESSAGE, builder.getNotification());
		} else {
			manager.notify(NOTIFICATION_ID_TEST_MESSAGE, builder.build());
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void notifyMessage(Context context, int notificationId, int iconResId, int titleResId, String message, Class<?> mainActivityClass) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// openIntent
		Intent openIntent = Intent.makeMainActivity(new ComponentName(context, mainActivityClass));

		// pendingIntent
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// @formatter:off
		Notification.Builder builder = new Notification.Builder(context)
						.setSmallIcon(iconResId)
						.setContentTitle(context.getString(titleResId))
						.setContentText(message)
						.setAutoCancel(true)
						.setContentIntent(pendingIntent);
		// @formatter:on

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			manager.notify(notificationId, builder.getNotification());
		} else {
			manager.notify(notificationId, builder.build());
		}
	}
}
