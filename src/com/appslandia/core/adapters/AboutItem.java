package com.appslandia.core.adapters;

import com.appslandia.core.utils.ParcelUtils;
import com.appslandia.core.utils.ToStringBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class AboutItem extends SimpleItem implements Parcelable {

	public String desc;

	public AboutItem() {
	}

	public AboutItem(int id, String name) {
		super(id, name);
	}

	public AboutItem(int id, String name, String desc) {
		super(id, name);
		this.desc = desc;
	}

	protected AboutItem(Parcel in) {
		super(in);
		this.desc = ParcelUtils.readString(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);

		ParcelUtils.writeString(dest, desc);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<AboutItem> CREATOR = new Parcelable.Creator<AboutItem>() {
		@Override
		public AboutItem createFromParcel(Parcel in) {
			return new AboutItem(in);
		}

		@Override
		public AboutItem[] newArray(int size) {
			return new AboutItem[size];
		}
	};

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		AboutItem other = (AboutItem) obj;
		return _id == other._id;
	}

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}
}
