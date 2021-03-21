package com.appslandia.phrasebuilder;

import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.phrasebuilder.entities.Language;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LanguageListAdapter extends ArrayAdapterImpl<Language> {

	// Adapter Callback
	public static interface Callbacks {

		void onAdapterDeleteLanguage(Language language);

		void onAdapterTestLanguage(int languageId);
	}

	int resourceId;
	LayoutInflater inflater;

	// Listeners
	final View.OnClickListener onTestClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Language language = (Language) v.getTag();
			((Callbacks) getContext()).onAdapterTestLanguage(language._id);
		}
	};

	final View.OnClickListener onDeleteClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Language language = (Language) v.getTag();
			((Callbacks) getContext()).onAdapterDeleteLanguage(language);
		}
	};

	public LanguageListAdapter(Activity context, int resourceId, List<Language> objects) {
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

			holder.testButton = (ImageView) convertView.findViewById(R.id.language_list_activity_list_item_test_button);
			holder.deleteButton = (ImageView) convertView.findViewById(R.id.language_list_activity_list_item_delete_button);

			holder.testButton.setOnClickListener(onTestClickListener);
			holder.deleteButton.setOnClickListener(onDeleteClickListener);

			holder.languageTextView = (TextView) convertView.findViewById(R.id.language_list_activity_list_item_language_textview);
			holder.phrasesTextView = (TextView) convertView.findViewById(R.id.language_list_activity_list_item_phrases_textview);
			holder.masteredTextView = (TextView) convertView.findViewById(R.id.language_list_activity_list_item_mastered_textview);
			holder.learningTextView = (TextView) convertView.findViewById(R.id.language_list_activity_list_item_learning_textview);

			holder.phrasesQtyTextView = (TextView) convertView.findViewById(R.id.language_list_activity_list_item_phrases_qty_textview);
			holder.masteredQtyTextView = (TextView) convertView.findViewById(R.id.language_list_activity_list_item_mastered_qty_textview);
			holder.learningQtyTextView = (TextView) convertView.findViewById(R.id.language_list_activity_list_item_learning_qty_textview);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Language language = getItem(position);

		// Model to views
		holder.languageTextView.setText(language.name);
		holder.phrasesQtyTextView.setText(Integer.toString(language.phrase_count));
		holder.masteredQtyTextView.setText(Integer.toString(language.mastered_count));
		holder.learningQtyTextView.setText(Integer.toString(language.phrase_count - language.mastered_count));

		// Tags
		holder.testButton.setTag(language);
		holder.deleteButton.setTag(language);

		return convertView;
	}

	static class ViewHolder {
		TextView languageTextView;
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
