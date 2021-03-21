package com.appslandia.core.views;

import java.util.ArrayList;

import com.appslandia.core.adapters.SimpleItem;
import com.appslandia.phrasebuilder.R;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

public abstract class FirstTimeUsersDialog extends DialogImpl {

	// Callback
	public interface Callbacks {

		void onFirstTimeUsersClosed();
	}

	public FirstTimeUsersDialog() {
	}

	protected ListView listView;
	final private OnListItemClickListener mOnClickListener = new OnListItemClickListener() {

		@Override
		protected void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
			onListItemClick((ListView) parent, view, position, id);
		}
	};

	public void onListItemClick(ListView l, View v, int position, long id) {
	}

	protected abstract ArrayList<SimpleItem> initItems();

	@Override
	public void onDestroyView() {
		mOnClickListener.removeCallbackOn(listView);
		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View viewRoot = inflater.inflate(R.layout.libs_first_time_users_dialog, container, false);

		// ListView
		listView = (ListView) viewRoot.findViewById(R.id.libs_first_time_users_dialog_reviews_listview);
		listView.setOnItemClickListener(mOnClickListener);

		listView.setDivider(null);

		// Adapter
		FirstTimeUsersAdapter<SimpleItem> adapter = new FirstTimeUsersAdapter<SimpleItem>(getActivity(), R.layout.libs_first_time_users_list_item, initItems());
		listView.setAdapter(adapter);

		// okBtn
		View okBtn = viewRoot.findViewById(R.id.libs_first_time_users_ok_button);
		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FirstTimeUsersDialog.this.dismissAllowingStateLoss();
				((Callbacks) getActivity()).onFirstTimeUsersClosed();
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
