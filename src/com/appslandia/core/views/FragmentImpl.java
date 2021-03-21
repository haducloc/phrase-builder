package com.appslandia.core.views;

import android.app.Fragment;

public abstract class FragmentImpl extends Fragment {

	public static final int STATE_RESUMED = 1;
	public static final int STATE_NOT_RESUMED = 2;

	protected int fragmentState;

	@Override
	public void onResume() {
		super.onResume();
		this.fragmentState = STATE_RESUMED;
	}

	@Override
	public void onPause() {
		super.onPause();

		this.fragmentState = STATE_NOT_RESUMED;
	}

	public boolean isStateResumed() {
		return this.fragmentState == STATE_RESUMED;
	}
}
