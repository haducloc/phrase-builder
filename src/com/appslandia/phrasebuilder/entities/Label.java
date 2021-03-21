package com.appslandia.phrasebuilder.entities;

import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.utils.ToStringBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class Label implements FilterableItem, Parcelable {

	public int _id;
	public String name;
	public String s_name;

	public int phrase_count;
	public int mastered_count;

	public Label() {
	}

	public Label(String name, String s_name) {
		this.name = name;
		this.s_name = s_name;
	}

	private Label(Parcel in) {
		this._id = in.readInt();
		this.name = in.readString();
		this.s_name = in.readString();

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
	public String getFilterName() {
		return s_name;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._id);
		dest.writeString(this.name);
		dest.writeString(this.s_name);

		dest.writeInt(this.phrase_count);
		dest.writeInt(this.mastered_count);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Label> CREATOR = new Parcelable.Creator<Label>() {
		@Override
		public Label createFromParcel(Parcel in) {
			return new Label(in);
		}

		@Override
		public Label[] newArray(int size) {
			return new Label[size];
		}
	};

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		Label other = (Label) obj;
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
