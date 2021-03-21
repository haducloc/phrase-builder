package com.appslandia.phrasebuilder;

import com.appslandia.phrasebuilder.utils.AppBackupUtils;

import android.app.Application;

public class ApplicationImpl extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		AppBackupUtils.tryCreateInexactRepeatingBackup(this);
	}
}
