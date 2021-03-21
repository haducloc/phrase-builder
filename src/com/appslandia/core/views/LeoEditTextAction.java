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
import android.widget.EditText;

public class LeoEditTextAction extends EditText implements OnTouchListener {

	protected Drawable leftIcon;
	protected Drawable rightIcon;

	protected OnTouchListener onTouchListener;
	protected OnTextViewActionListener onTextViewActionListener;

	public LeoEditTextAction(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray props = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LeoWidgets, 0, 0);
		initialize(props);
		props.recycle();
	}

	public LeoEditTextAction(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray props = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LeoWidgets, defStyle, 0);
		initialize(props);
		props.recycle();
	}

	protected void initialize(TypedArray props) {
		leftIcon = props.getDrawable(R.styleable.LeoWidgets_leftIcon);
		rightIcon = props.getDrawable(R.styleable.LeoWidgets_rightIcon);

		ViewUtils.setDrawableIntrinsicBounds(leftIcon);
		ViewUtils.setDrawableIntrinsicBounds(rightIcon);

		setCompoundIcons(getCompoundDrawables());
		super.setOnTouchListener(this);
	}

	protected void setCompoundIcons(Drawable[] compoundDrawables) {
		setCompoundDrawables(leftIcon, compoundDrawables[1], rightIcon, compoundDrawables[3]);
	}

	public void showRightCompound(boolean show) {
		Drawable x = show ? rightIcon : (null);
		Drawable[] compoundDrawables = getCompoundDrawables();
		setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], x, compoundDrawables[3]);
	}

	@Override
	public void setOnTouchListener(OnTouchListener listener) {
		onTouchListener = listener;
	}

	public void setOnTextViewActionListener(OnTextViewActionListener listener) {
		this.onTextViewActionListener = listener;
	}

	protected boolean handleRightAction() {
		return true;
	}

	protected boolean handleLeftAction() {
		return true;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (rightIcon != null) {
			boolean tappedX = event.getX() > (getWidth() - getPaddingRight() - rightIcon.getIntrinsicWidth());
			if (tappedX && handleRightAction()) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (onTextViewActionListener != null) {
						onTextViewActionListener.onActionSelected(this, true);
					}
				}
				return true;
			}
		}
		if (leftIcon != null) {
			boolean tappedX = event.getX() < getPaddingLeft() + leftIcon.getIntrinsicWidth();
			if (tappedX && handleLeftAction()) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (onTextViewActionListener != null) {
						onTextViewActionListener.onActionSelected(this, false);
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