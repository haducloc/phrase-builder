package com.appslandia.core.utils;

import java.io.Closeable;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import android.util.SparseArray;

public class AssetDescManager implements Closeable {
	private static final String TAG = AssetDescManager.class.getSimpleName();

	private final Context context;
	private final SparseArray<AssetFileDescriptor> descMap;

	public AssetDescManager(Context context) {
		this(context, 1);
	}

	public AssetDescManager(Context context, int initialCapacity) {
		this.context = context;
		this.descMap = new SparseArray<>(initialCapacity);
	}

	public AssetFileDescriptor getAssetDesc(boolean expression, int trueResId, int falseResId) {
		return expression ? getAssetDesc(trueResId) : getAssetDesc(falseResId);
	}

	public AssetFileDescriptor getAssetDesc(int resId) {
		AssetFileDescriptor desc = descMap.get(resId);
		if (desc == null) {
			desc = context.getResources().openRawResourceFd(resId);
			descMap.put(resId, desc);
		}
		return desc;
	}

	public void close(int resId) throws IOException {
		AssetFileDescriptor desc = descMap.get(resId);
		if (desc != null) {
			desc.close();
		}
	}

	public void close() throws IOException {
		int count = descMap.size();
		for (int i = 0; i < count; i++) {
			AssetFileDescriptor desc = descMap.valueAt(i);
			desc.close();
		}
	}

	public void tryClose(int resId) {
		AssetFileDescriptor desc = descMap.get(resId);
		if (desc != null) {
			try {
				desc.close();
			} catch (IOException ex) {
				Log.d(TAG, ex.getMessage());
			}
		}
	}

	public void tryClose() {
		int count = descMap.size();
		for (int i = 0; i < count; i++) {
			AssetFileDescriptor desc = descMap.valueAt(i);
			try {
				desc.close();
			} catch (IOException ex) {
				Log.d(TAG, ex.getMessage());
			}
		}
	}
}
