package com.appslandia.core.views;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

public abstract class RetainedFragment extends Fragment {

	public static final String FRAGMENT_TAG = RetainedFragment.class.getName();
	public static final String ARGUMENT_HOST_FRAGMENT = "hostFragment";

	protected Context context;

	public Bundle getInputArgs() {
		Bundle args = getArguments();
		if (args == null) {
			args = new Bundle(1);
			setArguments(args);
		}
		return args;
	}

	public boolean isHostFragment() {
		return getArguments() != null ? getArguments().getBoolean(ARGUMENT_HOST_FRAGMENT) : (false);
	}

	public void setHostFragment(Fragment fragment, int requestCode) {
		super.setTargetFragment(fragment, requestCode);
		getInputArgs().putBoolean(ARGUMENT_HOST_FRAGMENT, true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		this.context = getActivity().getApplicationContext();
	}
}
