package com.appslandia.phrasebuilder;

import java.util.ArrayList;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.core.views.DialogImpl;
import com.appslandia.core.views.FormDialog;
import com.appslandia.core.views.OnListItemClickListener;
import com.appslandia.phrasebuilder.entities.Label;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LabelSelectDialog extends FormDialog {

	public static final String DIALOG_LABELS = "labels";

	// Dialog Callback
	public interface Callbacks {

		void onSelectLabel(Label label);
	}

	ArrayList<Label> getLabels() {
		return getArguments().getParcelableArrayList(DIALOG_LABELS);
	}

	Callbacks getCallbacks() {
		return (Callbacks) getTargetFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View viewRoot = inflater.inflate(R.layout.label_select_dialog, container, false);
		ListView list = (ListView) viewRoot.findViewById(R.id.libs_list_fragment_list_view);

		ArrayAdapterImpl<Label> adapter = new ArrayAdapterImpl<Label>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, getLabels());
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnListItemClickListener() {

			@Override
			protected void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
				Label label = getLabels().get(position);
				getCallbacks().onSelectLabel(label);

				DialogImpl.dismiss(getFragmentManager(), LabelSelectDialog.class);
			}
		});
		return viewRoot;
	}

	@Override
	protected void initRightAction(ImageView actionView) {
	}

	@Override
	protected void initTitleView(TextView titleTextView) {
		titleTextView.setText(R.string.title_search_by_label);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
