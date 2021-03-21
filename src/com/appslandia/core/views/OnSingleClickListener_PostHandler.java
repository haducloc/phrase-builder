package com.appslandia.core.views;

import android.os.Handler;
import android.view.View;

public abstract class OnSingleClickListener_PostHandler implements View.OnClickListener {

	private final long clickDelayedMs;
	private volatile int clickCount = 0;

	private final Runnable enableClickRunnable = new Runnable() {

		@Override
		public void run() {
			clickCount = 0;
		}
	};

	public OnSingleClickListener_PostHandler() {
		this(500);
	}

	public OnSingleClickListener_PostHandler(long clickDelayedMs) {
		this.clickDelayedMs = clickDelayedMs;
	}

	@Override
	final public void onClick(View v) {
		if (++clickCount == 1) {
			try {
				onSingleClick(v);
			} finally {
				getPostHandler().postDelayed(enableClickRunnable, clickDelayedMs);
			}
		}
	}

	protected abstract Handler getPostHandler();

	public void removeCallbacks() {
		getPostHandler().removeCallbacks(enableClickRunnable);
	}

	protected abstract void onSingleClick(View v);
}
