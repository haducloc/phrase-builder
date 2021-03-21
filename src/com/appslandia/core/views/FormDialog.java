package com.appslandia.core.views;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class FormDialog extends DialogImpl {

	public FormDialog() {
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TextView titleTextView = (TextView) view.findViewById(android.R.id.text1);
		ImageView actionView = (ImageView) view.findViewById(android.R.id.button1);

		initTitleView(titleTextView);
		initRightAction(actionView);
	}

	protected abstract void initTitleView(TextView titleTextView);

	protected abstract void initRightAction(ImageView actionView);

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dlg = super.onCreateDialog(savedInstanceState);
		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dlg;
	}
}
