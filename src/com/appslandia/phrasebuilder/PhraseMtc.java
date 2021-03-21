package com.appslandia.phrasebuilder;

import com.appslandia.core.utils.MathUtils;
import com.appslandia.core.utils.ParcelUtils;
import com.appslandia.core.utils.ToStringBuilder;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;

public class PhraseMtc extends PhraseTest {

	public String[] keywordOptions = new String[4];
	public int selectedIndex = -1;
	public int correctIndex;
	public int optionEnabledBitMask = 1;

	private Spannable[] phraseSpannables;

	public PhraseMtc() {
	}

	private PhraseMtc(Parcel in) {
		this._id = in.readInt();
		this.phrase_text = in.readString();
		this.key_word = in.readString();
		this.labels = ParcelUtils.readString(in);
		this.notes = in.readString();
		this.mastered = ParcelUtils.readBool(in);

		in.readStringArray(this.keywordOptions);
		this.selectedIndex = in.readInt();
		this.correctIndex = in.readInt();
		this.optionEnabledBitMask = in.readInt();
	}

	// index: {0,1,2,3}
	public Spannable getPhraseSpannable(int index) {
		if (phraseSpannables == null) {
			phraseSpannables = new Spannable[4];
		}
		Spannable span = phraseSpannables[index];
		if (span == null) {
			span = PhraseUtils.createPhraseTextSpan(getPhraseSegs(), keywordOptions[index], (this.correctIndex == index));
		}
		return span;
	}

	public boolean isKeywordAvailable(int index) {
		int indexFlag = MathUtils.pow(2, index);
		return (this.optionEnabledBitMask & indexFlag) == indexFlag;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._id);
		dest.writeString(this.phrase_text);
		dest.writeString(this.key_word);
		ParcelUtils.writeString(dest, this.labels);
		dest.writeString(this.notes);
		ParcelUtils.writeBool(dest, this.mastered);

		dest.writeStringArray(keywordOptions);
		dest.writeInt(this.selectedIndex);
		dest.writeInt(this.correctIndex);
		dest.writeInt(this.optionEnabledBitMask);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<PhraseMtc> CREATOR = new Parcelable.Creator<PhraseMtc>() {
		@Override
		public PhraseMtc createFromParcel(Parcel in) {
			return new PhraseMtc(in);
		}

		@Override
		public PhraseMtc[] newArray(int size) {
			return new PhraseMtc[size];
		}
	};

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}
}
