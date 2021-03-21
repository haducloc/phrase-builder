package com.appslandia.core.views;

import android.os.Parcel;
import android.os.Parcelable;

public class ListViewPos implements Parcelable {

	private int position;
	private int topY;

	public ListViewPos(int position, int topY) {
		this.position = position;
		this.topY = topY;
	}

	private ListViewPos(Parcel in) {
		this.position = in.readInt();
		this.topY = in.readInt();
	}

	public int getPosition() {
		return position;
	}

	public int getTopY() {
		return topY;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.position);
		dest.writeInt(this.topY);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<ListViewPos> CREATOR = new Parcelable.Creator<ListViewPos>() {
		@Override
		public ListViewPos createFromParcel(Parcel in) {
			return new ListViewPos(in);
		}

		@Override
		public ListViewPos[] newArray(int size) {
			return new ListViewPos[size];
		}
	};
}
