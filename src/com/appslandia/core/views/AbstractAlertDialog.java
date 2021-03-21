package com.appslandia.core.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

public abstract class AbstractAlertDialog extends DialogImpl {

	public static final String ARGUMENT_TITLE = "title";
	public static final String ARGUMENT_MESSAGE = "message";
	public static final String ARGUMENT_ICON = "icon";

	public AbstractAlertDialog setIcon(int iconResId) {
		getInputArgs().putInt(ARGUMENT_ICON, iconResId);
		return this;
	}

	public AbstractAlertDialog setTitle(String title) {
		getInputArgs().putString(ARGUMENT_TITLE, title);
		return this;
	}

	public AbstractAlertDialog setMessage(String message) {
		getInputArgs().putString(ARGUMENT_MESSAGE, message);
		return this;
	}

	public Button getPositiveButton() {
		return ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Bundle args = getInputArgs();

		initBuilder(builder, args);

		return builder.create();
	}

	protected void initBuilder(AlertDialog.Builder builder, Bundle args) {
		builder.setTitle(args.getString(ARGUMENT_TITLE));
		builder.setMessage(args.getString(ARGUMENT_MESSAGE));
		builder.setIcon(args.getInt(ARGUMENT_ICON));
	}
}