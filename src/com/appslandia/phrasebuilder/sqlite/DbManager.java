package com.appslandia.phrasebuilder.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbManager extends SQLiteOpenHelper {

	private static volatile DbManager instance;
	public static final Object MUTEX = new Object();

	private static final String DB_NAME = "PhraseBuilder.db";

	// List database versions
	public static final int DB_VERSION_V1 = 1;
	public static final int DB_VERSION_V2 = 2;

	public static int CURRENT_DB_VERSION() {
		return DB_VERSION_V2;
	}

	public static DbManager getInstance(Context context) {
		if (instance == null) {
			synchronized (MUTEX) {
				if (instance == null) {
					instance = new DbManager(context.getApplicationContext());
				}
			}
		}
		return instance;
	}

	final boolean initialize;

	public DbManager(Context context) {
		this(context, true);
	}

	public DbManager(Context context, boolean initialize) {
		super(context, DB_NAME, null, CURRENT_DB_VERSION());
		this.initialize = initialize;
	}

	public static SQLiteDatabase openRead(Context context) {
		return getInstance(context).getReadableDatabase();
	}

	public static SQLiteDatabase openWrite(Context context) {
		return getInstance(context).getWritableDatabase();
	}

	// @formatter:off
	public static final String create_table_language_sql = 
				"CREATE TABLE language (" 
					+ " _id INTEGER PRIMARY KEY AUTOINCREMENT" 
					+ " ,name TEXT NOT NULL" 
					+ " ,lang_pos INTEGER NOT NULL" 
					+ " );";
	// @formatter:on

	// @formatter:off
	public static final String create_table_label_sql = 
				"CREATE TABLE label (" 
					+ " _id INTEGER PRIMARY KEY AUTOINCREMENT" 
					+ " ,name TEXT NOT NULL" 
					+ " ,s_name TEXT NOT NULL"
					+ " );";
	// @formatter:on

	// @formatter:off
	public static final String create_table_phrase_sql 
			= "CREATE TABLE phrase (" 
				+ " _id INTEGER PRIMARY KEY AUTOINCREMENT" 
				+ " ,phrase_text TEXT NOT NULL" 
				+ " ,key_word TEXT NOT NULL" 
				+ " ,s_keyword TEXT NOT NULL" 
				+ " ,notes TEXT NOT NULL"
				+ " ,language_id INTEGER NOT NULL" 
				+ " ,mastered INTEGER NOT NULL" 
				+ " ,last_updated INTEGER NOT NULL" 
				+ " ,deleted INTEGER NOT NULL"
				+ " ,bundle_id INTEGER NOT NULL"
				+ " )";
	// @formatter:on

	// @formatter:off
	public static final String create_table_phrase_label_sql = 
			"CREATE TABLE phrase_label (" 
				+ " phrase_id INTEGER NOT NULL" 
				+ " ,label_id INTEGER NOT NULL" 
				+ " ,PRIMARY KEY (phrase_id, label_id)" 
				+ " );";

	// @formatter:on

	// Delete SQL
	public static final String delete_table_language_sql = "DELETE FROM language";
	public static final String delete_table_label_sql = "DELETE FROM label";
	public static final String delete_table_phrase_sql = "DELETE FROM phrase";
	public static final String delete_table_phrase_label_sql = "DELETE FROM phrase_label";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(create_table_language_sql);
		db.execSQL(create_table_label_sql);
		db.execSQL(create_table_phrase_sql);
		db.execSQL(create_table_phrase_label_sql);

		if (this.initialize) {
			initSampleData(db);
		}
	}

	protected void initSampleData(SQLiteDatabase db) {
		DbInitializer.initialize(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == DB_VERSION_V1) {
			new SQLiteUpgrader_v1_v2().onUpgrade(db, oldVersion, newVersion);
		}
	}
}
