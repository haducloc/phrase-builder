package com.appslandia.phrasebuilder;

import java.io.IOException;

import com.appslandia.core.io.DataBackupWriter;
import com.appslandia.core.io.KeyDataOutput;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.PreferenceUtils;
import com.appslandia.phrasebuilder.sqlite.DbManager;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.database.sqlite.SQLiteDatabase;
import android.os.ParcelFileDescriptor;

public class BackupAgentImpl extends BackupAgent {

	final BackupHelperImpl helper;

	public BackupAgentImpl() {
		super();
		this.helper = new BackupHelperImpl(this);
	}

	private void saveBackupTimestamp(long backupTimestamp) {
		PreferenceUtils.savePreference(this, ManageBackupActivity.PREFERENCE_ID, ManageBackupActivity.PREFERENCE_LAST_BACKUP_TIMESTAMP, backupTimestamp);
	}

	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
		synchronized (BackupUtils.MUTEX) {
			DbManager manager = null;
			try {
				manager = new DbManager(this);
				SQLiteDatabase db = manager.getReadableDatabase();

				final KeyDataOutput output = new DataBackupWriter(data);

				this.helper.backupLanguages(output, db);
				this.helper.backupLabels(output, db);
				this.helper.backupPhrases(output, db);
				this.helper.backupPhraseLabels(output, db);

				// backupTimestamp
				long backupTimestamp = this.helper.backupPreferences(output);
				this.saveBackupTimestamp(backupTimestamp);

				BackupUtils.notifyBackupPerformed(this, R.drawable.ic_notification_phrasebuilder, R.string.app_name, MainActivity.class);
			} catch (IOException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new IOException(ex.getMessage(), ex);
			} finally {
				if (manager != null) {
					manager.close();
				}
			}
		}
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
		synchronized (BackupUtils.MUTEX) {
			// Not initialize data
			DbManager manager = new DbManager(this, false);
			SQLiteDatabase db = manager.getWritableDatabase();

			try {
				// beginTransaction
				db.beginTransaction();

				// Loop entities
				while (data.readNextHeader()) {
					// Key
					String key = data.getKey();

					// dataBytes
					int size = data.getDataSize();
					byte[] dataBytes = new byte[size];
					data.readEntityData(dataBytes, 0, size);

					this.helper.restoreKeyData(db, key, dataBytes);
				}

				// setTransactionSuccessful
				db.setTransactionSuccessful();

				BackupUtils.notifyBackupRestored(this, R.drawable.ic_notification_phrasebuilder, R.string.app_name, MainActivity.class);

			} catch (IOException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new IOException(ex.getMessage(), ex);
			} finally {
				// endTransaction
				db.endTransaction();

				manager.close();
			}
		}
	}
}
