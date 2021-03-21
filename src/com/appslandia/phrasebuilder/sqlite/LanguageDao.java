package com.appslandia.phrasebuilder.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.utils.BackupUtils;
import com.appslandia.phrasebuilder.entities.Language;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public final class LanguageDao {

	public static final String create_sql = "INSERT INTO language (name,lang_pos) VALUES (?,?)";

	public static void insert(SQLiteDatabase db, Language language) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(create_sql);
			stat.bindString(1, language.name);
			stat.bindLong(2, language.lang_pos);

			try {
				int languageId = (int) stat.executeInsert();
				language._id = languageId;
			} finally {
				stat.close();
			}
		}
	}

	public static final String create_restore_sql = "INSERT INTO language (_id,name,lang_pos) VALUES (?,?,?)";

	public static void insertRestore(SQLiteDatabase db, Language language) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(create_restore_sql);
			stat.bindLong(1, language._id);
			stat.bindString(2, language.name);
			stat.bindLong(3, language.lang_pos);

			try {
				stat.executeInsert();
			} finally {
				stat.close();
			}
		}
	}

	public static final String update_sql = "UPDATE language SET name=? WHERE _id=?";

	public static int update(SQLiteDatabase db, Language language) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(update_sql);
			stat.bindString(1, language.name);
			stat.bindLong(2, language._id);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	public static final String delete_sql = "DELETE FROM language WHERE _id=?";

	public static int delete(SQLiteDatabase db, int languageId) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(delete_sql);
			stat.bindLong(1, languageId);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	public static final String query_languages_sql = "SELECT _id,name FROM language ORDER BY lang_pos,name";

	public static List<Language> queryLanguages(SQLiteDatabase db) {
		Cursor c = db.rawQuery(query_languages_sql, null);
		List<Language> list = new ArrayList<Language>();
		if (c.moveToFirst()) {
			while (c.isAfterLast() == false) {
				Language obj = new Language();
				obj._id = c.getInt(0);
				obj.name = c.getString(1);

				list.add(obj);
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}

	// @formatter:off
	public static final String query_stat_sql = 
			"SELECT l._id,l.name" 
				+ ",(SELECT COUNT(1) FROM phrase p WHERE p.language_id=l._id AND p.deleted=0) AS phrase_count" 
				+ ",(SELECT COUNT(1) FROM phrase p WHERE p.language_id=l._id AND p.deleted=0 AND p.mastered=1) AS mastered_count"
				+ " FROM language AS l ORDER BY l.lang_pos,l.name";
	// @formatter:on

	public static List<Language> queryStat(SQLiteDatabase db) {
		Cursor c = db.rawQuery(query_stat_sql, null);
		List<Language> list = new ArrayList<Language>();
		if (c.moveToFirst()) {
			while (c.isAfterLast() == false) {
				Language obj = new Language();
				obj._id = c.getInt(0);
				obj.name = c.getString(1);
				obj.phrase_count = c.getInt(2);
				obj.mastered_count = c.getInt(3);

				list.add(obj);

				c.moveToNext();
			}
		}
		c.close();
		return list;
	}

	public static final String check_language_sql_new = "SELECT COUNT(1) FROM language WHERE name=?";
	public static final String check_language_sql_update = "SELECT COUNT(1) FROM language WHERE name=? AND _id!=?";

	public static boolean checkLanguage(SQLiteDatabase db, String name, Integer languageId) {
		SQLiteStatement stat = null;
		if (languageId == null) {
			stat = db.compileStatement(check_language_sql_new);
			stat.bindString(1, name);
		} else {
			stat = db.compileStatement(check_language_sql_update);
			stat.bindString(1, name);
			stat.bindLong(2, languageId);
		}
		boolean exists = stat.simpleQueryForLong() > 0;
		stat.close();
		return exists;
	}
}