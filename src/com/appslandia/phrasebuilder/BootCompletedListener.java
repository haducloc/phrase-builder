package com.appslandia.phrasebuilder;

import com.appslandia.phrasebuilder.utils.AppBackupUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		AppBackupUtils.tryCreateInexactRepeatingBackup(context);
	}
}
