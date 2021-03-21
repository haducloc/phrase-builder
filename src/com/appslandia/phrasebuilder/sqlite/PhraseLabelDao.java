package com.appslandia.phrasebuilder.sqlite;

import com.appslandia.core.utils.BackupUtils;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public final class PhraseLabelDao {

	public static final String create_sql = "INSERT INTO phrase_label (phrase_id,label_id) VALUES (?,?)";

	public static long insert(SQLiteDatabase db, int phrase_id, int label_id) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(create_sql);
			stat.bindLong(1, phrase_id);
			stat.bindLong(2, label_id);

			try {
				return stat.executeInsert();
			} finally {
				stat.close();
			}
		}
	}

	public static final String delete_sql = "DELETE FROM phrase_label WHERE phrase_id=? AND label_id=?";

	public static int delete(SQLiteDatabase db, int phrase_id, int label_id) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(delete_sql);
			stat.bindLong(1, phrase_id);
			stat.bindLong(2, label_id);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	public static final String delete_by_label_sql = "DELETE FROM phrase_label WHERE label_id=?";

	public static int deleteByLabel(SQLiteDatabase db, int labelId) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(delete_by_label_sql);
			stat.bindLong(1, labelId);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}
}
