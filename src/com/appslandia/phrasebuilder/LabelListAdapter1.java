package com.appslandia.phrasebuilder;

import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.phrasebuilder.entities.Label;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LabelListAdapter1 extends ArrayAdapterImpl<Label> {

	int resourceId;
	LayoutInflater inflater;

	final View.OnClickListener onTestClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Label label = (Label) v.getTag();
			((LabelListItemCallbacks) getContext()).onAdapterTestLabel(label);
		}
	};

	final View.OnClickListener onDeleteClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Label label = (Label) v.getTag();
			((LabelListItemCallbacks) getContext()).onAdapterDeleteLabel(label);
		}
	};

	public LabelListAdapter1(Activity context, int resourceId, List<Label> objects) {
		super(context, resourceId, objects);

		this.resourceId = resourceId;
		this.inflater = context.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(resourceId, parent, false);

			// View holder
			holder = new ViewHolder();
			holder.labelTextView = (TextView) convertView.findViewById(R.id.label_list_activity_list_item_label_textview);

			holder.testButton = (ImageView) convertView.findViewById(R.id.label_list_activity_list_item_test_button);
			holder.deleteButton = (ImageView) convertView.findViewById(R.id.label_list_activity_list_item_delete_button);

			holder.testButton.setOnClickListener(onTestClickListener);
			holder.deleteButton.setOnClickListener(onDeleteClickListener);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Model to views
		Label label = getItem(position);
		holder.labelTextView.setText(label.name);

		// Tags
		holder.testButton.setTag(label);
		holder.deleteButton.setTag(label);

		return convertView;
	}

	static class ViewHolder {
		TextView labelTextView;
		ImageView testButton;
		ImageView deleteButton;
	}
}
