package com.appslandia.core.views;

import com.appslandia.phrasebuilder.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class Loading extends DialogImpl {

	public Loading() {
	}

	public DialogImpl setMessage(String value) {
		getInputArgs().putString(ARGUMENT_1, value);
		return this;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.libs_loading_progress, null);
		TextView loadingTv = (TextView) view.findViewById(android.R.id.text1);

		if (getArguments() != null) {
			String message = getArguments().getString(ARGUMENT_1);
			if (message != null) {
				loadingTv.setText(message);
			}
		}
		return view;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dlg = super.onCreateDialog(savedInstanceState);
		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dlg;
	}
}
