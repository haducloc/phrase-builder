package com.appslandia.phrasebuilder;

import java.util.ArrayList;

import com.appslandia.core.adapters.SimpleItem;
import com.appslandia.core.views.FirstTimeUsersDialog;
import com.appslandia.core.views.HelpActivity;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

public class FirstTimeUsersDialogImpl extends FirstTimeUsersDialog {

	@Override
	protected ArrayList<SimpleItem> initItems() {

		ArrayList<SimpleItem> list = new ArrayList<SimpleItem>();

		list.add(new SimpleItem(1, getString(R.string.ftu_definitions)));
		list.add(new SimpleItem(2, getString(R.string.ftu_manage_languages)));
		list.add(new SimpleItem(3, getString(R.string.ftu_manage_labels)));

		list.add(new SimpleItem(4, getString(R.string.ftu_create_phrases)));
		list.add(new SimpleItem(5, getString(R.string.ftu_test_phrases)));
		list.add(new SimpleItem(6, getString(R.string.ftu_ask_for_help)));

		return list;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		int itemId = (int) id;

		if (itemId == 1) {
			Intent intent = new Intent(getActivity(), HelpActivityImpl.class);
			intent.putExtra(HelpActivity.INTENT_SECTION_ID, "definitions");
			startActivity(intent);
			return;
		}
		if (itemId == 2) {
			Intent intent = new Intent(getActivity(), LanguageListActivity.class);
			startActivity(intent);
			return;
		}
		if (itemId == 3) {
			Intent intent = new Intent(getActivity(), LabelListActivity.class);
			startActivity(intent);
			return;
		}
		if (itemId == 4) {
			Intent intent = new Intent(getActivity(), PhraseEditActivity.class);
			startActivity(intent);
			return;
		}
		if (itemId == 5) {
			Intent intent = new Intent(getActivity(), PhraseTestInputsActivity.class);
			startActivity(intent);
			return;
		}
		if (itemId == 6) {
			Intent intent = new Intent(getActivity(), HelpActivityImpl.class);
			startActivity(intent);
			return;
		}
	}
}
