package com.appslandia.core.views;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.Item;
import com.appslandia.phrasebuilder.R;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;

public class SelectSingleDialog extends AbstractAlertDialog {

	public interface Callbacks {

		void onSelectSingleDialogResult(int button, Item selectedItem);
	}

	public static final String ARGUMENT_OBJECTS = "objects";
	public static final String ARGUMENT_SELECTED_ID = "selectedId";

	public static final String STATE_SELECTED_INDEX = "selectedIndex";
	protected int selectedIndex;

	public void setObjects(List<? extends Item> objects) {
		getInputArgs().putParcelableArrayList(ARGUMENT_OBJECTS, (ArrayList<? extends Item>) objects);
	}

	public void setSelectedId(int selectedId) {
		getInputArgs().putInt(ARGUMENT_SELECTED_ID, selectedId);
	}

	protected List<Item> getObjects() {
		return getInputArgs().getParcelableArrayList(ARGUMENT_OBJECTS);
	}

	protected String[] initItems() {
		List<Item> objects = getObjects();
		String[] items = new String[objects.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = formatItem(objects.get(i));
		}
		return items;
	}

	protected String formatItem(Item item) {
		return item.getName();
	}

	public int initSelectedIndex() {
		int selectedId = getInputArgs().getInt(ARGUMENT_SELECTED_ID);
		List<Item> objects = getObjects();
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).getId() == selectedId) {
				return i;
			}
		}
		return -1;
	}

	boolean hasSelectedItem() {
		return selectedIndex >= 0;
	}

	protected boolean disablePositiveIfNoSelected() {
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_INDEX, this.selectedIndex);
	}

	protected Callbacks getCallbacks() {
		if (isHostFragment()) {
			return (Callbacks) getTargetFragment();
		} else {
			return (Callbacks) getActivity();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			this.selectedIndex = initSelectedIndex();
		} else {
			this.selectedIndex = savedInstanceState.getInt(STATE_SELECTED_INDEX);
		}
	}

	@Override
	protected void initBuilder(Builder builder, Bundle args) {
		super.initBuilder(builder, args);

		builder.setSingleChoiceItems(initItems(), this.selectedIndex, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				selectedIndex = which;
				getPositiveButton().setEnabled(true);
			}

		}).setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				getCallbacks().onSelectSingleDialogResult(DialogInterface.BUTTON_POSITIVE, getObjects().get(selectedIndex));

			}
		}).setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				getCallbacks().onSelectSingleDialogResult(DialogInterface.BUTTON_NEGATIVE, null);
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		if (disablePositiveIfNoSelected()) {
			getPositiveButton().setEnabled(hasSelectedItem());
		}
	}
}
