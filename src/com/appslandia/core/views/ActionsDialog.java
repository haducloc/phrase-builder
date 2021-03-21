package com.appslandia.core.views;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;

public class ActionsDialog extends AbstractAlertDialog {

	public static final String ARGUMENT_NEGATIVE_TEXT = "negativeText";
	public static final String ARGUMENT_POSITIVE_TEXT = "positiveText";

	public static interface Callbacks {

		void doPositiveClick(ActionsDialog dlg);

		void doNegativeClick(ActionsDialog dlg);
	}

	public ActionsDialog setNegativeText(String text) {
		getInputArgs().putString(ARGUMENT_NEGATIVE_TEXT, text);
		return this;
	}

	public ActionsDialog setPositiveText(String text) {
		getInputArgs().putString(ARGUMENT_POSITIVE_TEXT, text);
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
		super.initBuilder(builder, args);

		builder.setNegativeButton(args.getString(ARGUMENT_NEGATIVE_TEXT), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getCallbacks().doNegativeClick(ActionsDialog.this);
			}
		});
		builder.setPositiveButton(args.getString(ARGUMENT_POSITIVE_TEXT), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getCallbacks().doPositiveClick(ActionsDialog.this);
			}
		});
	}
}