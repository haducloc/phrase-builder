package com.appslandia.core.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public final class SQLiteUtils {

	public static <E> List<E> query(SQLiteDatabase db, String query, String[] selectionArgs, CursorConverter<E> converter) {
		Cursor c = db.rawQuery(query, selectionArgs);
		List<E> list = new ArrayList<E>();
		if (c.moveToFirst()) {
			while (c.isAfterLast() == false) {
				list.add(converter.convert(c));

				c.moveToNext();
			}

		}
		c.close();
		return list;
	}

	public static void bindNullableString(SQLiteStatement stat, int index, String value) {
		if (value == null) {
			stat.bindNull(index);
		} else {
			stat.bindString(index, value);
		}
	}

	public static void bindBoolean(SQLiteStatement stat, int index, boolean value) {
		stat.bindLong(index, value ? 1 : 0);
	}

	public static boolean readBoolean(Cursor c, int index) {
		return c.getShort(index) == 0 ? false : true;
	}
}
