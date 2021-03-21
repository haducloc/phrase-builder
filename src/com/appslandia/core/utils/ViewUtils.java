package com.appslandia.core.utils;

import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class ViewUtils {

	private static final class ViewIdSeqHolder {
		static final AtomicInteger Instance = new AtomicInteger(1);
	}

	@SuppressLint("NewApi")
	public static int nextId() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			for (;;) {
				final int result = ViewIdSeqHolder.Instance.get();
				int newValue = result + 1;
				if (newValue > 0x00FFFFFF)
					newValue = 1; // Roll over to 1, not 0.
				if (ViewIdSeqHolder.Instance.compareAndSet(result, newValue)) {
					return result;
				}
			}
		} else {
			return View.generateViewId();
		}
	}

	public static boolean setSelectionItemId(Spinner spinner, int id) {
		if (id <= 0) {
			return false;
		}
		SpinnerAdapter adapter = spinner.getAdapter();
		int count = adapter.getCount();

		for (int pos = 0; pos < count; pos++) {
			int itemId = (int) adapter.getItemId(pos);

			if (id == itemId) {
				if (pos != spinner.getSelectedItemPosition()) {
					spinner.setSelection(pos);
				}
				return true;
			}
		}
		return false;
	}

	public static int dpToPx(DisplayMetrics displayMetrics, float dp) {
		return (int) (dp * displayMetrics.density + 0.5f);
	}

	public static float pxToDp(DisplayMetrics displayMetrics, int px) {
		return px / displayMetrics.density;
	}

	public static void setTransparentDivider(ListView list, int heightDp) {
		list.setDivider(null);
		list.setDividerHeight(ViewUtils.dpToPx(list.getResources().getDisplayMetrics(), heightDp));
	}

	public static void setDrawableIntrinsicBounds(Drawable drawable) {
		if (drawable != null) {
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		}
	}

	public static void hideSoftInput(EditText editText) {
		InputMethodManager manager = (InputMethodManager) editText.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (manager != null) {
			manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}
	}

	public static void showSoftInput(EditText editText) {
		InputMethodManager manager = (InputMethodManager) editText.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (manager != null) {
			manager.showSoftInput(editText, 0);
		}
	}

	public static void initEditTextWrapper(EditText editText, final int normalBackgroundResId, final int focusBackgroundResId) {
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					((ViewGroup) v.getParent()).setBackgroundResource(focusBackgroundResId);
				} else {
					((ViewGroup) v.getParent()).setBackgroundResource(normalBackgroundResId);
				}
			}
		});
	}

	public static void initNumberPicker(NumberPicker numPicker) {
		int count = numPicker.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = numPicker.getChildAt(i);
			if (view instanceof EditText) {

				EditText editText = (EditText) view;
				editText.setEnabled(false);
				editText.setTextColor(Color.BLACK);
				break;
			}
		}
	}
}
