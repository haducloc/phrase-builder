package com.appslandia.core.utils;

import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public final class ToastUtils {

	private static final class ToastSeqHolder {
		static final AtomicInteger Instance = new AtomicInteger(0);
	}

	public static void shortToast(Context context, Object message) {
		String msg = String.format("[%03d] %s", ToastSeqHolder.Instance.incrementAndGet(), String.valueOf(message));
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void longToast(Context context, Object message) {
		String msg = String.format("[%03d] %s", ToastSeqHolder.Instance.incrementAndGet(), String.valueOf(message));
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
}
