package com.appslandia.phrasebuilder.sqlite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.sqlite.SQLiteUtils;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.DateUtils;
import com.appslandia.phrasebuilder.entities.Phrase;
import com.appslandia.phrasebuilder.utils.PhraseBuilderUtils;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public final class PhraseDao {

	// @formatter:off
	public static final String create_sql = 
			"INSERT INTO phrase "
			+ "(phrase_text,key_word,s_keyword,notes,language_id"
			+ ",mastered,last_updated,deleted,bundle_id)" 
			+ " VALUES (?,?,?,?,?,?,?,?,?)";
	// @formatter:on

	public static void insert(SQLiteDatabase db, Phrase phrase) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(create_sql);
			stat.bindString(1, phrase.phrase_text);
			stat.bindString(2, phrase.key_word);
			stat.bindString(3, phrase.s_keyword);
			stat.bindString(4, phrase.notes);
			stat.bindLong(5, phrase.language_id);

			SQLiteUtils.bindBoolean(stat, 6, phrase.mastered);
			stat.bindLong(7, phrase.last_updated);
			stat.bindLong(8, phrase.deleted);
			stat.bindLong(9, phrase.bundle_id);

			try {
				int phraseId = (int) stat.executeInsert();
				phrase._id = phraseId;
			} finally {
				stat.close();
			}
		}
	}

	// @formatter:off
	public static final String create_restore_sql = 
			"INSERT INTO phrase "
			+ "(_id,phrase_text,key_word,s_keyword,notes,language_id"
			+ ",mastered,last_updated,deleted,bundle_id)" 
			+ " VALUES (?,?,?,?,?,?,?,?,?,?)";
	// @formatter:on

	public static void insertRestore(SQLiteDatabase db, Phrase phrase) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(create_restore_sql);
			stat.bindLong(1, phrase._id);
			stat.bindString(2, phrase.phrase_text);
			stat.bindString(3, phrase.key_word);
			stat.bindString(4, phrase.s_keyword);
			stat.bindString(5, phrase.notes);
			stat.bindLong(6, phrase.language_id);

			SQLiteUtils.bindBoolean(stat, 7, phrase.mastered);
			stat.bindLong(8, phrase.last_updated);
			stat.bindLong(9, phrase.deleted);
			stat.bindLong(10, phrase.bundle_id);

			try {
				stat.executeInsert();
			} finally {
				stat.close();
			}
		}
	}

	// @formatter:off
	public static final String update_sql = 
			"UPDATE phrase SET" 
				+ "  phrase_text=?" 
				+ "  ,key_word=?" 
				+ "  ,s_keyword=?" 
				+ "  ,notes=?" 
				+ "  ,language_id=?" 
				+ "  ,mastered=?" 
				+ "  ,last_updated=?" 
				+ " WHERE _id=?";

	// @formatter:on

	public static int update(SQLiteDatabase db, Phrase phrase) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(update_sql);
			stat.bindString(1, phrase.phrase_text);
			stat.bindString(2, phrase.key_word);
			stat.bindString(3, phrase.s_keyword);
			stat.bindString(4, phrase.notes);
			stat.bindLong(5, phrase.language_id);

			SQLiteUtils.bindBoolean(stat, 6, phrase.mastered);
			stat.bindLong(7, phrase.last_updated);
			stat.bindLong(8, phrase._id);

			// Not Update bundle_id
			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	public static final String delete_sql = "UPDATE phrase SET deleted=? WHERE _id=?";

	public static int delete(SQLiteDatabase db, int phraseId) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(delete_sql);
			stat.bindLong(1, System.currentTimeMillis());
			stat.bindLong(2, phraseId);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	public static final String restore_sql = "UPDATE phrase SET deleted=0,last_updated=? WHERE _id=?";

	public static int restore(SQLiteDatabase db, int phraseId, long dateRestored) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(restore_sql);
			stat.bindLong(1, dateRestored);
			stat.bindLong(2, phraseId);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	public static final String delete_forever_sql = "DELETE FROM phrase WHERE _id=?";

	public static int deleteForever(SQLiteDatabase db, int phraseId) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(delete_forever_sql);
			stat.bindLong(1, phraseId);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	public static final String delete_trash_sql = "DELETE FROM phrase WHERE language_id=? AND deleted>0";

	public static int deleteTrash(SQLiteDatabase db, int languageId) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(delete_trash_sql);
			stat.bindLong(1, languageId);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	// @formatter:off
	
	public static final String search_sql = 
			"SELECT p._id,p.phrase_text,p.key_word,p.notes,p.language_id,p.mastered,p.last_updated,p2.labels" 
			+ " FROM phrase p," 
			+ "   (SELECT p1._id,'|' || group_concat(p1.labels,'|') || '|' AS labels" 
			+ "    FROM"
			+ "      (SELECT p._id,fl.label_id || ':' || l.name || ':' || l.s_name AS labels" 
			+ "       FROM phrase p" 
			+ "       LEFT JOIN phrase_label fl ON p._id=fl.phrase_id" 
			+ "       LEFT JOIN label l ON fl.label_id=l._id"
			+ "       WHERE p.deleted=0 AND p.language_id=?" 
			+ "       ) p1" 
			+ "    GROUP BY p1._id) p2" 
			+ " WHERE p._id=p2._id" 
			+ "   AND p.last_updated<?" 
			+ "   AND (" 
			+ "         (?='')" 
			+ "         OR (p.s_keyword LIKE ? OR p2.labels LIKE ?)"
			+ "       )" 
			+ " ORDER BY p.last_updated DESC LIMIT " + PhraseBuilderUtils.PHRASES_LOAD_LIMIT;

	// @formatter:on

	// @formatter:off
	public static final String search_unlabeled_sql = 
			"SELECT p._id,p.phrase_text,p.key_word,p.notes,p.language_id,p.mastered,p.last_updated,p2.labels" 
			+ " FROM phrase p," 
			+ "   (SELECT p1._id,'|' || group_concat(p1.labels,'|') || '|' AS labels" 
			+ "    FROM"
			+ "      (SELECT p._id,fl.label_id || ':' || l.name || ':' || l.s_name AS labels" 
			+ "       FROM phrase p" 
			+ "       LEFT JOIN phrase_label fl ON p._id=fl.phrase_id" 
			+ "       LEFT JOIN label l ON fl.label_id=l._id"
			+ "       WHERE p.deleted=0 AND p.language_id=?" 
			+ "       ) p1" 
			+ "    GROUP BY p1._id) p2" 
			+ " WHERE p._id=p2._id" 
			+ "   AND p.last_updated<?" 
			+ "   AND (p2.labels IS NULL OR p.s_keyword LIKE ? OR p2.labels LIKE ?)"
			+ " ORDER BY p.last_updated DESC LIMIT " + PhraseBuilderUtils.PHRASES_LOAD_LIMIT;

	// @formatter:on

	// @formatter:off
	public static final String search_learning_sql = 
			"SELECT p._id,p.phrase_text,p.key_word,p.notes,p.language_id,p.mastered,p.last_updated,p2.labels" 
			+ " FROM phrase p," 
			+ "   (SELECT p1._id,'|' || group_concat(p1.labels,'|') || '|' AS labels" 
			+ "    FROM"
			+ "      (SELECT p._id,fl.label_id || ':' || l.name || ':' || l.s_name AS labels" 
			+ "       FROM phrase p" 
			+ "       LEFT JOIN phrase_label fl ON p._id=fl.phrase_id" 
			+ "       LEFT JOIN label l ON fl.label_id=l._id"
			+ "       WHERE p.deleted=0 AND p.language_id=?" 
			+ "       ) p1" 
			+ "    GROUP BY p1._id) p2" 
			+ " WHERE p._id=p2._id" 
			+ "   AND p.last_updated<?"
			+ "   AND (p.mastered=? OR p.s_keyword LIKE ? OR p2.labels LIKE ?)"
			+ " ORDER BY p.last_updated DESC LIMIT " + PhraseBuilderUtils.PHRASES_LOAD_LIMIT;

	// @formatter:on

	public static List<Phrase> search(SQLiteDatabase db, int languageId, long maxLastUpdated, String searchText) {
		Cursor c;
		if (searchText.isEmpty()) {
			c = db.rawQuery(search_sql, new String[] { Integer.toString(languageId), Long.toString(maxLastUpdated), searchText, "%", "%:|%" });

		} else if (PhraseUtils.KEYWORD_UNLABELED.equals(searchText)) {
			c = db.rawQuery(search_unlabeled_sql, new String[] { Integer.toString(languageId), Long.toString(maxLastUpdated), ("%" + searchText + "%"), ("%:" + searchText + "|%") });

		} else if (PhraseUtils.KEYWORD_LEARNING.equals(searchText) || PhraseUtils.KEYWORD_MASTERED.equals(searchText)) {
			String mastered = PhraseUtils.KEYWORD_LEARNING.equals(searchText) ? ("0") : ("1");
			c = db.rawQuery(search_learning_sql, new String[] { Integer.toString(languageId), Long.toString(maxLastUpdated), mastered, ("%" + searchText + "%"), ("%:" + searchText + "|%") });

		} else {
			c = db.rawQuery(search_sql, new String[] { Integer.toString(languageId), Long.toString(maxLastUpdated), searchText, ("%" + searchText + "%"), ("%:" + searchText + "|%") });
		}

		List<Phrase> list = new ArrayList<Phrase>();
		if (c.moveToFirst()) {
			while (c.isAfterLast() == false) {
				Phrase obj = new Phrase();

				obj._id = c.getInt(0);
				obj.phrase_text = c.getString(1);
				obj.key_word = c.getString(2);
				obj.notes = c.getString(3);
				obj.language_id = c.getInt(4);

				obj.mastered = SQLiteUtils.readBoolean(c, 5);
				obj.last_updated = c.getLong(6);
				obj.labels = c.getString(7);

				obj.mem_just_updated = obj.last_updated;

				list.add(obj);
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}

	public static final String update_mastered_sql = "UPDATE phrase SET mastered=? WHERE _id=?";

	public static int updateMasteredTag(SQLiteDatabase db, int phraseId, boolean mastered) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(update_mastered_sql);
			SQLiteUtils.bindBoolean(stat, 1, mastered);
			stat.bindLong(2, phraseId);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	public static final String delete_by_language_sql = "DELETE FROM phrase WHERE language_id=?";

	public static int deleteByLanguage(SQLiteDatabase db, int languageId) {
		synchronized (BackupUtils.MUTEX) {
			SQLiteStatement stat = db.compileStatement(delete_by_language_sql);
			stat.bindLong(1, languageId);

			try {
				return stat.executeUpdateDelete();
			} finally {
				stat.close();
			}
		}
	}

	// @formatter:off
	public static final String query_count_phrases_sql = 
								"SELECT COUNT(1) FROM phrase p WHERE p.language_id=? and p.deleted=0;";
	// @formatter:on

	public static int queryCountPhrases(SQLiteDatabase db, int languageId) {
		Cursor c = db.rawQuery(query_count_phrases_sql, new String[] { Integer.toString(languageId) });
		int count = 0;
		if (c.moveToFirst()) {
			while (c.isAfterLast() == false) {
				count = c.getInt(0);
				break;
			}
		}
		c.close();
		return count;
	}

	// @formatter:off
	public static final String load_deleted_sql = 
			"SELECT p._id,p.phrase_text,p.key_word,p.notes,p.language_id,p.mastered,p.last_updated,p.deleted,p2.labels" 
			+ " FROM phrase p," 
			+ "   (SELECT p1._id,'|' || group_concat(p1.labels,'|') || '|' AS labels" 
			+ "    FROM"
			+ "      (SELECT p._id,fl.label_id || ':' || l.name || ':' || l.s_name AS labels" 
			+ "       FROM phrase p" 
			+ "       LEFT JOIN phrase_label fl ON p._id=fl.phrase_id" 
			+ "       LEFT JOIN label l ON fl.label_id=l._id"
			+ "       WHERE p.deleted>0 AND p.language_id=?" 
			+ "       ) p1" 
			+ "    GROUP BY p1._id) p2" 
			+ " WHERE p._id=p2._id" 
			+ " ORDER BY p.deleted DESC";
	// @formatter:on

	public static List<Phrase> loadDeletedPhrases(SQLiteDatabase db, int languageId) {
		Cursor c = db.rawQuery(load_deleted_sql, new String[] { Integer.toString(languageId) });

		List<Phrase> list = new ArrayList<Phrase>();
		if (c.moveToFirst()) {
			while (c.isAfterLast() == false) {
				Phrase obj = new Phrase();

				obj._id = c.getInt(0);
				obj.phrase_text = c.getString(1);
				obj.key_word = c.getString(2);
				obj.notes = c.getString(3);
				obj.language_id = c.getInt(4);

				obj.mastered = SQLiteUtils.readBoolean(c, 5);
				obj.last_updated = c.getLong(6);
				obj.deleted = c.getLong(7);
				obj.labels = c.getString(8);

				list.add(obj);
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}

	// @formatter:off
	public static final String load_test_phrase_sql = 
			"SELECT p._id,p.phrase_text,p.key_word,p.notes,p.mastered,p2.labels"
			+ " FROM phrase p,"
			+ "   (SELECT p1._id,'|' || group_concat(p1.labels,'|') || '|' AS labels"
			+ "    FROM"
			+ "      (SELECT p._id,fl.label_id || ':' || l.name || ':' || l.s_name AS labels" 
			+ "       FROM phrase p" 
			+ "       LEFT JOIN phrase_label fl ON p._id=fl.phrase_id" 
			+ "       LEFT JOIN label l ON fl.label_id=l._id"
			+ "       WHERE p.deleted=0 AND p.language_id=?"
			+ "       ) p1"
			+ "    GROUP BY p1._id) p2"
			+ " WHERE p._id=p2._id"
			+ " AND (('1'=?) OR ('2'=? AND p.mastered=0) OR ('3'=? AND p.mastered=1))"
			+ " AND ((0=?) OR (p.last_updated >= ?))";

	// @formatter:on

	public static List<Phrase> loadTestPhrases(SQLiteDatabase db, int languageId, int masteryTypeId, int daysAgo, List<FilterableItem> labels) {
		String sql = null;

		// Labels
		if (labels.isEmpty()) {
			sql = load_test_phrase_sql;
		} else {
			StringBuilder sb = new StringBuilder(load_test_phrase_sql);
			sb.append(" AND (");
			boolean isFirst = true;
			for (FilterableItem label : labels) {
				if (isFirst) {
					isFirst = false;
				} else {
					sb.append(" OR");
				}
				sb.append(" (p2.labels LIKE '%|").append(label.getId()).append(":%')");
			}
			sb.append(")");
			sql = sb.toString();
		}
		String masteryType = Integer.toString(masteryTypeId);

		// lastUpdated
		long lastUpdatedParam = 0;
		if (daysAgo > 0) {
			lastUpdatedParam = System.currentTimeMillis() - (long) daysAgo * 86400000l;
			lastUpdatedParam = DateUtils.clearTime(new Date(lastUpdatedParam)).getTime();
		}
		String lastUpdatedStr = Long.toString(lastUpdatedParam);

		Cursor c = db.rawQuery(sql, new String[] { Integer.toString(languageId), masteryType, masteryType, masteryType, lastUpdatedStr, lastUpdatedStr });

		List<Phrase> list = new ArrayList<Phrase>();
		if (c.moveToFirst()) {
			while (c.isAfterLast() == false) {
				Phrase obj = new Phrase();

				obj._id = c.getInt(0);
				obj.phrase_text = c.getString(1);
				obj.key_word = c.getString(2);
				obj.notes = c.getString(3);

				obj.mastered = SQLiteUtils.readBoolean(c, 4);
				obj.labels = c.getString(5);

				list.add(obj);
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}

	// @formatter:off
	public static final String load_keyword_sql = 
			"SELECT DISTINCT p.key_word FROM phrase p WHERE p.language_id=?";

	// @formatter:on

	public static List<String> loadKeywords(SQLiteDatabase db, int languageId) {
		Cursor c = db.rawQuery(load_keyword_sql, new String[] { Integer.toString(languageId) });

		List<String> list = new ArrayList<String>();
		if (c.moveToFirst()) {
			while (c.isAfterLast() == false) {
				list.add(c.getString(0));
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}
}
