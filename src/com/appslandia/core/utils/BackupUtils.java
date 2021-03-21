package com.appslandia.core.utils;

import com.appslandia.phrasebuilder.R;

import android.app.AlarmManager;
import android.content.Context;

public class BackupUtils {

	public static final Mutex MUTEX = new Mutex();

	public static final int REQUEST_CODE_BACKUP_REQUEST = 1000;

	public static final int NOTIFICATION_ID_BACKUP_REQUESTED = 2000;
	public static final int NOTIFICATION_ID_BACKUP_PERFORMED = 2001;
	public static final int NOTIFICATION_ID_BACKUP_RESTORED = 2002;

	// Sunday
	public static final int DEFAULT_BACKUP_REQUEST_ON_DAY = 1;

	// 12:05 PM
	public static final int DEFAULT_BACKUP_REQUEST_AT_HOUR = 12;
	public static final int DEFAULT_BACKUP_REQUEST_AT_MINUTE = 5;

	// 7 days
	public static final int AUTO_REQUEST_BACKUP_INTERVAL_DAYS = 7;

	// 7 days in milliseconds
	public static final long AUTO_REQUEST_BACKUP_INTERVAL_MS = AUTO_REQUEST_BACKUP_INTERVAL_DAYS * AlarmManager.INTERVAL_DAY;

	public static void createInexactRepeatingBackup(Context context, Class<?> handlerClass, long triggerAtMs, long intervalMs) {
		AlarmUtils.createInexactRepeatingBackup(context, handlerClass, triggerAtMs, intervalMs, REQUEST_CODE_BACKUP_REQUEST);
	}

	public static void tryCreateInexactRepeatingBackup(Context context, Class<?> handlerClass, long triggerAtMs, long intervalMs) {
		AlarmUtils.tryCreateInexactRepeatingBackup(context, handlerClass, triggerAtMs, intervalMs, REQUEST_CODE_BACKUP_REQUEST);
	}

	public static void notifyBackupRequested(Context context, int iconResId, int titleResId, Class<?> mainActivityClass) {
		String message = context.getString(R.string.message_backup_backup_requested_successfully, DateUtils.getNotificationTimestamp());
		NotificationUtils.notifyMessage(context, NOTIFICATION_ID_BACKUP_REQUESTED, iconResId, titleResId, message, mainActivityClass);
	}

	public static void notifyBackupPerformed(Context context, int iconResId, int titleResId, Class<?> mainActivityClass) {
		String message = context.getString(R.string.message_backup_backup_performed_successfully, DateUtils.getNotificationTimestamp());
		NotificationUtils.notifyMessage(context, NOTIFICATION_ID_BACKUP_PERFORMED, iconResId, titleResId, message, mainActivityClass);
	}

	public static void notifyBackupRestored(Context context, int iconResId, int titleResId, Class<?> mainActivityClass) {
		String message = context.getString(R.string.message_backup_backup_restored_successfully);
		NotificationUtils.notifyMessage(context, NOTIFICATION_ID_BACKUP_RESTORED, iconResId, titleResId, message, mainActivityClass);
	}
}
