package com.appslandia.core.sqlite;

import android.database.sqlite.SQLiteDatabase;

public interface SQLiteUpgrader {

	void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
