package com.appslandia.core.views;

import java.util.LinkedList;

import android.view.View;

public abstract class FastClicksListener implements View.OnClickListener {

	private final LinkedList<Long> clickTimestamps = new LinkedList<Long>();

	private final int clicks;
	private final int clickDis;

	public FastClicksListener() {
		this(2, 360);
	}

	public FastClicksListener(int clicks, int clickDis) {
		this.clicks = clicks;
		this.clickDis = clickDis;
	}

	@Override
	public void onClick(View v) {
		recordClick(v, System.currentTimeMillis());
	}

	protected abstract void onTripleClick(View v);

	protected void recordClick(View v, long clickTimestamp) {
		if (clickTimestamps.isEmpty()) {
			clickTimestamps.add(clickTimestamp);
		} else {
			long lastClick = clickTimestamps.getLast();
			if (clickTimestamp - lastClick <= clickDis) {

				if (clickTimestamps.size() < this.clicks - 1) {
					clickTimestamps.add(clickTimestamp);
				} else {
					clickTimestamps.clear();
					onTripleClick(v);
				}
			} else {
				clickTimestamps.clear();
				clickTimestamps.add(clickTimestamp);
			}
		}
	}
}
