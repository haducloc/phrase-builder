package com.appslandia.phrasebuilder.entities;

import com.appslandia.core.adapters.Item;
import com.appslandia.core.utils.ToStringBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class Language implements Item {

	public int _id;
	public String name;
	public int lang_pos;

	public int phrase_count;
	public int mastered_count;

	public Language() {
	}

	public Language(int id, String name) {
		this._id = id;
		this.name = name;
	}

	private Language(Parcel in) {
		this._id = in.readInt();
		this.name = in.readString();
		this.lang_pos = in.readInt();

		this.phrase_count = in.readInt();
		this.mastered_count = in.readInt();
	}

	@Override
	public int getId() {
		return _id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._id);
		dest.writeString(this.name);
		dest.writeInt(this.lang_pos);

		dest.writeInt(this.phrase_count);
		dest.writeInt(this.mastered_count);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Language> CREATOR = new Parcelable.Creator<Language>() {
		@Override
		public Language createFromParcel(Parcel in) {
			return new Language(in);
		}

		@Override
		public Language[] newArray(int size) {
			return new Language[size];
		}
	};

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		Language other = (Language) obj;
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
