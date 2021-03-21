package com.appslandia.phrasebuilder;

import com.appslandia.core.utils.BackupUtils;

import android.app.backup.BackupManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BackupRequestReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		new BackupManager(context).dataChanged();

		BackupUtils.notifyBackupRequested(context, R.drawable.ic_notification_phrasebuilder, R.string.app_name, MainActivity.class);
	}
}
