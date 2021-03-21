package com.appslandia.core.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class WeekDate implements Parcelable {

	public int dateM;
	public int day;
	public String daySn;

	public WeekDate(int dateM, int day, String daySn) {
		this.dateM = dateM;
		this.day = day;
		this.daySn = daySn;
	}

	protected WeekDate(Parcel in) {
		this.dateM = in.readInt();
		this.day = in.readInt();
		this.daySn = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.dateM);
		dest.writeInt(this.day);
		dest.writeString(this.daySn);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<WeekDate> CREATOR = new Parcelable.Creator<WeekDate>() {
		@Override
		public WeekDate createFromParcel(Parcel in) {
			return new WeekDate(in);
		}

		@Override
		public WeekDate[] newArray(int size) {
			return new WeekDate[size];
		}
	};

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		WeekDate other = (WeekDate) obj;
		return dateM == other.dateM;
	}

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}

}
