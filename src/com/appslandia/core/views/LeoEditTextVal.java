package com.appslandia.core.views;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class LeoEditTextVal extends EditText {

	private TextWatcher tempTextWatcher;

	public LeoEditTextVal(Context context) {
		super(context);
	}

	public LeoEditTextVal(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LeoEditTextVal(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setTempTextWatcher(TextWatcher tempTextWatcher) {
		this.tempTextWatcher = tempTextWatcher;
	}

	private Runnable attachTextWatcherRunnable;

	protected Runnable getAttachTextWatcherRunnable() {

		if (this.attachTextWatcherRunnable == null) {
			this.attachTextWatcherRunnable = new Runnable() {
				@Override
				public void run() {
					addTextChangedListener(tempTextWatcher);
				}
			};
		}
		return this.attachTextWatcherRunnable;
	}

	public void postAttachTextWatcherRunnable() {
		post(getAttachTextWatcherRunnable());
	}

	public void removeCallbacks() {
		removeCallbacks(getAttachTextWatcherRunnable());
	}
}
