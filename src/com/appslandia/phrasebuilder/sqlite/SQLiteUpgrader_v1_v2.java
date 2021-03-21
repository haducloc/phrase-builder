package com.appslandia.phrasebuilder.sqlite;

import com.appslandia.core.sqlite.SQLiteUpgrader;

import android.database.sqlite.SQLiteDatabase;

public class SQLiteUpgrader_v1_v2 implements SQLiteUpgrader {

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.beginTransaction();
		try {

			// Modify language
			db.execSQL("ALTER TABLE language RENAME TO language_old");
			db.execSQL(DbManager.create_table_language_sql);
			db.execSQL("INSERT INTO language SELECT _id,name,lang_pos FROM language_old");
			db.execSQL("DROP TABLE language_old");

			// Modify label
			db.execSQL("ALTER TABLE label RENAME TO label_old");
			db.execSQL(DbManager.create_table_label_sql);
			db.execSQL("INSERT INTO label SELECT _id,name,s_name FROM label_old");
			db.execSQL("DROP TABLE label_old");

			// Update label
			db.execSQL("UPDATE label SET name='at-work',s_name='at-work' WHERE name='work'");

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
}
