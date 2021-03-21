package com.appslandia.core.views;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Parcelable;

public abstract class DialogImpl extends DialogFragment {

	public static final String ARGUMENT_ID = "id";
	public static final String ARGUMENT_HOST_FRAGMENT = "hostFragment";

	public static final String ARGUMENT_1 = "argument1";
	public static final String ARGUMENT_2 = "argument2";
	public static final String ARGUMENT_3 = "argument3";

	public Bundle getInputArgs() {
		Bundle args = getArguments();
		if (args == null) {
			args = new Bundle(1);
			setArguments(args);
		}
		return args;
	}

	public int getDialogId() {
		return getInputArgs().getInt(ARGUMENT_ID);
	}

	public DialogImpl setDialogId(int id) {
		getInputArgs().putInt(ARGUMENT_ID, id);
		return this;
	}

	public int getIntArgument1() {
		return getInputArgs().getInt(ARGUMENT_1);
	}

	public DialogImpl setIntArgument1(int value) {
		getInputArgs().putInt(ARGUMENT_1, value);
		return this;
	}

	public String getStringArgument1() {
		return getInputArgs().getString(ARGUMENT_1);
	}

	public DialogImpl setStringArgument1(String value) {
		getInputArgs().putString(ARGUMENT_1, value);
		return this;
	}

	public Parcelable getParcelArgument1() {
		return getInputArgs().getParcelable(ARGUMENT_1);
	}

	public DialogImpl setParcelArgument1(Parcelable obj) {
		getInputArgs().putParcelable(ARGUMENT_1, obj);
		return this;
	}

	public boolean isHostFragment() {
		return getArguments() != null ? getArguments().getBoolean(ARGUMENT_HOST_FRAGMENT) : (false);
	}

	public DialogImpl setHostFragment(Fragment fragment, int requestCode) {
		super.setTargetFragment(fragment, requestCode);
		getInputArgs().putBoolean(ARGUMENT_HOST_FRAGMENT, true);
		return this;
	}

	public DialogImpl setUncancelable() {
		setCancelable(false);
		return this;
	}

	public void show(FragmentManager manager) {
		String tag = this.getClass().getName();
		if (manager.findFragmentByTag(tag) == null) {
			super.show(manager, tag);
		}
	}

	public void showAllowingStateLoss(FragmentManager manager) {
		String tag = this.getClass().getName();
		if (manager.findFragmentByTag(tag) == null) {
			manager.beginTransaction().add(this, tag).commitAllowingStateLoss();
		}
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		throw new UnsupportedOperationException("Use show(FragmentManager) instead!");
	}

	@Override
	public int show(FragmentTransaction transaction, String tag) {
		throw new UnsupportedOperationException("Use show(FragmentManager) instead!");
	}

	@Override
	public void dismiss() {
		throw new UnsupportedOperationException("Use static dismiss(FragmentManager) instead!");
	}

	public static void dismiss(FragmentManager manager, Class<? extends DialogImpl> tagClass) {
		if (manager != null) {
			DialogImpl dlg = (DialogImpl) manager.findFragmentByTag(tagClass.getName());
			if (dlg != null) {
				dlg.dismissAllowingStateLoss();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends DialogImpl> T find(FragmentManager manager, Class<T> tagClass) {
		return (T) manager.findFragmentByTag(tagClass.getName());
	}
}
