package com.appslandia.phrasebuilder;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.core.loaders.AsyncLoader;
import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.PreferenceUtils;
import com.appslandia.core.views.ActionsDialog;
import com.appslandia.core.views.ActivityImpl;
import com.appslandia.core.views.DialogImpl;
import com.appslandia.core.views.HelpActivity;
import com.appslandia.core.views.LeoSpinner;
import com.appslandia.core.views.OnItemSelectedListenerImpl;
import com.appslandia.core.views.TaskFragment;
import com.appslandia.core.views.YesConfirmDialog;
import com.appslandia.phrasebuilder.entities.Label;
import com.appslandia.phrasebuilder.entities.Language;
import com.appslandia.phrasebuilder.sqlite.DbManager;
import com.appslandia.phrasebuilder.sqlite.LabelDao;
import com.appslandia.phrasebuilder.sqlite.LanguageDao;
import com.appslandia.phrasebuilder.sqlite.PhraseLabelDao;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class LabelListActivity extends ActivityImpl implements LabelListItemCallbacks, LabelEditDialog.Callbacks, TaskFragment.Callbacks, YesConfirmDialog.Callbacks, LoaderManager.LoaderCallbacks<Object> {

	// Callback
	public static interface LabelChangedCallbacks {

		void loadLabels();
	}

	// Preferences
	public static final String PREFERENCE_ID = LabelListActivity.class.getSimpleName();
	public static final String PREFERENCE_LANGUAGE_ID = PREFERENCE_ID + "_languageId";

	// Dialogs
	public static final int DIALOG_ADAPTER_DELETE_LABEL = 1;
	public static final int DIALOG_DIALOG_DELETE_LABEL = 2;

	// Tasks
	public static final int TASK_SAVE_LABEL = 1;
	public static final int TASK_ADAPTER_DELETE_LABEL = 2;
	public static final int TASK_DIALOG_DELETE_LABEL = 3;
	public static final int TASK_DIALOG_CHECK_LABEL = 4;

	// Loaders
	public static final int LOADER_LANGUAGES = 1;

	// Transients
	public static final String STATE_LANGUAGE_ID = "languageId";
	public static final String STATE_SIMPLE_VIEW_ACTIVE = "simpleViewActive";

	// taskFragment
	TaskFragment taskFragment;

	// Views
	LeoSpinner languageSpinner;
	View simpleBtn, detailsBtn;

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (isFinishing()) {
			saveActivityPrefs();
		}
		super.onPause();
	}

	void saveActivityPrefs() {
		synchronized (BackupUtils.MUTEX) {
			PreferenceUtils.savePreference(this, PREFERENCE_ID, PREFERENCE_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
		outState.putBoolean(STATE_SIMPLE_VIEW_ACTIVE, languageSpinner.getVisibility() == View.INVISIBLE);

		super.onSaveInstanceState(outState);
	}

	int getParamLanguageId() {
		return PreferenceUtils.getInt(this, PREFERENCE_ID, PREFERENCE_LANGUAGE_ID, 0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.label_list_activity);

		// Initialize ActionBar
		ActivityUtils.initActionBarUp(getActionBar());

		// Initialize Activity
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.ActivityListBackgroundColor));

		// simpleBtn
		simpleBtn = findViewById(R.id.label_list_activity_simpleview_button);
		simpleBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				languageSpinner.setVisibility(View.INVISIBLE);
				simpleBtn.setBackgroundResource(R.drawable.libs_bg_view_selected);
				detailsBtn.setBackgroundResource(R.drawable.libs_bg_view_button);

				getFragmentManager().beginTransaction().replace(R.id.label_list_activity_container, new LabelListFragment1()).commit();
			}
		});

		final boolean simpleView = (savedInstanceState != null) ? savedInstanceState.getBoolean(STATE_SIMPLE_VIEW_ACTIVE) : (true);
		if (simpleView) {
			simpleBtn.setBackgroundResource(R.drawable.libs_bg_view_selected);
		} else {
			simpleBtn.setBackgroundResource(R.drawable.libs_bg_view_button);
		}

		// detailsBtn
		detailsBtn = findViewById(R.id.label_list_activity_detailsview_button);
		detailsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				languageSpinner.setVisibility(View.VISIBLE);
				simpleBtn.setBackgroundResource(R.drawable.libs_bg_view_button);
				detailsBtn.setBackgroundResource(R.drawable.libs_bg_view_selected);

				showDetailsFragment();
			}
		});

		// languageSpinner
		languageSpinner = (LeoSpinner) findViewById(R.id.label_list_activity_language_spinner);
		ArrayAdapterImpl<Language> languagesAdapter = new ArrayAdapterImpl<Language>(this, android.R.layout.simple_spinner_item, new ArrayList<Language>());
		languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		languageSpinner.setAdapter(languagesAdapter);
		languageSpinner.setOnItemSelectedListener(new OnItemSelectedListenerImpl() {

			@Override
			public void doOnItemSelected(AdapterView<?> parent, View view, int position, long id) {
				showDetailsFragment();
			}
		});

		if (simpleView == false) {
			languageSpinner.setVisibility(View.VISIBLE);
			detailsBtn.setBackgroundResource(R.drawable.libs_bg_view_selected);
		} else {
			languageSpinner.setVisibility(View.INVISIBLE);
			detailsBtn.setBackgroundResource(R.drawable.libs_bg_view_button);
		}

		// Handle savedInstanceState
		if (savedInstanceState == null) {
			languageSpinner.setTag(getParamLanguageId());
		} else {
			languageSpinner.setTag(savedInstanceState.getInt(STATE_LANGUAGE_ID));
		}

		// Disabled buttons
		simpleBtn.setEnabled(false);
		detailsBtn.setEnabled(false);

		// Load languages
		getLoaderManager().initLoader(LOADER_LANGUAGES, null, this);

		// LabelListFragment1
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.label_list_activity_container, new LabelListFragment1()).commit();
		}

		// TaskFragment
		taskFragment = (TaskFragment) getFragmentManager().findFragmentByTag(TaskFragment.FRAGMENT_TAG);
		if (taskFragment == null) {
			taskFragment = new TaskFragment();
			getFragmentManager().beginTransaction().add(taskFragment, TaskFragment.FRAGMENT_TAG).commit();
		}
	}

	void showDetailsFragment() {
		LabelListFragment2 fragment = new LabelListFragment2();
		fragment.getInputArgs().putInt(LabelListFragment2.ARGUMENT_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
		getFragmentManager().beginTransaction().replace(R.id.label_list_activity_container, fragment).commit();
	}

	//
	// -------------------- Handle Loaders --------------------
	//

	@Override
	public Loader<Object> onCreateLoader(int id, final Bundle args) {
		Loader<Object> loader = new AsyncLoader<Object>(this) {
			@Override
			protected Object loadData() {
				List<Language> languages = LanguageDao.queryLanguages(DbManager.openRead(getContext()));
				if (languages.isEmpty()) {
					languages.add(PhraseUtils.getNoLanguage(getLoaderResources()));
				}
				return languages;
			}
		};
		return loader;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLoadFinished(Loader<Object> loader, Object data) {
		List<Language> languages = (List<Language>) data;
		boolean updateObjects = languageSpinner.checkObjects(languages);
		if (updateObjects) {
			// Update languages
			languageSpinner.setObjects(languages);

			// Language selection
			Integer languageId = (Integer) languageSpinner.getTag();
			if (languageSpinner.setSelectionItemId(languageId) == false) {
				languageSpinner.setSelection(0);
			}

			// Notify languages updated
			languageSpinner.notifyDataSetChanged();

			simpleBtn.setEnabled(true);
			detailsBtn.setEnabled(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {
		languageSpinner.setObjects(null);
	}

	//
	// -------------------- Handle Menus --------------------
	//

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.label_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		}
		if (id == R.id.action_new_label) {
			onActionNewLabelSelected();
			return true;
		}
		if (id == R.id.action_help) {
			onActionHelp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	void onActionNewLabelSelected() {
		LabelEditDialog dlg = new LabelEditDialog();
		dlg.show(getFragmentManager());
	}

	void onActionHelp() {
		Intent intent = new Intent(this, HelpActivityImpl.class);
		intent.putExtra(HelpActivity.INTENT_SECTION_ID, "manageLabels");
		startActivity(intent);
	}

	@Override
	public void doNoConfirm(YesConfirmDialog dlg) {
	}

	@Override
	public void doYesConfirm(YesConfirmDialog dlg) {
		if (dlg.getDialogId() == DIALOG_ADAPTER_DELETE_LABEL) {
			taskFragment.executeLoading(TASK_ADAPTER_DELETE_LABEL, dlg.getInputArgs().getInt(ActionsDialog.ARGUMENT_1));

		} else if (dlg.getDialogId() == DIALOG_DIALOG_DELETE_LABEL) {
			taskFragment.executeLoading(TASK_DIALOG_DELETE_LABEL, dlg.getInputArgs().getInt(ActionsDialog.ARGUMENT_1));
		}
	}

	void confirmDelete(Label label, int dialogId) {
		YesConfirmDialog dlg = new YesConfirmDialog();
		dlg.setMessage(getString(R.string.message_confirm_delete_label, label.name));
		dlg.setIntArgument1(label._id);

		dlg.setDialogId(dialogId).show(getFragmentManager());
	}

	//
	// -------------------- Handle Adapter Actions --------------------
	//

	@Override
	public void onAdapterClickLabel(Label Label) {
		LabelEditDialog dlg = new LabelEditDialog();
		Bundle args = new Bundle(1);
		args.putParcelable(LabelEditDialog.DIALOG_ARG_LABEL, Label);
		dlg.setArguments(args);
		dlg.show(getFragmentManager());
	}

	@Override
	public void onAdapterDeleteLabel(Label label) {
		confirmDelete(label, DIALOG_ADAPTER_DELETE_LABEL);
	}

	@Override
	public void onAdapterTestLabel(Label label) {
		Intent intent = new Intent(this, PhraseTestInputsActivity.class);
		intent.putExtra(PhraseTestInputsActivity.INTENT_PARAM_LABEL, label);
		startActivity(intent);
	}

	//
	// -------------------- Handle Dialog Actions --------------------
	//

	@Override
	public void onDialogSaveLabel(Label label) {
		taskFragment.executeLoading(TASK_SAVE_LABEL, label);
	}

	@Override
	public void onDialogDeleteLabel(Label label) {
		confirmDelete(label, DIALOG_DIALOG_DELETE_LABEL);
	}

	@Override
	public void onDialogCheckLabel(String sName, Integer labelId) {
		taskFragment.execute(TASK_DIALOG_CHECK_LABEL, sName, labelId);
	}

	//
	// -------------------- Handle DataAccess --------------------
	//

	void deleteLabel(int labelId) {
		SQLiteDatabase db = DbManager.openWrite(this);
		db.beginTransaction();
		try {
			PhraseLabelDao.deleteByLabel(db, labelId);
			LabelDao.delete(db, labelId);
			db.setTransactionSuccessful();

		} finally {
			db.endTransaction();
		}
	}

	@Override
	public Object onTaskExecute(int taskId, Object[] params) {
		if (taskId == TASK_SAVE_LABEL) {
			Label label = (Label) params[0];
			if (label._id == 0) {
				LabelDao.insert(DbManager.openWrite(this), label);
			} else {
				LabelDao.update(DbManager.openWrite(this), label);
			}
			return null;
		}
		if (taskId == TASK_ADAPTER_DELETE_LABEL || taskId == TASK_DIALOG_DELETE_LABEL) {
			int labelId = (Integer) params[0];
			deleteLabel(labelId);
			return null;
		}
		if (taskId == TASK_DIALOG_CHECK_LABEL) {
			String sName = (String) params[0];
			Integer labelId = (Integer) params[1];

			return LabelDao.checkLabel(DbManager.openWrite(this), sName, labelId);
		}
		return null;
	}

	@Override
	public void onTaskExecuted(int taskId, Object result) {
		if (taskId == TASK_SAVE_LABEL) {
			DialogImpl.dismiss(getFragmentManager(), LabelEditDialog.class);
			Toast.makeText(this, getString(R.string.message_saved_label_successfully), Toast.LENGTH_SHORT).show();

			refreshLabels();
			return;
		}
		if (taskId == TASK_ADAPTER_DELETE_LABEL) {
			Toast.makeText(this, getString(R.string.message_deleted_label_successfully), Toast.LENGTH_SHORT).show();
			refreshLabels();
			return;
		}
		if (taskId == TASK_DIALOG_DELETE_LABEL) {
			DialogImpl.dismiss(getFragmentManager(), LabelEditDialog.class);
			Toast.makeText(this, getString(R.string.message_deleted_label_successfully), Toast.LENGTH_SHORT).show();
			refreshLabels();
			return;
		}
		if (taskId == TASK_DIALOG_CHECK_LABEL) {
			LabelEditDialog dialog = LabelEditDialog.find(getFragmentManager(), LabelEditDialog.class);
			dialog.onCheckLabelResult((Boolean) result);
			return;
		}
	}

	void refreshLabels() {
		LabelChangedCallbacks fragment = (LabelChangedCallbacks) getFragmentManager().findFragmentById(R.id.label_list_activity_container);
		((LabelChangedCallbacks) fragment).loadLabels();
	}
}