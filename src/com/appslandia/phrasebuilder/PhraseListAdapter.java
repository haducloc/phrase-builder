package com.appslandia.phrasebuilder;

import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.utils.DateUtils;
import com.appslandia.core.views.LabelLayout;
import com.appslandia.core.views.LayoutSizerImpl;
import com.appslandia.core.views.LeoToggleView;
import com.appslandia.phrasebuilder.entities.Phrase;
import com.appslandia.phrasebuilder.utils.LabelUtils;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PhraseListAdapter extends ArrayAdapterImpl<Phrase> {

	// Adapter Callback
	public static interface Callbacks {

		void onAdapterDeletePhrase(Phrase phrase);

		void onAdapterUpdateMastery(Phrase phrase, boolean mastered);

		void onAdapterClickLabel(FilterableItem label);
	}

	int resourceId;
	LayoutInflater inflater;
	List<FilterableItem> unlabeledList;

	// Listeners
	final View.OnClickListener onLabelClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			((Callbacks) getContext()).onAdapterClickLabel((FilterableItem) v.getTag());
		}
	};

	final View.OnClickListener deleteButtonClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			((Callbacks) getContext()).onAdapterDeletePhrase((Phrase) v.getTag());
		}
	};

	final LeoToggleView.OnToggleListener masteryToggleListener = new LeoToggleView.OnToggleListener() {

		@Override
		public void onToggle(View view, boolean isOn) {
			((Callbacks) getContext()).onAdapterUpdateMastery((Phrase) view.getTag(), isOn);
		}
	};

	public PhraseListAdapter(Activity context, int resourceId, List<Phrase> objects) {
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

	static final Phrase EMPTY_PHRASE = new Phrase();

	static {
		EMPTY_PHRASE.phrase_text = "This is a sample phrase";
		EMPTY_PHRASE.key_word = "sample";
		EMPTY_PHRASE.s_keyword = "sample";
		EMPTY_PHRASE.notes = "N/A";
	}

	@Override
	public Phrase getItem(int position) {
		if (position < mObjects.size()) {
			return mObjects.get(position);
		} else {
			return EMPTY_PHRASE;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(resourceId, parent, false);

			// View holder
			holder = new ViewHolder();

			holder.phraseTextView = (TextView) convertView.findViewById(R.id.phrase_list_activity_list_view_phrasetext_textview);
			holder.labelLayout = (LabelLayout) convertView.findViewById(R.id.phrase_list_activity_list_view_labellayout);
			holder.notesTextView = (TextView) convertView.findViewById(R.id.phrase_list_activity_list_view_notes_textview);
			holder.lastUpdatedTextView = (TextView) convertView.findViewById(R.id.phrase_list_activity_list_view_lastupdated_textview);

			holder.masteryToggleButton = (LeoToggleView) convertView.findViewById(R.id.phrase_list_activity_list_view_mastery_button);
			holder.deleteButton = convertView.findViewById(R.id.phrase_list_activity_list_view_delete_button);

			holder.masteryToggleButton.setToggleListener(masteryToggleListener);
			holder.labelLayout.setOnLabelClickListener(onLabelClickListener);
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

		holder.lastUpdatedTextView.setText(DateUtils.getRelativeDateString(phrase.last_updated));
		holder.masteryToggleButton.setStateOn(phrase.mastered);

		// Labels
		List<FilterableItem> labelList = phrase.getLabelList();
		if (labelList.isEmpty() == false) {
			holder.labelLayout.setLabelList(labelList);
		} else {
			holder.labelLayout.setLabelList(getUnlabeledList());
		}
		holder.labelLayout.setLayoutSizer(new LayoutSizerImpl(70));
		holder.labelLayout.createLabelViews();

		// Tags
		holder.masteryToggleButton.setTag(phrase);
		holder.deleteButton.setTag(phrase);

		return convertView;
	}

	static class ViewHolder {
		TextView phraseTextView;
		LabelLayout labelLayout;
		TextView notesTextView;
		TextView lastUpdatedTextView;

		LeoToggleView masteryToggleButton;
		View deleteButton;
	}
}
