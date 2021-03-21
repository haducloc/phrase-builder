package com.appslandia.core.views;

import android.view.View;
import android.widget.AdapterView;

public abstract class OnItemSelectedListenerImpl implements AdapterView.OnItemSelectedListener {

	private boolean firstTime = true;

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public final void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (this.firstTime) {
			this.firstTime = false;
		} else {
			doOnItemSelected(parent, view, position, id);
		}
	}

	public abstract void doOnItemSelected(AdapterView<?> parent, View view, int position, long id);
}
