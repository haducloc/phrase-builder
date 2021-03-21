package com.appslandia.core.views;

import com.appslandia.core.utils.ViewUtils;
import com.appslandia.phrasebuilder.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;

public class LeoNumberPicker extends DialogImpl {

	public static final String ARGUMENT_TITLE = "title";
	public static final String ARGUMENT_MIN = "min";
	public static final String ARGUMENT_MAX = "max";
	public static final String ARGUMENT_DISPLAYED_VALUES = "displayedValues";
	public static final String ARGUMENT_VALUE = "value";

	public static interface Callbacks {

		void onNumberSet(int number);
	}

	public LeoNumberPicker setTitle(String title) {
		getInputArgs().putString(ARGUMENT_TITLE, title);
		return this;
	}

	public LeoNumberPicker setMin(int min) {
		getInputArgs().putInt(ARGUMENT_MIN, min);
		return this;
	}

	public LeoNumberPicker setMax(int max) {
		getInputArgs().putInt(ARGUMENT_MAX, max);
		return this;
	}

	public LeoNumberPicker setDisplayedValues(String[] displayedValues) {
		getInputArgs().putStringArray(ARGUMENT_DISPLAYED_VALUES, displayedValues);
		return this;
	}

	public LeoNumberPicker setValue(int value) {
		getInputArgs().putInt(ARGUMENT_VALUE, value);
		return this;
	}

	protected Callbacks getCallbacks() {
		if (isHostFragment()) {
			return (Callbacks) getTargetFragment();
		} else {
			return (Callbacks) getActivity();
		}
	}

	protected Formatter getFormatter() {
		return null;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		final NumberPicker picker = new NumberPicker(getActivity());

		picker.setMinValue(args.getInt(ARGUMENT_MIN));
		picker.setMaxValue(args.getInt(ARGUMENT_MAX));

		picker.setDisplayedValues(args.getStringArray(ARGUMENT_DISPLAYED_VALUES));
		picker.setValue(args.getInt(ARGUMENT_VALUE));
		picker.setFormatter(getFormatter());

		ViewUtils.initNumberPicker(picker);

		// builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(args.getString(ARGUMENT_TITLE));
		builder.setView(picker);

		builder.setNegativeButton(getString(R.string.button_cancel), null);
		builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getCallbacks().onNumberSet(picker.getValue());
			}
		});
		return builder.create();
	}
}
