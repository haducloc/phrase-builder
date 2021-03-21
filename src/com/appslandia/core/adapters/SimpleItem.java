package com.appslandia.core.adapters;

import com.appslandia.core.utils.ToStringBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleItem implements Item {

	public int _id;
	public String name;

	public SimpleItem() {
	}

	public SimpleItem(int id, String name) {
		this._id = id;
		this.name = name;
	}

	protected SimpleItem(Parcel in) {
		this._id = in.readInt();
		this.name = in.readString();
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
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<SimpleItem> CREATOR = new Parcelable.Creator<SimpleItem>() {
		@Override
		public SimpleItem createFromParcel(Parcel in) {
			return new SimpleItem(in);
		}

		@Override
		public SimpleItem[] newArray(int size) {
			return new SimpleItem[size];
		}
	};

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		SimpleItem other = (SimpleItem) obj;
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
