package com.appslandia.phrasebuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.appslandia.core.io.KeyDataHandler;
import com.appslandia.core.io.KeyDataOutput;
import com.appslandia.core.io.KeyDataReader;
import com.appslandia.core.io.KeyDataWriter;
import com.appslandia.core.utils.MathUtils;
import com.appslandia.core.utils.Out;
import com.appslandia.phrasebuilder.sqlite.DbManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class BackupLocalImpl {

	public static final Object mutex = new Object();
	public static final String KEY_LAST_BACKUP_TIMESTAMP = "lastBackupTimestamp";
	public static final String BACKUP_DATA_FILE = "phrase_builder_data.bak";

	final BackupHelperImpl helper;

	public static File getBackupFile() {
		return new File(Environment.getExternalStorageDirectory(), BACKUP_DATA_FILE);
	}

	public BackupLocalImpl(Context context) {
		super();
		this.helper = new BackupHelperImpl(context.getApplicationContext());
	}

	private void saveBackupTimestamp(KeyDataOutput output, long backupTimestamp) throws IOException {
		output.writeEntity(KEY_LAST_BACKUP_TIMESTAMP, MathUtils.toByteArray(backupTimestamp));
	}

	public void onBackup() throws IOException {
		synchronized (mutex) {
			DbManager manager = null;
			KeyDataWriter writer = null;

			try {
				manager = new DbManager(this.helper.context);
				SQLiteDatabase db = manager.getReadableDatabase();

				writer = new KeyDataWriter(new FileOutputStream(getBackupFile()));

				// Write backupTimestamp FIRST
				long backupTimestamp = System.currentTimeMillis() + 1500;
				this.saveBackupTimestamp(writer, backupTimestamp);

				this.helper.backupLanguages(writer, db);
				this.helper.backupLabels(writer, db);
				this.helper.backupPhrases(writer, db);
				this.helper.backupPhraseLabels(writer, db);

			} catch (IOException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new IOException(ex.getMessage(), ex);
			} finally {
				if (manager != null) {
					manager.close();
				}
				if (writer != null) {
					writer.close();
				}
			}
		}
	}

	public void onRestore() throws IOException {
		synchronized (mutex) {
			// Not initialize data
			DbManager manager = new DbManager(this.helper.context, false);

			final SQLiteDatabase db = manager.getWritableDatabase();
			KeyDataReader reader = null;

			try {
				// beginTransaction
				db.beginTransaction();

				// Reset all
				db.execSQL(DbManager.delete_table_phrase_sql);
				db.execSQL(DbManager.delete_table_phrase_label_sql);
				db.execSQL(DbManager.delete_table_label_sql);
				db.execSQL(DbManager.delete_table_language_sql);

				// handler
				KeyDataHandler handler = new KeyDataHandler() {

					@Override
					public boolean handle(String key, byte[] data) {
						helper.restoreKeyData(db, key, data);
						return true;
					}
				};

				// reader
				reader = new KeyDataReader(new FileInputStream(new File(Environment.getExternalStorageDirectory(), BACKUP_DATA_FILE)), handler);
				reader.startRead();

				// setTransactionSuccessful
				db.setTransactionSuccessful();

			} catch (IOException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new IOException(ex.getMessage(), ex);
			} finally {
				// endTransaction
				db.endTransaction();

				manager.close();
				if (reader != null) {
					reader.close();
				}
			}
		}
	}

	public static boolean isBackupFileExists() {
		return getBackupFile().exists();
	}

	public static boolean verifyBackupState() {
		KeyDataReader reader = null;
		try {
			// handler
			KeyDataHandler handler = new KeyDataHandler() {

				@Override
				public boolean handle(String key, byte[] data) {
					return true;
				}
			};

			// reader
			reader = new KeyDataReader(new FileInputStream(getBackupFile()), handler);
			reader.startRead();

		} catch (IOException ex) {
			return false;
		} catch (Exception ex) {
			return false;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					return false;
				}
			}
		}
		return true;
	}

	public static long tryGetBackupTimestamp() {
		KeyDataReader reader = null;
		final Out<Long> timestamp = new Out<Long>();

		try {
			// handler
			KeyDataHandler handler = new KeyDataHandler() {

				@Override
				public boolean handle(String key, byte[] data) {
					if (key.equals(KEY_LAST_BACKUP_TIMESTAMP)) {
						timestamp.setValue(MathUtils.toLong(data));
						return false;
					}
					return true;
				}
			};

			// reader
			reader = new KeyDataReader(new FileInputStream(getBackupFile()), handler);
			reader.startRead();

		} catch (IOException ex) {
			return 0;
		} catch (Exception ex) {
			return 0;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					return 0;
				}
			}
		}
		return timestamp.getValue() != null ? timestamp.getValue() : (0);
	}
}
