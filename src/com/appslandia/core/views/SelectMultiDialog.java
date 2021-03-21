package com.appslandia.core.views;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.Item;
import com.appslandia.phrasebuilder.R;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;

public class SelectMultiDialog extends AbstractAlertDialog {

	public interface Callbacks {

		void onSelectMultiDialogResult(int button, List<Item> objects, boolean[] selectedItems);
	}

	public static final String ARGUMENT_OBJECTS = "objects";
	public static final String ARGUMENT_SELECTED_IDS = "selectedIds";

	public static final String STATE_SELECTED_ITEMS = "selectedItems";
	protected boolean[] selectedItems;

	public void setObjects(List<? extends Item> objects) {
		getInputArgs().putParcelableArrayList(ARGUMENT_OBJECTS, (ArrayList<? extends Item>) objects);
	}

	public void setSelectedIds(int[] selectedIds) {
		getInputArgs().putIntArray(ARGUMENT_SELECTED_IDS, selectedIds);
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

	protected boolean[] initSelectedItems() {
		List<Item> objects = getObjects();
		boolean[] items = new boolean[objects.size()];
		int[] selectedIds = getInputArgs().getIntArray(ARGUMENT_SELECTED_IDS);
		if (selectedIds != null) {
			for (int i = 0; i < items.length; i++) {
				for (int id : selectedIds) {
					if (objects.get(i).getId() == id) {
						items[i] = true;
						break;
					}
				}
			}
		}
		return items;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBooleanArray(STATE_SELECTED_ITEMS, this.selectedItems);
	}

	boolean hasSelectedItems() {
		for (boolean selected : this.selectedItems) {
			if (selected) {
				return true;
			}
		}
		return false;
	}

	protected boolean disablePositiveIfNoSelected() {
		return true;
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
			this.selectedItems = this.initSelectedItems();
		} else {
			this.selectedItems = savedInstanceState.getBooleanArray(STATE_SELECTED_ITEMS);
		}
	}

	@Override
	protected void initBuilder(Builder builder, Bundle args) {
		super.initBuilder(builder, args);

		builder.setMultiChoiceItems(initItems(), this.selectedItems, new DialogInterface.OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				selectedItems[which] = isChecked;

				if (disablePositiveIfNoSelected()) {
					getPositiveButton().setEnabled(hasSelectedItems());
				}
			}

		}).setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				getCallbacks().onSelectMultiDialogResult(DialogInterface.BUTTON_POSITIVE, getObjects(), selectedItems);
			}
		}).setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int id) {
				getCallbacks().onSelectMultiDialogResult(DialogInterface.BUTTON_NEGATIVE, null, null);
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		if (disablePositiveIfNoSelected()) {
			getPositiveButton().setEnabled(hasSelectedItems());
		}
	}
}
