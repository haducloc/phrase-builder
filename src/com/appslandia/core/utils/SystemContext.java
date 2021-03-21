package com.appslandia.core.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;

public class SystemContext {

	private static SystemContext instance;
	private static final Object MUTEX = new Object();

	private Context context;
	private String androidID;
	private final Object mutex = new Object();

	public static SystemContext getInstance(Context context) {
		if (instance == null) {
			synchronized (MUTEX) {
				if (instance == null) {
					instance = new SystemContext(context);
				}
			}
		}
		return instance;
	}

	private SystemContext(Context context) {
		this.context = context.getApplicationContext();
	}

	public String getAndroidID() {
		if (this.androidID == null) {
			synchronized (this.mutex) {
				if (this.androidID == null) {
					this.androidID = Secure.getString(this.context.getContentResolver(), Secure.ANDROID_ID);
				}
			}
		}
		return this.androidID;
	}

	public boolean isConnectivityAvailable() {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}
}
