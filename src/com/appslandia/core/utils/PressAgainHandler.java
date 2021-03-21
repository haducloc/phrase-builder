package com.appslandia.core.utils;

import com.appslandia.phrasebuilder.R;

import android.app.Activity;
import android.widget.Toast;

public class PressAgainHandler {

	private long prevPressTimeMillis = 0;
	private final int pressDisMs;

	public PressAgainHandler() {
		this(2500);
	}

	public PressAgainHandler(int pressDisMs) {
		this.pressDisMs = pressDisMs;
	}

	public boolean isPressTimeMsRecorded() {
		return this.prevPressTimeMillis > 0;
	}

	public void recordPressTimeMs(Activity activity) {
		this.prevPressTimeMillis = System.currentTimeMillis();
		Toast.makeText(activity, R.string.message_press_again_to_exit, Toast.LENGTH_SHORT).show();
	}

	public boolean canBackPress(Activity activity) {
		long current = System.currentTimeMillis();
		if (current - this.prevPressTimeMillis <= this.pressDisMs) {
			return true;
		} else {
			this.prevPressTimeMillis = current;
			Toast.makeText(activity, R.string.message_press_again_to_exit, Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}
