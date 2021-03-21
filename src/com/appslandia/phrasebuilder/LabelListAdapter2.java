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

public class LabelListAdapter2 extends ArrayAdapterImpl<Label> {

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

	public LabelListAdapter2(Activity context, int resourceId, List<Label> objects) {
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

			holder.testButton = (ImageView) convertView.findViewById(R.id.label_list_activity_list_item_test_button);
			holder.deleteButton = (ImageView) convertView.findViewById(R.id.label_list_activity_list_item_delete_button);

			holder.testButton.setOnClickListener(onTestClickListener);
			holder.deleteButton.setOnClickListener(onDeleteClickListener);

			holder.labelTextView = (TextView) convertView.findViewById(R.id.label_list_activity_list_item_label_textview);
			holder.phrasesTextView = (TextView) convertView.findViewById(R.id.label_list_activity_list_item_phrases_textview);
			holder.masteredTextView = (TextView) convertView.findViewById(R.id.label_list_activity_list_item_mastered_textview);
			holder.learningTextView = (TextView) convertView.findViewById(R.id.label_list_activity_list_item_learning_textview);

			holder.phrasesQtyTextView = (TextView) convertView.findViewById(R.id.label_list_activity_list_item_phrases_qty_textview);
			holder.masteredQtyTextView = (TextView) convertView.findViewById(R.id.label_list_activity_list_item_mastered_qty_textview);
			holder.learningQtyTextView = (TextView) convertView.findViewById(R.id.label_list_activity_list_item_learning_qty_textview);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Label label = getItem(position);

		// Model to views
		holder.labelTextView.setText(label.name);
		holder.phrasesQtyTextView.setText(Integer.toString(label.phrase_count));
		holder.masteredQtyTextView.setText(Integer.toString(label.mastered_count));
		holder.learningQtyTextView.setText(Integer.toString(label.phrase_count - label.mastered_count));

		// Tags
		holder.testButton.setTag(label);
		holder.deleteButton.setTag(label);

		return convertView;
	}

	static class ViewHolder {
		TextView labelTextView;
		TextView phrasesTextView;
		TextView masteredTextView;
		TextView learningTextView;

		TextView phrasesQtyTextView;
		TextView masteredQtyTextView;
		TextView learningQtyTextView;

		ImageView testButton;
		ImageView deleteButton;
	}
}
