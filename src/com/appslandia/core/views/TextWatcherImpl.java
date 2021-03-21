package com.appslandia.core.views;

import android.text.TextWatcher;

public abstract class TextWatcherImpl implements TextWatcher {

	@Override
	public void onTextChanged(CharSequence answer, int start, int before, int count) {
	}

	@Override
	public void beforeTextChanged(CharSequence answer, int start, int count, int after) {
	}
}
