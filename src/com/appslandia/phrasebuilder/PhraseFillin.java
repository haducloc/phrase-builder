package com.appslandia.phrasebuilder;

import com.appslandia.core.utils.ParcelUtils;
import com.appslandia.core.utils.ToStringBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class PhraseFillin extends PhraseTest {

	public String answered;

	public PhraseFillin() {
	}

	private PhraseFillin(Parcel in) {
		this._id = in.readInt();
		this.phrase_text = in.readString();
		this.key_word = in.readString();
		this.labels = ParcelUtils.readString(in);
		this.notes = in.readString();
		this.mastered = ParcelUtils.readBool(in);

		this.answered = ParcelUtils.readString(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._id);
		dest.writeString(this.phrase_text);
		dest.writeString(this.key_word);
		ParcelUtils.writeString(dest, this.labels);
		dest.writeString(this.notes);
		ParcelUtils.writeBool(dest, this.mastered);

		ParcelUtils.writeString(dest, this.answered);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<PhraseFillin> CREATOR = new Parcelable.Creator<PhraseFillin>() {
		@Override
		public PhraseFillin createFromParcel(Parcel in) {
			return new PhraseFillin(in);
		}

		@Override
		public PhraseFillin[] newArray(int size) {
			return new PhraseFillin[size];
		}
	};

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}
}
