package com.appslandia.core.views;

import com.appslandia.phrasebuilder.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class LeoToggleView extends ImageView {

	private boolean stateOn = false;
	private OnToggleListener listener;

	private int stateOnSrc;
	private int stateOffSrc;

	public static interface OnToggleListener {
		void onToggle(View view, boolean isOn);
	}

	public LeoToggleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LeoToggleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LeoWidgets, defStyle, 0);
		this.stateOnSrc = ta.getResourceId(R.styleable.LeoWidgets_stateOnSrc, 0);
		this.stateOffSrc = ta.getResourceId(R.styleable.LeoWidgets_stateOffSrc, 0);

		this.setStateOn(false);
		ta.recycle();

		super.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setStateOn(!stateOn);
				if (listener != null) {
					listener.onToggle(v, stateOn);
				}
			}
		});
	}

	public boolean isStateOn() {
		return this.stateOn;
	}

	public void setStateOn(boolean stateOn) {
		this.stateOn = stateOn;
		if (stateOn) {
			this.setImageResource(stateOnSrc);
		} else {
			this.setImageResource(stateOffSrc);
		}
	}

	public void setStateOnSrc(int stateOnSrc) {
		if (stateOnSrc != this.stateOnSrc) {
			this.stateOnSrc = stateOnSrc;
			invalidate();
			requestLayout();
		}
	}

	public void setStateOffSrc(int stateOffSrc) {
		if (stateOffSrc != this.stateOffSrc) {
			this.stateOffSrc = stateOffSrc;
			invalidate();
			requestLayout();
		}
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		throw new UnsupportedOperationException();
	}

	public void setToggleListener(OnToggleListener listener) {
		this.listener = listener;
	}
}
