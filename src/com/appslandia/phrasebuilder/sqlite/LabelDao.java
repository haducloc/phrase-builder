package com.appslandia.phrasebuilder.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.utils.BackupUtils;
import com.appslandia.phrasebuilder.entities.Label;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public final class LabelDao {

	public static final String create_sql = "INSERT INTO label (name, s_name) VALUES (?,?)";

	public static void insert(SQLiteDatabase db, Label label) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(create_sql);
			stat.bindString(1, label.name);
			stat.bindString(2, label.s_name);

			try {
				int labelId = (int) stat.executeInsert();
				label._id = labelId;
			} finally {
				stat.close();
			}
		}
	}

	public static final String create_restore_sql = "INSERT INTO label (_id,name, s_name) VALUES (?,?,?)";

	public static void insertRestore(SQLiteDatabase db, Label label) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(create_restore_sql);
			stat.bindLong(1, label._id);
			stat.bindString(2, label.name);
			stat.bindString(3, label.s_name);

			try {
				stat.executeInsert();
			} finally {
				stat.close();
			}
		}
	}

	public static final String update_sql = "UPDATE label SET name=?, s_name=? WHERE _id=?";

	public static int update(SQLiteDatabase db, Label label) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(update_sql);
			stat.bindString(1, label.name);
			stat.bindString(2, label.s_name);
			stat.bindLong(3, label._id);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	public static final String delete_sql = "DELETE FROM label WHERE _id=?";

	public static int delete(SQLiteDatabase db, int labelId) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(delete_sql);
			stat.bindLong(1, labelId);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	public static final String query_labels_sql = "SELECT _id, name, s_name FROM label ORDER BY name";

	public static List<Label> queryLabels(SQLiteDatabase db) {
		Cursor c = db.rawQuery(query_labels_sql, null);
		List<Label> list = new ArrayList<Label>();
		if (c.moveToFirst()) {
			while (c.isAfterLast() == false) {
				Label obj = new Label();
				obj._id = c.getInt(0);
				obj.name = c.getString(1);
				obj.s_name = c.getString(2);

				list.add(obj);
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}

	// @formatter:off
	public static final String query_stat_sql = 
			"SELECT l._id, l.name, l.s_name" 
				+ ",(SELECT COUNT(1) FROM phrase p "
				+ "		WHERE p.language_id=? AND p.deleted=0"
				+ " 	AND EXISTS (SELECT 1 FROM phrase_label AS pl WHERE pl.phrase_id=p._id AND pl.label_id=l._id)) AS phrase_count"
				+ ",(SELECT COUNT(1) FROM phrase p "
				+ "		WHERE p.language_id=? AND p.deleted=0 AND p.mastered=1"
				+ " 	AND EXISTS (SELECT 1 FROM phrase_label AS pl WHERE pl.phrase_id=p._id AND pl.label_id=l._id)) AS mastered_count" 
				+ " FROM label AS l ORDER BY l.name";

	// @formatter:on

	public static List<Label> queryStat(SQLiteDatabase db, int languageId) {
		Cursor c = db.rawQuery(query_stat_sql, new String[] { Integer.toString(languageId), Integer.toString(languageId) });
		List<Label> list = new ArrayList<Label>();
		if (c.moveToFirst()) {
			while (c.isAfterLast() == false) {
				Label obj = new Label();
				obj._id = c.getInt(0);
				obj.name = c.getString(1);
				obj.s_name = c.getString(2);
				obj.phrase_count = c.getInt(3);
				obj.mastered_count = c.getInt(4);

				list.add(obj);
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}

	public static final String check_label_sql_new = "SELECT COUNT(1) FROM label WHERE s_name=?";
	public static final String check_label_sql_update = "SELECT COUNT(1) FROM label WHERE s_name=? AND _id!=?";

	public static boolean checkLabel(SQLiteDatabase db, String sName, Integer labelId) {
		SQLiteStatement stat = null;
		if (labelId == null) {
			stat = db.compileStatement(check_label_sql_new);
			stat.bindString(1, sName);
		} else {
			stat = db.compileStatement(check_label_sql_update);
			stat.bindString(1, sName);
			stat.bindLong(2, labelId);
		}
		boolean exists = stat.simpleQueryForLong() > 0;
		stat.close();
		return exists;
	}
}
