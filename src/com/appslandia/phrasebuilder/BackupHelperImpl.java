package com.appslandia.phrasebuilder;

import java.io.IOException;

import com.appslandia.core.io.KeyDataOutput;
import com.appslandia.core.sqlite.SQLiteUtils;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.CharsetUtils;
import com.appslandia.core.utils.PreferenceUtils;
import com.appslandia.core.utils.QueryParams;
import com.appslandia.core.utils.RecordTextBuilder;
import com.appslandia.core.utils.RecordTextInterator;
import com.appslandia.core.utils.SplitterUtils;
import com.appslandia.core.utils.StringUtils;
import com.appslandia.phrasebuilder.entities.Label;
import com.appslandia.phrasebuilder.entities.Language;
import com.appslandia.phrasebuilder.sqlite.LabelDao;
import com.appslandia.phrasebuilder.sqlite.LanguageDao;
import com.appslandia.phrasebuilder.sqlite.PhraseDao;
import com.appslandia.phrasebuilder.sqlite.PhraseLabelDao;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class BackupHelperImpl {

	public static final String KEY_LANGUAGES = "languages";
	public static final String KEY_LABELS = "labels";
	public static final String KEY_PHRASES = "phrases";
	public static final String KEY_PHRASE_LABELS = "phraseLabels";
	public static final String KEY_PREFERENCES = "preferences";

	public static final byte[] EMPTY_BYTES = new byte[0];

	public static final int PHRASE_CHUNK_NUMBER = 100;
	public static final int PHRASE_CHUNK_RECORDS = 150;

	public static final int PHRASE_LABEL_CHUNK_NUMBER = 10;
	public static final int PHRASE_LABEL_CHUNK_RECORDS = 200;

	Context context;

	public BackupHelperImpl(Context context) {
		this.context = context;
	}

	public void restoreKeyData(SQLiteDatabase db, String key, byte[] data) {
		if (key.equals(KEY_LANGUAGES)) {
			this.restoreLanguages(db, data);

		} else if (key.equals(KEY_LABELS)) {
			this.restoreLabels(db, data);

		} else if (key.startsWith(KEY_PHRASES)) {
			this.restorePhrases(db, data);

		} else if (key.startsWith(KEY_PHRASE_LABELS)) {
			this.restorePhraseLabels(db, data);

		} else if (key.equals(KEY_PREFERENCES)) {
			this.restorePreferences(data);
		}
	}

	public QueryParams initPrefQueryParams() {
		return new QueryParams(16);
	}

	public long backupPreferences(KeyDataOutput output) throws IOException {
		// QueryParams
		QueryParams queryParams = initPrefQueryParams();

		// MainActivity
		boolean learnWorkflowShowed = PreferenceUtils.getBoolean(context, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_LEARN_WORKFLOW, false);
		boolean rateUsDisabled = PreferenceUtils.getBoolean(context, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_DISABLED, false);
		int rateUsLaters = PreferenceUtils.getInt(context, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_LATERS, 0);
		int rateUsLastCount = PreferenceUtils.getInt(context, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_LAST_COUNT, 0);

		queryParams.put(MainActivity.PREFERENCE_LEARN_WORKFLOW, String.valueOf(learnWorkflowShowed));
		queryParams.put(MainActivity.PREFERENCE_RATE_US_DISABLED, String.valueOf(rateUsDisabled));
		queryParams.put(MainActivity.PREFERENCE_RATE_US_LATERS, String.valueOf(rateUsLaters));
		queryParams.put(MainActivity.PREFERENCE_RATE_US_LAST_COUNT, String.valueOf(rateUsLastCount));

		// PhraseListActivity
		int languageId = PreferenceUtils.getInt(context, PhraseListActivity.PREFERENCE_ID, PhraseListActivity.PREFERENCE_LANGUAGE_ID, 0);
		queryParams.put(PhraseListActivity.PREFERENCE_LANGUAGE_ID, String.valueOf(languageId));

		// PhraseEditActivity
		languageId = PreferenceUtils.getInt(context, PhraseEditActivity.PREFERENCE_ID, PhraseEditActivity.PREFERENCE_LANGUAGE_ID, 0);
		queryParams.put(PhraseEditActivity.PREFERENCE_LANGUAGE_ID, String.valueOf(languageId));

		// PhraseTrashActivity
		languageId = PreferenceUtils.getInt(context, PhraseTrashActivity.PREFERENCE_ID, PhraseTrashActivity.PREFERENCE_LANGUAGE_ID, 0);
		queryParams.put(PhraseTrashActivity.PREFERENCE_LANGUAGE_ID, String.valueOf(languageId));

		// LabelListActivity
		languageId = PreferenceUtils.getInt(context, LabelListActivity.PREFERENCE_ID, LabelListActivity.PREFERENCE_LANGUAGE_ID, 0);
		queryParams.put(LabelListActivity.PREFERENCE_LANGUAGE_ID, String.valueOf(languageId));

		// PhraseTestInputsActivity
		languageId = PreferenceUtils.getInt(context, PhraseTestInputsActivity.PREFERENCE_ID, PhraseTestInputsActivity.PREFERENCE_LANGUAGE_ID, 0);
		queryParams.put(PhraseTestInputsActivity.PREFERENCE_LANGUAGE_ID, String.valueOf(languageId));

		// PhraseTestActivity
		boolean soundEnabled = PreferenceUtils.getBoolean(context, PhraseTestActivity.PREFERENCE_ID, PhraseTestActivity.PREFERENCE_TEST_SOUND_ENABLED, true);
		queryParams.put(PhraseTestActivity.PREFERENCE_TEST_SOUND_ENABLED, String.valueOf(soundEnabled));

		// ManageBackupActivity
		SharedPreferences prefs = context.getSharedPreferences(ManageBackupActivity.PREFERENCE_ID, Context.MODE_PRIVATE);
		int backupOnDay = prefs.getInt(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_ON_DAY, BackupUtils.DEFAULT_BACKUP_REQUEST_ON_DAY);
		int backupAtHour = prefs.getInt(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_AT_HOUR, BackupUtils.DEFAULT_BACKUP_REQUEST_AT_HOUR);
		int backupAtMinute = prefs.getInt(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_AT_MIMUTE, BackupUtils.DEFAULT_BACKUP_REQUEST_AT_MINUTE);

		queryParams.put(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_ON_DAY, String.valueOf(backupOnDay));
		queryParams.put(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_AT_HOUR, String.valueOf(backupAtHour));
		queryParams.put(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_AT_MIMUTE, String.valueOf(backupAtMinute));

		// >>> lastBackupTimestamp
		long lastBackupTimestamp = System.currentTimeMillis();
		queryParams.put(ManageBackupActivity.PREFERENCE_LAST_BACKUP_TIMESTAMP, String.valueOf(lastBackupTimestamp));

		// Write backup
		byte[] data = CharsetUtils.toBytes(queryParams.toQueryString());
		output.writeEntity(KEY_PREFERENCES, data);

		return lastBackupTimestamp;
	}

	public void restoreLanguages(SQLiteDatabase db, byte[] data) {
		RecordTextInterator iter = new RecordTextInterator(CharsetUtils.toString(data), StringUtils.RECORD_SEP_CHAR);
		while (iter.hasNext()) {
			String[] items = SplitterUtils.split(iter.next(), StringUtils.TAB_CHAR);

			Language obj = new Language();
			obj._id = Integer.parseInt(items[0]);
			obj.name = items[1];
			obj.lang_pos = Integer.parseInt(items[2]);

			LanguageDao.insertRestore(db, obj);
		}
	}

	public void restoreLabels(SQLiteDatabase db, byte[] data) {
		RecordTextInterator iter = new RecordTextInterator(CharsetUtils.toString(data), StringUtils.RECORD_SEP_CHAR);
		while (iter.hasNext()) {
			String[] items = SplitterUtils.split(iter.next(), StringUtils.TAB_CHAR);

			Label obj = new Label();
			obj._id = Integer.parseInt(items[0]);
			obj.name = items[1];
			obj.s_name = StringUtils.toSearchable(obj.name);

			LabelDao.insertRestore(db, obj);
		}
	}

	public void restorePhrases(SQLiteDatabase db, byte[] data) {
		if (data.length == EMPTY_BYTES.length) {
			return;
		}
		RecordTextInterator iter = new RecordTextInterator(CharsetUtils.toString(data), StringUtils.RECORD_SEP_CHAR);

		SQLiteStatement stat = db.compileStatement(PhraseDao.create_restore_sql);

		while (iter.hasNext()) {
			String[] items = SplitterUtils.split(iter.next(), StringUtils.TAB_CHAR);

			stat.bindLong(1, Integer.parseInt(items[0])); // _id
			stat.bindString(2, items[1]); // phrase_text
			stat.bindString(3, items[2]); // key_word
			stat.bindString(4, StringUtils.toSearchable(items[2])); // s_keyword
			stat.bindString(5, items[3]); // notes

			stat.bindLong(6, Integer.parseInt(items[4])); // language_id
			SQLiteUtils.bindBoolean(stat, 7, Boolean.parseBoolean(items[5])); // mastered
			stat.bindLong(8, Long.parseLong(items[6])); // last_updated
			stat.bindLong(9, Long.parseLong(items[7])); // deleted
			stat.bindLong(10, Integer.parseInt(items[8])); // bundle_id

			// executeInsert
			stat.executeInsert();
		}
		stat.close();
	}

	public void restorePhraseLabels(SQLiteDatabase db, byte[] data) {
		if (data.length == EMPTY_BYTES.length) {
			return;
		}
		RecordTextInterator iter = new RecordTextInterator(CharsetUtils.toString(data), StringUtils.RECORD_SEP_CHAR);

		SQLiteStatement stat = db.compileStatement(PhraseLabelDao.create_sql);

		while (iter.hasNext()) {
			String[] items = SplitterUtils.split(iter.next(), StringUtils.TAB_CHAR);

			stat.bindLong(1, Integer.parseInt(items[0]));
			stat.bindLong(2, Integer.parseInt(items[1]));

			// executeInsert
			stat.executeInsert();
		}
		stat.close();
	}

	public void restorePreferences(byte[] data) {
		// queryParams
		QueryParams queryParams = this.initPrefQueryParams();
		queryParams.parse(CharsetUtils.toString(data));

		// MainActivity
		String learnWorkflowShowed = queryParams.getString(MainActivity.PREFERENCE_LEARN_WORKFLOW);
		String rateUsDisabled = queryParams.getString(MainActivity.PREFERENCE_RATE_US_DISABLED);
		String rateUsLaters = queryParams.getString(MainActivity.PREFERENCE_RATE_US_LATERS);
		String rateUsLastCount = queryParams.getString(MainActivity.PREFERENCE_RATE_US_LAST_COUNT);

		PreferenceUtils.savePreference(context, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_LEARN_WORKFLOW, Boolean.parseBoolean(learnWorkflowShowed));
		PreferenceUtils.savePreference(context, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_DISABLED, Boolean.parseBoolean(rateUsDisabled));
		PreferenceUtils.savePreference(context, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_LATERS, Integer.parseInt(rateUsLaters));
		PreferenceUtils.savePreference(context, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_LAST_COUNT, Integer.parseInt(rateUsLastCount));

		// PhraseListActivity
		String languageId = queryParams.getString(PhraseListActivity.PREFERENCE_LANGUAGE_ID);
		PreferenceUtils.savePreference(context, PhraseListActivity.PREFERENCE_ID, PhraseListActivity.PREFERENCE_LANGUAGE_ID, Integer.parseInt(languageId));

		// PhraseEditActivity
		languageId = queryParams.getString(PhraseEditActivity.PREFERENCE_LANGUAGE_ID);
		PreferenceUtils.savePreference(context, PhraseEditActivity.PREFERENCE_ID, PhraseEditActivity.PREFERENCE_LANGUAGE_ID, Integer.parseInt(languageId));

		// PhraseTrashActivity
		languageId = queryParams.getString(PhraseTrashActivity.PREFERENCE_LANGUAGE_ID);
		PreferenceUtils.savePreference(context, PhraseTrashActivity.PREFERENCE_ID, PhraseTrashActivity.PREFERENCE_LANGUAGE_ID, Integer.parseInt(languageId));

		// LabelListActivity
		languageId = queryParams.getString(LabelListActivity.PREFERENCE_LANGUAGE_ID);
		PreferenceUtils.savePreference(context, LabelListActivity.PREFERENCE_ID, LabelListActivity.PREFERENCE_LANGUAGE_ID, Integer.parseInt(languageId));

		// PhraseTestInputsActivity
		languageId = queryParams.getString(PhraseTestInputsActivity.PREFERENCE_LANGUAGE_ID);
		PreferenceUtils.savePreference(context, PhraseTestInputsActivity.PREFERENCE_ID, PhraseTestInputsActivity.PREFERENCE_LANGUAGE_ID, Integer.parseInt(languageId));

		// PhraseTestActivity
		String soundEnabled = queryParams.getString(PhraseTestActivity.PREFERENCE_TEST_SOUND_ENABLED);
		PreferenceUtils.savePreference(context, PhraseTestActivity.PREFERENCE_ID, PhraseTestActivity.PREFERENCE_TEST_SOUND_ENABLED, Boolean.parseBoolean(soundEnabled));

		// ManageBackupActivity
		Editor editor = context.getSharedPreferences(ManageBackupActivity.PREFERENCE_ID, Context.MODE_PRIVATE).edit();

		String backupOnDay = queryParams.getString(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_ON_DAY);
		String backupAtHour = queryParams.getString(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_AT_HOUR);
		String backupAtMinute = queryParams.getString(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_AT_MIMUTE);
		String lastBackupTimestamp = queryParams.getString(ManageBackupActivity.PREFERENCE_LAST_BACKUP_TIMESTAMP);

		editor.putInt(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_ON_DAY, Integer.parseInt(backupOnDay));
		editor.putInt(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_AT_HOUR, Integer.parseInt(backupAtHour));
		editor.putInt(ManageBackupActivity.PREFERENCE_REQUEST_BACKUP_AT_MIMUTE, Integer.parseInt(backupAtMinute));
		editor.putLong(ManageBackupActivity.PREFERENCE_LAST_BACKUP_TIMESTAMP, Long.parseLong(lastBackupTimestamp));

		// Commit All
		editor.commit();
	}

	public static final String select_languages_sql = "SELECT _id,name,lang_pos FROM language";

	public void backupLanguages(KeyDataOutput output, SQLiteDatabase db) throws IOException {
		Cursor c = db.rawQuery(select_languages_sql, null);
		if (c.moveToFirst()) {

			StringBuilder builder = new StringBuilder();
			while (c.isAfterLast() == false) {
				if (builder.length() > 0) {
					builder.append(StringUtils.RECORD_SEP_CHAR);
				}

				builder.append(c.getInt(0)).append(StringUtils.TAB_CHAR); // _id
				builder.append(c.getString(1)).append(StringUtils.TAB_CHAR); // name
				builder.append(c.getInt(2)); // lang_pos

				c.moveToNext();
			}
			// Write backup
			byte[] data = CharsetUtils.toBytes(builder.toString());
			output.writeEntity(KEY_LANGUAGES, data);
		}
		c.close();
	}

	public static final String select_labels_sql = "SELECT _id,name FROM label";

	public void backupLabels(KeyDataOutput output, SQLiteDatabase db) throws IOException {
		Cursor c = db.rawQuery(select_labels_sql, null);
		if (c.moveToFirst()) {
			StringBuilder builder = new StringBuilder();
			while (c.isAfterLast() == false) {
				if (builder.length() > 0) {
					builder.append(StringUtils.RECORD_SEP_CHAR);
				}

				builder.append(c.getInt(0)).append(StringUtils.TAB_CHAR); // _id
				builder.append(c.getString(1)); // name

				c.moveToNext();
			}
			// Write backup
			byte[] data = CharsetUtils.toBytes(builder.toString());
			output.writeEntity(KEY_LABELS, data);
		}
		c.close();
	}

	// @formatter:off
	public static final String select_phrases_sql = 
			"SELECT _id,phrase_text,key_word,notes,language_id"
			+ ",mastered,last_updated,deleted,bundle_id"
			+ " FROM phrase";
	// @formatter:on

	public void backupPhrases(KeyDataOutput output, SQLiteDatabase db) throws IOException {
		Cursor c = db.rawQuery(select_phrases_sql, null);
		if (c.moveToFirst()) {

			final RecordTextBuilder builder = new RecordTextBuilder(StringUtils.RECORD_SEP_CHAR, StringUtils.TAB_CHAR);
			while (c.isAfterLast() == false) {

				// beginRecord
				builder.beginRecord();

				// Fields
				builder.append(c.getInt(0)); // _id
				builder.append(c.getString(1)); // phrase_text
				builder.append(c.getString(2)); // key_word
				builder.append(c.getString(3)); // notes
				builder.append(c.getInt(4)); // language_id
				builder.append(SQLiteUtils.readBoolean(c, 5)); // mastered
				builder.append(c.getLong(6)); // last_updated
				builder.append(c.getLong(7)); // deleted

				builder.appendLast(c.getInt(8)); // bundle_id

				// finishRecord
				builder.finishRecord();

				// Chunk full?
				if (builder.getRecordCount() % PHRASE_CHUNK_RECORDS == 0) {
					if (builder.getSequence() < PHRASE_CHUNK_NUMBER) {

						// Write Chunk
						byte[] data = CharsetUtils.toBytes(builder.toString());
						output.writeEntity(KEY_PHRASES + builder.getSequence(), data);

						// reset
						builder.reset();
					}
				}
				c.moveToNext();
			} // -- end while

			// Write Chunk
			if (builder.isEmpty() == false) {
				byte[] data = CharsetUtils.toBytes(builder.toString());
				output.writeEntity(KEY_PHRASES + builder.getSequence(), data);
			}

			// emptyChunkSeq
			final int emptyChunkSeq = builder.isEmpty() ? builder.getSequence() : (builder.getSequence() + 1);

			for (int chunkSeq = emptyChunkSeq; chunkSeq <= PHRASE_CHUNK_NUMBER; chunkSeq++) {
				output.writeEntity(KEY_PHRASES + chunkSeq, EMPTY_BYTES);
			}
		}
		c.close();
	}

	public static final String select_phrase_labels_sql = "SELECT phrase_id,label_id FROM phrase_label";

	public void backupPhraseLabels(KeyDataOutput output, SQLiteDatabase db) throws IOException {
		Cursor c = db.rawQuery(select_phrase_labels_sql, null);
		if (c.moveToFirst()) {

			final RecordTextBuilder builder = new RecordTextBuilder(StringUtils.RECORD_SEP_CHAR, StringUtils.TAB_CHAR);
			while (c.isAfterLast() == false) {

				// beginRecord
				builder.beginRecord();

				// Fields
				builder.append(c.getInt(0)); // phrase_id
				builder.appendLast(c.getInt(1)); // label_id

				// finishRecord
				builder.finishRecord();

				// Chunk full?
				if (builder.getRecordCount() % PHRASE_LABEL_CHUNK_RECORDS == 0) {
					if (builder.getSequence() < PHRASE_LABEL_CHUNK_NUMBER) {

						// Write Chunk
						byte[] data = CharsetUtils.toBytes(builder.toString());
						output.writeEntity(KEY_PHRASE_LABELS + builder.getSequence(), data);

						// reset
						builder.reset();
					}
				}
				c.moveToNext();
			} // -- end while

			// Write Chunk
			if (builder.isEmpty() == false) {
				byte[] data = CharsetUtils.toBytes(builder.toString());
				output.writeEntity(KEY_PHRASE_LABELS + builder.getSequence(), data);
			}

			// emptyChunkSeq
			final int emptyChunkSeq = builder.isEmpty() ? builder.getSequence() : (builder.getSequence() + 1);

			for (int chunkSeq = emptyChunkSeq; chunkSeq <= PHRASE_LABEL_CHUNK_NUMBER; chunkSeq++) {
				output.writeEntity(KEY_PHRASE_LABELS + chunkSeq, EMPTY_BYTES);
			}
		}
		c.close();
	}
}
