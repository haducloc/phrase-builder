package com.appslandia.core.views;

import com.appslandia.phrasebuilder.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TimePicker;

public class LeoTimePicker extends DialogImpl {

	public static final String ARGUMENT_HOUR = "hour";
	public static final String ARGUMENT_MINUTE = "minute";
	public static final String ARGUMENT_HOUR_CLOCK_24 = "hourClock24";

	public static interface Callbacks {

		void onTimeSet(int newHour24, int newMinute);
	}

	public LeoTimePicker setHour(int hour) {
		getInputArgs().putInt(ARGUMENT_HOUR, hour);
		return this;
	}

	public LeoTimePicker setMinute(int minute) {
		getInputArgs().putInt(ARGUMENT_MINUTE, minute);
		return this;
	}

	public LeoTimePicker setHourClock24(boolean hourClock24) {
		getInputArgs().putBoolean(ARGUMENT_HOUR_CLOCK_24, hourClock24);
		return this;
	}

	protected Callbacks getCallbacks() {
		if (isHostFragment()) {
			return (Callbacks) getTargetFragment();
		} else {
			return (Callbacks) getActivity();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();

		int hour = (args != null) ? args.getInt(ARGUMENT_HOUR, 12) : (12);
		int minute = (args != null) ? args.getInt(ARGUMENT_MINUTE, 0) : (0);
		boolean hourClock24 = (args != null) ? args.getBoolean(ARGUMENT_HOUR_CLOCK_24, false) : (false);

		final TimePicker timePicker = new TimePicker(getActivity());
		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);
		timePicker.setIs24HourView(hourClock24);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.title_set_time));
		builder.setView(timePicker);

		builder.setNegativeButton(getString(R.string.button_cancel), null);
		builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getCallbacks().onTimeSet(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
			}
		});
		return builder.create();
	}
}
