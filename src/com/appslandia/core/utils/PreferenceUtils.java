package com.appslandia.core.utils;

import android.content.Context;

public class PreferenceUtils {

	public static void savePreference(Context context, String name, String key, String value) {
		context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putString(key, value).commit();
	}

	public static void savePreference(Context context, String name, String key, boolean value) {
		context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
	}

	public static void savePreference(Context context, String name, String key, int value) {
		context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
	}

	public static void savePreference(Context context, String name, String key, long value) {
		context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().putLong(key, value).commit();
	}

	public static String getString(Context context, String name, String key, String defValue) {
		return context.getSharedPreferences(name, Context.MODE_PRIVATE).getString(key, defValue);
	}

	public static boolean getBoolean(Context context, String name, String key, boolean defValue) {
		return context.getSharedPreferences(name, Context.MODE_PRIVATE).getBoolean(key, defValue);
	}

	public static int getInt(Context context, String name, String key, int defValue) {
		return context.getSharedPreferences(name, Context.MODE_PRIVATE).getInt(key, defValue);
	}

	public static long getLong(Context context, String name, String key, long defValue) {
		return context.getSharedPreferences(name, Context.MODE_PRIVATE).getLong(key, defValue);
	}
}
