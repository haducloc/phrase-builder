package com.appslandia.core.views;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;

public class MessageDialog extends AbstractAlertDialog {

	public static final String ARGUMENT_BUTTON_TEXT = "buttonText";
	public static final String ARGUMENT_HANDLE_CLOSED = "handleClosed";

	public static interface Callbacks {

		void onMessageClosed(MessageDialog dlg);
	}

	public MessageDialog setButtonText(String text) {
		getInputArgs().putString(ARGUMENT_BUTTON_TEXT, text);
		return this;
	}

	public MessageDialog setHandleClosed(boolean handleClosed) {
		getInputArgs().putBoolean(ARGUMENT_HANDLE_CLOSED, handleClosed);
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
	protected void initBuilder(Builder builder, final Bundle args) {
		super.initBuilder(builder, args);

		builder.setNeutralButton(args.getString(ARGUMENT_BUTTON_TEXT), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (args.getBoolean(ARGUMENT_HANDLE_CLOSED)) {
					getCallbacks().onMessageClosed(MessageDialog.this);
				}
			}
		});
	}
}