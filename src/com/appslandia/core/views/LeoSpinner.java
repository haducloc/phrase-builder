package com.appslandia.core.views;

import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class LeoSpinner extends Spinner {

	public LeoSpinner(Context context) {
		super(context);
	}

	public LeoSpinner(Context context, int mode) {
		super(context, mode);
	}

	public LeoSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LeoSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LeoSpinner(Context context, AttributeSet attrs, int defStyle, int mode) {
		super(context, attrs, defStyle, mode);
	}

	public boolean setSelectionItemId(int id) {
		if (id <= 0) {
			return false;
		}
		SpinnerAdapter adapter = getAdapter();
		int count = adapter.getCount();

		for (int pos = 0; pos < count; pos++) {
			int itemId = (int) adapter.getItemId(pos);

			if (id == itemId) {
				if (pos != getSelectedItemPosition()) {
					setSelection(pos, false);
				}
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> boolean checkObjects(List<T> objects) {
		return ((ArrayAdapterImpl<T>) getAdapter()).checkObjects(objects);
	}

	@SuppressWarnings("unchecked")
	public <T> boolean setObjects(List<T> objects) {
		return ((ArrayAdapterImpl<T>) getAdapter()).setObjects(objects);
	}

	public void notifyDataSetChanged() {
		((BaseAdapter) getAdapter()).notifyDataSetChanged();
	}
}
