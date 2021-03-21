package com.appslandia.phrasebuilder.entities;

import java.util.List;

import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.utils.ParcelUtils;
import com.appslandia.core.utils.ToStringBuilder;
import com.appslandia.phrasebuilder.utils.LabelUtils;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;

public class Phrase implements Parcelable {

	public int _id;
	public String phrase_text;
	public String key_word;
	public String s_keyword;
	public String notes;

	public int language_id;
	public boolean mastered;
	public long last_updated;
	public long deleted;
	public int bundle_id;

	public long mem_just_updated;

	public String labels;
	private List<FilterableItem> labelList = null;
	private Spannable phraseTextSpan;

	public Phrase() {
	}

	private Phrase(Parcel in) {
		this._id = in.readInt();
		this.phrase_text = in.readString();
		this.key_word = in.readString();
		// this.s_keyword = in.readString();
		this.notes = in.readString();
		this.language_id = in.readInt();

		this.mastered = ParcelUtils.readBool(in);
		this.last_updated = in.readLong();
		this.deleted = in.readLong();
		this.bundle_id = in.readInt();

		this.mem_just_updated = in.readLong();
		this.labels = in.readString();
	}

	public List<FilterableItem> getLabelList() {
		if (labelList == null) {
			labelList = LabelUtils.toLabelList(labels);
		}
		return labelList;
	}

	public void setLabelList(List<FilterableItem> labelList) {
		this.labelList = labelList;
	}

	public Spannable getPhraseTextSpan() {
		if (phraseTextSpan == null) {
			phraseTextSpan = PhraseUtils.createPhraseTextSpan(phrase_text, key_word);
		}
		return phraseTextSpan;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._id);
		dest.writeString(this.phrase_text);
		dest.writeString(this.key_word);
		// dest.writeString( this.s_keyword);
		dest.writeString(this.notes);
		dest.writeInt(this.language_id);

		ParcelUtils.writeBool(dest, this.mastered);
		dest.writeLong(this.last_updated);
		dest.writeLong(this.deleted);
		dest.writeInt(this.bundle_id);

		dest.writeLong(this.mem_just_updated);
		dest.writeString(this.labels);
	}

	public static final Parcelable.Creator<Phrase> CREATOR = new Parcelable.Creator<Phrase>() {
		@Override
		public Phrase createFromParcel(Parcel in) {
			return new Phrase(in);
		}

		@Override
		public Phrase[] newArray(int size) {
			return new Phrase[size];
		}
	};

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		Phrase other = (Phrase) obj;
		return _id == other._id;
	}

	@Override
	public int hashCode() {
		return _id;
	}

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}
}
