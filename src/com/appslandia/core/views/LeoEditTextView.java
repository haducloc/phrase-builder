package com.appslandia.core.views;

import com.appslandia.phrasebuilder.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class LeoEditTextView extends TextView {

	public LeoEditTextView(Context context) {
		super(context);

		initialize();
	}

	public LeoEditTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initialize();
	}

	public LeoEditTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initialize();
	}

	protected void initialize() {
		setFocusable(true);
		setFocusableInTouchMode(true);

		setBackgroundResource(getBackgroudResId());
	}

	protected int getBackgroudResId() {
		return R.drawable.ic_lib_readonly_edittext;
	}
}
