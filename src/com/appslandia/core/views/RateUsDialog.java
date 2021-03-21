package com.appslandia.core.views;

import com.appslandia.phrasebuilder.R;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;

public class RateUsDialog extends AbstractAlertDialog {

	public static final String ARGUMENT_NO_THANKS = "noThanks";

	public static interface Callbacks {

		void onRateUsDialogResult(int button);
	}

	public RateUsDialog setAddNoThanks(boolean noThanks) {
		getInputArgs().putBoolean(ARGUMENT_NO_THANKS, noThanks);
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
	protected void initBuilder(Builder builder, Bundle args) {

		builder.setTitle(getString(R.string.title_rate_us));
		builder.setMessage(getString(R.string.message_rate_us));

		builder.setPositiveButton(getString(R.string.button_rate_now), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getCallbacks().onRateUsDialogResult(DialogInterface.BUTTON_POSITIVE);
			}
		});
		builder.setNeutralButton(getString(R.string.button_rate_later), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getCallbacks().onRateUsDialogResult(DialogInterface.BUTTON_NEUTRAL);
			}
		});

		if (args.getBoolean(ARGUMENT_NO_THANKS)) {
			builder.setNegativeButton(getString(R.string.button_no_thanks), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					getCallbacks().onRateUsDialogResult(DialogInterface.BUTTON_NEGATIVE);
				}
			});
		}
	}
}
