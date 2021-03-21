package com.appslandia.core.adapters;

import com.appslandia.core.utils.ToStringBuilder;
import com.appslandia.phrasebuilder.R;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class LabelItem extends SimpleItem implements FilterableItem {

	public String s_name;

	public LabelItem() {
	}

	public LabelItem(int id, String name, String s_name) {
		super(id, name);
		this.s_name = s_name;
	}

	protected LabelItem(Parcel in) {
		super(in);
		this.s_name = in.readString();
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
		super.writeToParcel(dest, flags);

		dest.writeString(this.s_name);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<LabelItem> CREATOR = new Parcelable.Creator<LabelItem>() {
		@Override
		public LabelItem createFromParcel(Parcel in) {
			return new LabelItem(in);
		}

		@Override
		public LabelItem[] newArray(int size) {
			return new LabelItem[size];
		}
	};

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		LabelItem other = (LabelItem) obj;
		return _id == other._id;
	}

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}

	// LABEL NOT FOUND
	public static final int LABEL_ID_NOT_FOUND = (0);

	public static LabelItem getLabelNotFound(Context context) {
		String notFound = context.getString(R.string.message_label_not_found);
		return new LabelItem(LABEL_ID_NOT_FOUND, notFound, notFound);
	}
}
