package com.appslandia.core.views;

import com.appslandia.phrasebuilder.R;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class YesConfirmDialog extends DialogImpl {

	public static final String ARGUMENT_MESSAGE = "message";
	public static final String INPUT_YES = "yes";

	public static interface Callbacks {

		void doNoConfirm(YesConfirmDialog dlg);

		void doYesConfirm(YesConfirmDialog dlg);
	}

	public YesConfirmDialog() {
	}

	LeoEditTextVal yesInput;
	View yesBtn;

	@Override
	public void onDestroyView() {
		yesInput.removeCallbacks();

		super.onDestroyView();
	}

	protected Callbacks getCallbacks() {
		if (isHostFragment()) {
			return (Callbacks) getTargetFragment();
		} else {
			return (Callbacks) getActivity();
		}
	}

	public YesConfirmDialog setMessage(String value) {
		getInputArgs().putString(ARGUMENT_MESSAGE, value);
		return this;
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);

		yesBtn.setEnabled(INPUT_YES.equalsIgnoreCase(yesInput.getText().toString().trim()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View viewRoot = inflater.inflate(R.layout.libs_yes_confirm_dialog, container, false);

		// messageTextView
		TextView messageTextView = (TextView) viewRoot.findViewById(R.id.libs_yes_confirm_dialog_message_textview);
		messageTextView.setText(getInputArgs().getString(ARGUMENT_MESSAGE));

		// yesBtn
		yesBtn = viewRoot.findViewById(R.id.libs_yes_confirm_dialog_yes_button);
		yesBtn.setEnabled(false);
		yesBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				YesConfirmDialog.this.dismissAllowingStateLoss();
				getCallbacks().doYesConfirm(YesConfirmDialog.this);
			}
		});

		// yesInput
		yesInput = (LeoEditTextVal) viewRoot.findViewById(R.id.libs_yes_confirm_dialog_yes_edittext);
		yesInput.setTempTextWatcher(new TextWatcherImpl() {

			@Override
			public void afterTextChanged(Editable s) {
				String yes = s.toString().trim();
				if (INPUT_YES.equalsIgnoreCase(yes)) {
					yesBtn.setEnabled(true);
					yesInput.setError(null);
				} else {
					yesBtn.setEnabled(false);
					if (yes.isEmpty()) {
						yesInput.setError(null);
					} else {
						yesInput.setError(getString(R.string.message_error_yes_confirm_please));
					}
				}
			}
		});
		yesInput.postAttachTextWatcherRunnable();

		// noButton
		View noButton = viewRoot.findViewById(R.id.libs_yes_confirm_dialog_no_button);
		noButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				YesConfirmDialog.this.dismissAllowingStateLoss();
				getCallbacks().doNoConfirm(YesConfirmDialog.this);
			}
		});

		return viewRoot;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dlg = super.onCreateDialog(savedInstanceState);
		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dlg;
	}
}
