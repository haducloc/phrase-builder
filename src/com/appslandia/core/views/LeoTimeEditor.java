package com.appslandia.core.views;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

public class LeoTimeEditor extends LeoEditTextView {

	private int hour;
	private int minute;

	private final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

	public LeoTimeEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LeoTimeEditor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setHourMinute(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;

		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);

		setText(timeFormat.format(cal.getTime()));
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	@Override
	public Parcelable onSaveInstanceState() {
		return new EditorState(hour, minute, super.onSaveInstanceState());
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof EditorState) {
			EditorState editorState = (EditorState) state;

			hour = editorState.hour;
			minute = editorState.minute;
			super.onRestoreInstanceState(editorState.parentState);
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	private static class EditorState implements Parcelable {

		private int hour;
		private int minute;
		private Parcelable parentState;

		public EditorState(int hour, int minute, Parcelable parentState) {
			this.hour = hour;
			this.minute = minute;
			this.parentState = parentState;
		}

		protected EditorState(Parcel in) {
			this.hour = in.readInt();
			this.minute = in.readInt();
			this.parentState = in.readParcelable(null);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(this.hour);
			dest.writeInt(this.minute);
			dest.writeParcelable(this.parentState, 0);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<EditorState> CREATOR = new Parcelable.Creator<EditorState>() {
			@Override
			public EditorState createFromParcel(Parcel in) {
				return new EditorState(in);
			}

			@Override
			public EditorState[] newArray(int size) {
				return new EditorState[size];
			}
		};
	}
}
