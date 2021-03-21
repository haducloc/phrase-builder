package com.appslandia.core.views;

import android.view.View;
import android.widget.AdapterView;

public abstract class OnListItemClickListener implements AdapterView.OnItemClickListener {

	private final long clickDelayedMs;
	private volatile int clickCount = 0;

	private final Runnable enableClickRunnable = new Runnable() {

		@Override
		public void run() {
			clickCount = 0;
		}
	};

	public OnListItemClickListener() {
		this(1000);
	}

	public OnListItemClickListener(long clickDelayedMs) {
		this.clickDelayedMs = clickDelayedMs;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (++clickCount == 1) {
			try {
				onSingleClick(parent, view, position, id);
			} finally {
				parent.postDelayed(enableClickRunnable, clickDelayedMs);
			}
		}
	}

	public void removeCallbackOn(View adapterView) {
		adapterView.removeCallbacks(enableClickRunnable);
	}

	protected abstract void onSingleClick(AdapterView<?> parent, View view, int position, long id);
}
