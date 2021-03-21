package com.appslandia.core.utils;

import java.util.Date;

import android.os.Parcel;

public class ParcelUtils {

	public static final byte BYTE_0 = 0;
	public static final byte BYTE_1 = 1;

	public static void writeInteger(Parcel dest, Integer value) {
		dest.writeInt(value == (null) ? (0) : value.intValue());
	}

	public static Integer readInteger(Parcel in) {
		int value = in.readInt();
		return value == (0) ? (null) : Integer.valueOf(value);
	}

	public static void writeLong(Parcel dest, Long value) {
		dest.writeLong(value == (null) ? (0) : value.longValue());
	}

	public static Long readLong(Parcel in) {
		long value = in.readLong();
		return value == (0) ? (null) : Long.valueOf(value);
	}

	public static void writeString(Parcel dest, String value) {
		if (value == (null)) {
			dest.writeByte(BYTE_0);
		} else {
			dest.writeByte(BYTE_1);
			dest.writeString(value);
		}
	}

	public static String readString(Parcel in) {
		byte b = in.readByte();
		if (b == BYTE_0) {
			return (null);
		}
		return in.readString();
	}

	public static void writeDate(Parcel dest, Date value) {
		dest.writeLong(value == (null) ? (0) : value.getTime());
	}

	public static Date readDate(Parcel in) {
		long value = in.readLong();
		return value == (0) ? (null) : new Date(value);
	}

	public static void writeBool(Parcel dest, boolean value) {
		dest.writeByte(value ? BYTE_1 : BYTE_0);
	}

	public static boolean readBool(Parcel in) {
		return (in.readByte() != BYTE_0);
	}
}
