package com.appslandia.core.views;

import android.app.Activity;

public abstract class ActivityImpl extends Activity {

	public static final int STATE_RESUMED = 1;
	public static final int STATE_NOT_RESUMED = 2;

	protected int activityState;

	@Override
	protected void onResume() {
		super.onResume();
		this.activityState = STATE_RESUMED;
	}

	@Override
	protected void onPause() {
		super.onPause();

		this.activityState = STATE_NOT_RESUMED;
	}

	public boolean isStateResumed() {
		return this.activityState == STATE_RESUMED;
	}
}
