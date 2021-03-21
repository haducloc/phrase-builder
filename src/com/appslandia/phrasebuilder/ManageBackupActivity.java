package com.appslandia.phrasebuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.core.adapters.SimpleItem;
import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.DateUtils;
import com.appslandia.core.utils.PreferenceUtils;
import com.appslandia.core.views.ActivityImpl;
import com.appslandia.core.views.HelpActivity;
import com.appslandia.core.views.LeoSpinner;
import com.appslandia.core.views.LeoTimeEditor;
import com.appslandia.core.views.LeoTimePicker;
import com.appslandia.core.views.OnItemSelectedListenerImpl;
import com.appslandia.core.views.TaskFragment2;
import com.appslandia.core.views.YesConfirmDialog;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ManageBackupActivity extends ActivityImpl implements LeoTimePicker.Callbacks {

	// Preferences
	public static final String PREFERENCE_ID = ManageBackupActivity.class.getSimpleName();

	public static final String PREFERENCE_LAST_BACKUP_TIMESTAMP = "lastBackupTimestamp";

	public static final String PREFERENCE_REQUEST_BACKUP_ON_DAY = "requestBackupOnDay";
	public static final String PREFERENCE_REQUEST_BACKUP_AT_HOUR = "requestBackupAtHour";
	public static final String PREFERENCE_REQUEST_BACKUP_AT_MIMUTE = "requestBackupAtMinute";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_backup_activity);

		// Initialize ActionBar
		initActionBar();

		// Initialize Activity
		initActivityProps();

		// PlaceholderFragment
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	protected void initActionBar() {
		ActivityUtils.initActionBarUp(getActionBar());
	}

	protected void initActivityProps() {
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.ActivityEditBackgroundColor));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.manage_backup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		}
		if (id == R.id.action_help) {
			onActionHelp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	void onActionHelp() {
		Intent intent = new Intent(this, HelpActivityImpl.class);
		intent.putExtra(HelpActivity.INTENT_SECTION_ID, "manageBackup");
		startActivity(intent);
	}

	@Override
	public void onTimeSet(int newHour, int newMinute) {
		// Save plan
		synchronized (BackupUtils.MUTEX) {
			Editor editor = getSharedPreferences(PREFERENCE_ID, Context.MODE_PRIVATE).edit();
			editor.putInt(PREFERENCE_REQUEST_BACKUP_AT_HOUR, newHour);
			editor.putInt(PREFERENCE_REQUEST_BACKUP_AT_MIMUTE, newMinute);
			editor.commit();
		}

		PlaceholderFragment fragment = getPlaceholderFragment();
		fragment.backupTimeEditor.setHourMinute(newHour, newMinute);
		fragment.setInexactRepeatingBackup();
	}

	PlaceholderFragment getPlaceholderFragment() {
		return (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);
	}

	public static class PlaceholderFragment extends Fragment implements YesConfirmDialog.Callbacks, TaskFragment2.Callbacks {

		// Tasks
		public static final int TASK_BACKUP = 1;
		public static final int TASK_RESTORE = 2;

		// Dialog
		public static final int DIALOG_BACKUP_CONFIRM = 1;
		public static final int DIALOG_RESTORE_CONFIRM = 2;

		final DateFormat timestampFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);

		// Views
		LeoSpinner backupDaySpinner;
		LeoTimeEditor backupTimeEditor;
		View restoreButton;
		TextView localBackupTimestampTextView;

		// BackupService
		BackupService backupService;

		@Override
		public void onDestroyView() {
			super.onDestroyView();
		}

		@Override
		public void onResume() {
			super.onResume();
		}

		@Override
		public void onPause() {
			super.onPause();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View formView = inflater.inflate(R.layout.manage_backup_fragment, container, false);

			// absTextView
			TextView absTextView = (TextView) formView.findViewById(R.id.manage_backup_fragment_abs_textview);
			absTextView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Integer num = (Integer) v.getTag();
					num = (num == null) ? (1) : (num.intValue() + 1);

					// Five times
					if (num == 5) {
						num = null;

						new BackupManager(getActivity()).dataChanged();
						Toast.makeText(getActivity(), R.string.message_backup_backup_requested_internal_successfully, Toast.LENGTH_SHORT).show();
					}
					v.setTag(num);
				}
			});

			// backupDaySpinner
			backupDaySpinner = (LeoSpinner) formView.findViewById(R.id.manage_backup_fragment_backup_day_spinner);
			ArrayAdapterImpl<SimpleItem> adapter = new ArrayAdapterImpl<SimpleItem>(getActivity(), android.R.layout.simple_spinner_item, DateUtils.getWeekDays(Locale.getDefault()));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			backupDaySpinner.setAdapter(adapter);

			// Preferences
			final SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCE_ID, Context.MODE_PRIVATE);

			// backupOnDay
			int backupOnDay = preferences.getInt(PREFERENCE_REQUEST_BACKUP_ON_DAY, BackupUtils.DEFAULT_BACKUP_REQUEST_ON_DAY);
			backupDaySpinner.setOnItemSelectedListener(new OnItemSelectedListenerImpl() {

				@Override
				public void doOnItemSelected(AdapterView<?> parent, View view, int position, long id) {
					onBackupDayChanged((int) id);
				}
			});
			backupDaySpinner.setSelectionItemId(backupOnDay);

			// backupTimeEditor
			int atHour = preferences.getInt(PREFERENCE_REQUEST_BACKUP_AT_HOUR, BackupUtils.DEFAULT_BACKUP_REQUEST_AT_HOUR);
			int atMinute = preferences.getInt(PREFERENCE_REQUEST_BACKUP_AT_MIMUTE, BackupUtils.DEFAULT_BACKUP_REQUEST_AT_MINUTE);

			backupTimeEditor = (LeoTimeEditor) formView.findViewById(R.id.manage_backup_fragment_backup_time_edittext);
			backupTimeEditor.setHourMinute(atHour, atMinute);
			backupTimeEditor.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					LeoTimePicker dlg = new LeoTimePicker();

					dlg.setHour(backupTimeEditor.getHour());
					dlg.setMinute(backupTimeEditor.getMinute());

					dlg.show(getFragmentManager());
				}
			});

			// absBackupTimestampTextView
			TextView absBackupTimestampTextView = (TextView) formView.findViewById(R.id.manage_backup_fragment_abs_lastbackup_textview);
			long backupTimestamp = preferences.getLong(PREFERENCE_LAST_BACKUP_TIMESTAMP, 0);
			if (backupTimestamp > 0) {
				absBackupTimestampTextView.setText(timestampFormat.format(new Date(backupTimestamp)));
			} else {
				absBackupTimestampTextView.setText(getString(R.string.message_backup_no_backup_yet));
			}

			// backupLocationTextView
			EditText backupLocationTextView = (EditText) formView.findViewById(R.id.manage_backup_fragment_externalbk_location_edittext);
			backupLocationTextView.setText(BackupLocalImpl.getBackupFile().getAbsolutePath());

			// localBackupTimestampTextView
			localBackupTimestampTextView = (TextView) formView.findViewById(R.id.manage_backup_fragment_externalbk_lastbackup_textview);
			localBackupTimestampTextView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// version >= M
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
							requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1000);
						}
					}
				}
			});

			// backupButton
			View backupButton = formView.findViewById(R.id.manage_backup_fragment_externalbk_backup_button);
			backupButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// version >= M
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
							requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 2000);
						} else {
							confirmDialog(DIALOG_BACKUP_CONFIRM, R.string.message_confirm_backup_file);
						}
					} else {
						confirmDialog(DIALOG_BACKUP_CONFIRM, R.string.message_confirm_backup_file);
					}
				}
			});

			// restoreButton
			restoreButton = formView.findViewById(R.id.manage_backup_fragment_externalbk_restore_button);
			restoreButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					confirmDialog(DIALOG_RESTORE_CONFIRM, R.string.message_confirm_restore_file);
				}
			});

			return formView;
		}

		@Override
		public void onStart() {
			super.onStart();

			boolean canRestore = this.tryDisplayLocalBackupTimestamp();
			restoreButton.setEnabled(canRestore);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			// BackupService
			backupService = (BackupService) getFragmentManager().findFragmentByTag(BackupService.FRAGMENT_TAG);
			if (backupService == null) {
				backupService = new BackupService();
				backupService.setHostFragment(this, 0);

				getFragmentManager().beginTransaction().add(backupService, BackupService.FRAGMENT_TAG).commit();
			}
		}

		@Override
		public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
			// From backupButton
			if (requestCode == 2000) {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					boolean canRestore = this.tryDisplayLocalBackupTimestamp();
					restoreButton.setEnabled(canRestore);

					confirmDialog(DIALOG_BACKUP_CONFIRM, R.string.message_confirm_backup_file);
				}
			}
			// From localBackupTimestampTextView
			if (requestCode == 1000) {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					boolean canRestore = this.tryDisplayLocalBackupTimestamp();
					restoreButton.setEnabled(canRestore);
				}
			}
		}

		void onBackupDayChanged(int newBackupDay) {
			synchronized (BackupUtils.MUTEX) {
				PreferenceUtils.savePreference(getActivity(), PREFERENCE_ID, PREFERENCE_REQUEST_BACKUP_ON_DAY, newBackupDay);
			}

			setInexactRepeatingBackup();
		}

		void setInexactRepeatingBackup() {
			Activity activity = getActivity();

			// Backup plan
			int backupOnDay = (int) backupDaySpinner.getSelectedItemId();
			int atHour = backupTimeEditor.getHour();
			int atMinute = backupTimeEditor.getMinute();

			Calendar triggerCal = DateUtils.getCalendar(backupOnDay, atHour, atMinute);

			// 3sec?
			if (triggerCal.getTimeInMillis() <= System.currentTimeMillis() + 3_000) {
				triggerCal.add(Calendar.DATE, BackupUtils.AUTO_REQUEST_BACKUP_INTERVAL_DAYS);
			}

			BackupUtils.createInexactRepeatingBackup(activity, BackupRequestReceiver.class, triggerCal.getTimeInMillis(), BackupUtils.AUTO_REQUEST_BACKUP_INTERVAL_MS);
			Toast.makeText(activity, getString(R.string.message_backup_next_backup_request_scheduled, timestampFormat.format(new Date(triggerCal.getTimeInMillis()))), Toast.LENGTH_SHORT).show();
		}

		//
		// -------------------- Backup/Restore --------------------
		//

		@Override
		public void doNoConfirm(YesConfirmDialog dlg) {
		}

		@Override
		public void doYesConfirm(YesConfirmDialog dlg) {
			if (dlg.getDialogId() == DIALOG_BACKUP_CONFIRM) {
				backupService.backup();

			} else if (dlg.getDialogId() == DIALOG_RESTORE_CONFIRM) {
				backupService.restore();
			}
		}

		void confirmDialog(int dialogId, int messageId) {
			YesConfirmDialog dlg = new YesConfirmDialog();
			dlg.setMessage(getString(messageId, BackupLocalImpl.BACKUP_DATA_FILE));
			dlg.setDialogId(dialogId).setHostFragment(this, 0).show(getFragmentManager());
		}

		boolean tryDisplayLocalBackupTimestamp() {
			// No backup?
			if (BackupLocalImpl.isBackupFileExists() == false) {
				localBackupTimestampTextView.setText(getString(R.string.message_backup_no_backup_yet));
			} else {
				// Parse backupTimestamp
				long backupTimestamp = BackupLocalImpl.tryGetBackupTimestamp();
				if (backupTimestamp > 0) {
					localBackupTimestampTextView.setText(timestampFormat.format(new Date(backupTimestamp)));
					return true;
				} else {
					// version >= M
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						// Backup error
						if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
							localBackupTimestampTextView.setText(getString(R.string.message_backup_allow_app_access_storage));
						} else {
							localBackupTimestampTextView.setText(getString(R.string.message_backup_error_backup_file));
						}
					} else {
						localBackupTimestampTextView.setText(getString(R.string.message_backup_error_backup_file));
					}
				}
			}
			return false;
		}

		@Override
		public void onTaskExecuted(int taskId, Object result) {
			if (taskId == TASK_BACKUP) {
				Boolean success = (Boolean) result;
				if (success) {
					Toast.makeText(getActivity(), R.string.message_backup_backup_file_successfully, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.message_backup_backup_file_failed, Toast.LENGTH_SHORT).show();
				}

				// tryDisplayLocalBackupTimestamp
				boolean canRestore = tryDisplayLocalBackupTimestamp();
				restoreButton.setEnabled(canRestore);
			} else {
				Boolean success = (Boolean) result;
				if (success) {
					Toast.makeText(getActivity(), R.string.message_backup_restore_file_successfully, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.message_backup_restore_file_failed, Toast.LENGTH_SHORT).show();
				}
			}
		}

		public final static class BackupService extends TaskFragment2 {

			void backup() {
				new BackupAsync().execute();
			}

			void restore() {
				new RestoreAsync().execute();
			}

			final class BackupAsync extends AsyncTaskImpl {

				public BackupAsync() {
					super(PlaceholderFragment.TASK_BACKUP, true);
				}

				@Override
				protected Object doInBackground(Object... params) {
					BackupLocalImpl backup = new BackupLocalImpl(context);
					try {
						backup.onBackup();
						return true;
					} catch (IOException ex) {
						return false;
					}
				}
			}

			final class RestoreAsync extends AsyncTaskImpl {

				public RestoreAsync() {
					super(PlaceholderFragment.TASK_RESTORE, true);
				}

				@Override
				protected Object doInBackground(Object... params) {
					BackupLocalImpl backup = new BackupLocalImpl(context);
					try {
						if (BackupLocalImpl.verifyBackupState()) {
							backup.onRestore();
							return true;
						}
						return false;
					} catch (IOException ex) {
						return false;
					}
				}
			}
		}
	}
}
