package com.appslandia.core.views;

import com.appslandia.core.utils.PressAgainHandler;

public abstract class PressAgainActivity extends ActivityImpl {

	protected PressAgainHandler pressAgainHandler;

	protected PressAgainHandler initPressAgainHandler() {
		return new PressAgainHandler();
	}

	@Override
	public void onBackPressed() {
		if (pressAgainHandler == null) {
			this.pressAgainHandler = initPressAgainHandler();
		}
		if (pressAgainHandler.isPressTimeMsRecorded()) {
			if (pressAgainHandler.canBackPress(this)) {
				super.onBackPressed();
			}
		} else {
			pressAgainHandler.recordPressTimeMs(this);
		}
	}
}
