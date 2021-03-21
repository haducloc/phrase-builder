package com.appslandia.phrasebuilder;

import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.utils.DateUtils;
import com.appslandia.core.views.LabelLayout;
import com.appslandia.core.views.LayoutSizerImpl;
import com.appslandia.phrasebuilder.entities.Phrase;
import com.appslandia.phrasebuilder.utils.LabelUtils;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PhraseTrashAdapter extends ArrayAdapterImpl<Phrase> {

	// Adapter Callback
	public static interface Callbacks {

		void onAdapterRestorePhrase(Phrase phrase);

		void onAdapterDeleteForeverPhrase(Phrase phrase);
	}

	int resourceId;
	LayoutInflater inflater;
	List<FilterableItem> unlabeledList;

	// Listeners
	final View.OnClickListener restoreButtonClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			((Callbacks) getContext()).onAdapterRestorePhrase((Phrase) v.getTag());
		}
	};

	final View.OnClickListener deleteButtonClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			((Callbacks) getContext()).onAdapterDeleteForeverPhrase((Phrase) v.getTag());
		}
	};

	public PhraseTrashAdapter(Activity context, int resourceId, List<Phrase> objects) {
		super(context, resourceId, objects);

		this.resourceId = resourceId;
		this.inflater = context.getLayoutInflater();
	}

	public List<FilterableItem> getUnlabeledList() {
		if (unlabeledList == null) {
			unlabeledList = LabelUtils.getUnlabeledList();
		}
		return unlabeledList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(resourceId, parent, false);

			// View holder
			holder = new ViewHolder();

			holder.phraseTextView = (TextView) convertView.findViewById(R.id.phrase_trash_activity_list_item_phrasetext_textview);
			holder.labelFlowLayout = (LabelLayout) convertView.findViewById(R.id.phrase_trash_activity_list_item_labellayout);
			holder.notesTextView = (TextView) convertView.findViewById(R.id.phrase_trash_activity_list_item_notes_textview);
			holder.dateDeletedTextView = (TextView) convertView.findViewById(R.id.phrase_trash_activity_list_item_date_deleted_textview);

			holder.restoreButton = (ImageView) convertView.findViewById(R.id.phrase_trash_activity_list_item_restore_button);
			holder.deleteButton = (ImageView) convertView.findViewById(R.id.phrase_trash_activity_list_item_delete_button);

			holder.restoreButton.setOnClickListener(restoreButtonClickListener);
			holder.deleteButton.setOnClickListener(deleteButtonClickListener);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Model to views
		Phrase phrase = getItem(position);

		holder.phraseTextView.setText(PhraseUtils.createPhraseTextSpan(phrase.phrase_text, phrase.key_word));
		if (phrase.notes.isEmpty() == false) {
			holder.notesTextView.setText(phrase.notes);
			holder.notesTextView.setVisibility(View.VISIBLE);
		} else {
			holder.notesTextView.setVisibility(View.GONE);
		}
		holder.dateDeletedTextView.setText(DateUtils.getRelativeDateString(phrase.deleted));

		// Labels
		List<FilterableItem> labelList = phrase.getLabelList();
		if (labelList.isEmpty() == false) {
			holder.labelFlowLayout.setLabelList(labelList);
		} else {
			holder.labelFlowLayout.setLabelList(getUnlabeledList());
		}

		holder.labelFlowLayout.setLayoutSizer(new LayoutSizerImpl(70));
		holder.labelFlowLayout.createLabelViews();

		holder.restoreButton.setTag(phrase);
		holder.deleteButton.setTag(phrase);

		return convertView;
	}

	static class ViewHolder {
		TextView phraseTextView;
		LabelLayout labelFlowLayout;
		TextView notesTextView;
		TextView dateDeletedTextView;

		ImageView restoreButton;
		ImageView deleteButton;
	}
}
