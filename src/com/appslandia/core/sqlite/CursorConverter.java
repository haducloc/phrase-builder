package com.appslandia.core.sqlite;

import android.database.Cursor;

public interface CursorConverter<E> {

	E convert(Cursor c);
}
