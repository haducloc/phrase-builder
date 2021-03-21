package com.appslandia.core.views;

import com.appslandia.core.utils.ViewUtils;
import com.appslandia.phrasebuilder.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class LeoLabelX extends TextView implements OnTouchListener {

	protected Drawable xIcon;

	protected OnTouchListener onTouchListener;
	protected OnCloseClickedListener onCloseClickedListener;

	public interface OnCloseClickedListener {
		void onCloseClicked(TextView textView);
	}

	public LeoLabelX(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray props = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LeoWidgets, 0, 0);
		initialize(props);
		props.recycle();
	}

	public LeoLabelX(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray props = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LeoWidgets, defStyle, 0);
		initialize(props);
		props.recycle();
	}

	protected void initialize(TypedArray props) {
		xIcon = props.getDrawable(R.styleable.LeoWidgets_xIcon);
		ViewUtils.setDrawableIntrinsicBounds(xIcon);

		setCompoundIcons(getCompoundDrawables());
		super.setOnTouchListener(this);
	}

	protected void setCompoundIcons(Drawable[] compoundDrawables) {
		setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], xIcon, compoundDrawables[3]);
	}

	@Override
	public void setOnTouchListener(OnTouchListener listener) {
		onTouchListener = listener;
	}

	public void setOnCloseClickedListener(OnCloseClickedListener listener) {
		onCloseClickedListener = listener;
	}

	protected boolean handleRightAction() {
		return true;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (xIcon != null) {
			boolean tappedX = event.getX() > (getWidth() - getPaddingRight() - xIcon.getIntrinsicWidth());
			if (tappedX && handleRightAction()) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (onCloseClickedListener != null) {
						onCloseClickedListener.onCloseClicked(this);
					}
				}
				return true;
			}
		}
		if (onTouchListener != null) {
			return onTouchListener.onTouch(v, event);
		}
		return false;
	}
}