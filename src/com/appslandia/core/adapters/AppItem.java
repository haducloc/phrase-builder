package com.appslandia.core.adapters;

import com.appslandia.core.utils.ToStringBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class AppItem extends AboutItem implements Parcelable {

	public int iconResId;
	public String packageName;

	public AppItem() {
	}

	public AppItem(String name, String desc, String packageName, int iconResId) {
		this.name = name;
		this.desc = desc;
		this.packageName = packageName;
		this.iconResId = iconResId;
	}

	protected AppItem(Parcel in) {
		this.name = in.readString();
		this.desc = in.readString();
		this.packageName = in.readString();
		this.iconResId = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(desc);
		dest.writeString(packageName);
		dest.writeInt(iconResId);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<AppItem> CREATOR = new Parcelable.Creator<AppItem>() {
		@Override
		public AppItem createFromParcel(Parcel in) {
			return new AppItem(in);
		}

		@Override
		public AppItem[] newArray(int size) {
			return new AppItem[size];
		}
	};

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		AppItem other = (AppItem) obj;
		return name.equals(other.name);
	}

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}
}
