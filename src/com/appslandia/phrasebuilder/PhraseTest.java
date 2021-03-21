package com.appslandia.phrasebuilder;

import java.util.List;

import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.utils.ToStringBuilder;
import com.appslandia.phrasebuilder.utils.LabelUtils;
import com.appslandia.phrasebuilder.utils.PhraseSegs;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.os.Parcelable;

public abstract class PhraseTest implements Parcelable {

	public int _id;
	public String phrase_text;
	public String key_word;
	public String labels;
	public String notes;
	public boolean mastered;

	private List<FilterableItem> labelList = null;
	private PhraseSegs phraseSegs;

	public List<FilterableItem> getLabelList() {
		if (labelList == null) {
			labelList = LabelUtils.toLabelList(labels);
		}
		return labelList;
	}

	public PhraseSegs getPhraseSegs() {
		if (phraseSegs == null) {
			phraseSegs = PhraseUtils.parsePhraseSegs(this.phrase_text, this.key_word);
		}
		return phraseSegs;
	}

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}
}
