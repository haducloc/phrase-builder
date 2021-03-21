package com.appslandia.core.utils;

import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("DefaultLocale")
public final class LogUtils {

	public static boolean enableDebug = true;

	private static final AtomicInteger LOG_SEQ = new AtomicInteger();

	private static String format(Object obj) {
		return String.format("-----[%d]: %s", (LOG_SEQ.incrementAndGet()), String.valueOf(obj));
	}

	private static String formatEnter(Object obj) {
		return String.format("----->[%d]: %s", (LOG_SEQ.incrementAndGet()), String.valueOf(obj));
	}

	private static String formatExit(Object obj) {
		return String.format("<-----[%d]: %s", (LOG_SEQ.incrementAndGet()), String.valueOf(obj));
	}

	private static String getLogTag(Class<?> clazz) {
		String tag = clazz.getSimpleName();
		if (tag.isEmpty()) {
			tag = clazz.getName();
			tag = tag.substring(tag.lastIndexOf('.') + 1);
		}
		return tag;
	}

	public static void debug(Class<?> clazz, Object obj) {
		if (enableDebug) {
			Log.d(getLogTag(clazz), format(obj));
		}
	}

	public static void debugEnter(Class<?> clazz, Object obj) {
		if (enableDebug) {
			Log.d(getLogTag(clazz), formatEnter(obj));
		}
	}

	public static void debugExit(Class<?> clazz, Object obj) {
		if (enableDebug) {
			Log.d(getLogTag(clazz), formatExit(obj));
		}
	}
}
