package com.appslandia.phrasebuilder.utils;

import java.util.Calendar;

import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.DateUtils;
import com.appslandia.phrasebuilder.BackupRequestReceiver;
import com.appslandia.phrasebuilder.ManageBackupActivity;

import android.content.Context;
import android.content.SharedPreferences;

public class AppBackupUtils {

	public static void tryCreateInexactRepeatingBackup(Context context) {

		// Backup plan
		SharedPreferences prefs = context.getSharedPreferences(ManageBackupActivity.PREFERENCE_ID, Context.MODE_PRIVATE);

		int backupOnDay = prefs.getInt(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_ON_DAY, BackupUtils.DEFAULT_BACKUP_REQUEST_ON_DAY);
		int atHour = prefs.getInt(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_AT_HOUR, BackupUtils.DEFAULT_BACKUP_REQUEST_AT_HOUR);
		int atMinute = prefs.getInt(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_AT_MIMUTE, BackupUtils.DEFAULT_BACKUP_REQUEST_AT_MINUTE);

		// triggerCal
		Calendar triggerCal = DateUtils.getCalendar(backupOnDay, atHour, atMinute);

		// 3secs?
		if (triggerCal.getTimeInMillis() <= System.currentTimeMillis() + 3_000) {
			triggerCal.add(Calendar.DATE, BackupUtils.AUTO_REQUEST_BACKUP_INTERVAL_DAYS);
		}

		// tryCreateInexactRepeatingBackup
		BackupUtils.tryCreateInexactRepeatingBackup(context, BackupRequestReceiver.class, triggerCal.getTimeInMillis(), BackupUtils.AUTO_REQUEST_BACKUP_INTERVAL_MS);
	}
}
