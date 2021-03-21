package com.appslandia.phrasebuilder;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.loaders.AsyncLoader;
import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.core.utils.ViewUtils;
import com.appslandia.core.views.AbstractListFragment;
import com.appslandia.core.views.ActivityImpl;
import com.appslandia.core.views.DialogImpl;
import com.appslandia.core.views.HelpActivity;
import com.appslandia.core.views.TaskFragment;
import com.appslandia.core.views.YesConfirmDialog;
import com.appslandia.phrasebuilder.entities.Language;
import com.appslandia.phrasebuilder.sqlite.DbManager;
import com.appslandia.phrasebuilder.sqlite.LanguageDao;
import com.appslandia.phrasebuilder.sqlite.PhraseDao;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class LanguageListActivity extends ActivityImpl implements LanguageListAdapter.Callbacks, LanguageEditDialog.Callbacks, TaskFragment.Callbacks, YesConfirmDialog.Callbacks {

	// Dialogs
	public static final int DIALOG_ADAPTER_DELETE_LANGUAGE = 1;
	public static final int DIALOG_DIALOG_DELETE_LANGUAGE = 2;

	// Tasks
	public static final int TASK_SAVE_LANGUAGE = 1;
	public static final int TASK_ADAPTER_DELETE_LANGUAGE = 2;
	public static final int TASK_DIALOG_DELETE_LANGUAGE = 3;
	public static final int TASK_DIALOG_CHECK_LANGUAGE = 4;

	// taskFragment
	TaskFragment taskFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.language_list_activity);

		// Initialize ActionBar
		ActivityUtils.initActionBarUp(getActionBar());

		// Initialize Activity
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.ActivityListBackgroundColor));

		// PlaceholderFragment
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}

		// TaskFragment
		taskFragment = (TaskFragment) getFragmentManager().findFragmentByTag(TaskFragment.FRAGMENT_TAG);
		if (taskFragment == null) {
			taskFragment = new TaskFragment();
			getFragmentManager().beginTransaction().add(taskFragment, TaskFragment.FRAGMENT_TAG).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.language_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		}
		if (id == R.id.action_new_language) {
			onNewLanguageSelected();
			return true;
		}
		if (id == R.id.action_help) {
			onActionHelp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	void onNewLanguageSelected() {
		LanguageEditDialog dlg = new LanguageEditDialog();
		dlg.show(getFragmentManager());
	}

	void onActionHelp() {
		Intent intent = new Intent(this, HelpActivityImpl.class);
		intent.putExtra(HelpActivity.INTENT_SECTION_ID, "manageLanguages");
		startActivity(intent);
	}

	//
	// -------------------- Handle Adapter Actions --------------------
	//

	public void onAdapterClickLanguage(Language Language) {
		LanguageEditDialog dlg = new LanguageEditDialog();
		Bundle args = new Bundle(1);
		args.putParcelable(LanguageEditDialog.DIALOG_ARG_LANGUAGE, Language);
		dlg.setArguments(args);
		dlg.show(getFragmentManager());
	}

	@Override
	public void onAdapterDeleteLanguage(Language language) {
		confirmDelete(language, DIALOG_ADAPTER_DELETE_LANGUAGE);
	}

	@Override
	public void onAdapterTestLanguage(int languageId) {
		Intent intent = new Intent(this, PhraseTestInputsActivity.class);
		intent.putExtra(PhraseTestInputsActivity.INTENT_PARAM_LANGUAGE_ID, languageId);
		startActivity(intent);
	}

	@Override
	public void doNoConfirm(YesConfirmDialog dlg) {
	}

	@Override
	public void doYesConfirm(YesConfirmDialog dlg) {
		if (dlg.getDialogId() == DIALOG_ADAPTER_DELETE_LANGUAGE) {
			taskFragment.executeLoading(TASK_ADAPTER_DELETE_LANGUAGE, dlg.getIntArgument1());

		} else if (dlg.getDialogId() == DIALOG_DIALOG_DELETE_LANGUAGE) {
			taskFragment.executeLoading(TASK_DIALOG_DELETE_LANGUAGE, dlg.getIntArgument1());
		}
	}

	void confirmDelete(Language language, int dialogId) {
		YesConfirmDialog dlg = new YesConfirmDialog();
		dlg.setMessage(getString(R.string.message_confirm_delete_language, language.name));
		dlg.setIntArgument1(language._id);

		dlg.setDialogId(dialogId).show(getFragmentManager());
	}

	//
	// -------------------- Handle Dialog Actions --------------------
	//

	@Override
	public void onDialogSaveLanguage(Language language) {
		taskFragment.executeLoading(TASK_SAVE_LANGUAGE, language);
	}

	@Override
	public void onDialogDeleteLanguage(Language language) {
		confirmDelete(language, DIALOG_DIALOG_DELETE_LANGUAGE);
	}

	@Override
	public void onDialogCheckLanguage(String name, Integer languageId) {
		taskFragment.execute(TASK_DIALOG_CHECK_LANGUAGE, name, languageId);
	}

	void deleteLanguage(int languageId) {
		SQLiteDatabase db = DbManager.openWrite(this);
		db.beginTransaction();
		try {
			PhraseDao.deleteByLanguage(db, languageId);
			LanguageDao.delete(db, languageId);

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public Object onTaskExecute(int taskId, Object[] params) {
		if (taskId == TASK_SAVE_LANGUAGE) {
			Language language = (Language) params[0];
			if (language._id == 0) {
				LanguageDao.insert(DbManager.openWrite(this), language);
			} else {
				LanguageDao.update(DbManager.openWrite(this), language);
			}
			return null;
		}
		if (taskId == TASK_ADAPTER_DELETE_LANGUAGE || taskId == TASK_DIALOG_DELETE_LANGUAGE) {
			int languageId = (Integer) params[0];
			deleteLanguage(languageId);
			return null;
		}
		if (taskId == TASK_DIALOG_CHECK_LANGUAGE) {
			String name = (String) params[0];
			Integer languageId = (Integer) params[1];

			return LanguageDao.checkLanguage(DbManager.openWrite(this), name, languageId);
		}
		return null;
	}

	@Override
	public void onTaskExecuted(int taskId, Object result) {
		if (taskId == TASK_SAVE_LANGUAGE) {
			DialogImpl.dismiss(getFragmentManager(), LanguageEditDialog.class);
			Toast.makeText(this, getString(R.string.message_saved_language_successfully), Toast.LENGTH_SHORT).show();
			refreshLanguages();
			return;
		}
		if (taskId == TASK_ADAPTER_DELETE_LANGUAGE) {
			Toast.makeText(this, getString(R.string.message_deleted_language_successfully), Toast.LENGTH_SHORT).show();
			refreshLanguages();
			return;
		}
		if (taskId == TASK_DIALOG_DELETE_LANGUAGE) {
			DialogImpl.dismiss(getFragmentManager(), LanguageEditDialog.class);
			Toast.makeText(this, getString(R.string.message_deleted_language_successfully), Toast.LENGTH_SHORT).show();
			refreshLanguages();
			return;
		}
		if (taskId == TASK_DIALOG_CHECK_LANGUAGE) {
			LanguageEditDialog dialog = LanguageEditDialog.find(getFragmentManager(), LanguageEditDialog.class);
			dialog.onCheckLanguageResult((Boolean) result);
			return;
		}
	}

	void refreshLanguages() {
		PlaceholderFragment fragment = (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);
		fragment.refreshLanguages();
	}

	public static class PlaceholderFragment extends AbstractListFragment implements LoaderManager.LoaderCallbacks<Object> {

		// Loaders
		public static final int LOADER_LANGUAGES = 1;

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

		void refreshLanguages() {
			this.setListShown(false);
			getLoaderManager().restartLoader(LOADER_LANGUAGES, null, this);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Language language = (Language) l.getItemAtPosition(position);
			((LanguageListActivity) getActivity()).onAdapterClickLanguage(language);
		}

		@Override
		protected int getViewResourceId() {
			return R.layout.language_list_fragment;
		}

		@Override
		protected void initializeView(View root) {
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			// Empty Message
			setEmptyText(getString(R.string.message_no_languages));

			// List Divider
			ViewUtils.setTransparentDivider(mList, 8); // 8dp

			// languagesAdapter
			LanguageListAdapter languagesAdapter = new LanguageListAdapter(getActivity(), R.layout.language_list_activity_list_item, new ArrayList<Language>());
			setListAdapter(languagesAdapter);
			setListShown(false);

			// Load languages
			getLoaderManager().initLoader(LOADER_LANGUAGES, null, this);
		}

		@Override
		public Loader<Object> onCreateLoader(int id, Bundle args) {
			Loader<Object> loader = new AsyncLoader<Object>(getActivity()) {
				@Override
				protected Object loadData() {
					return LanguageDao.queryStat(DbManager.openRead(getContext()));
				}
			};
			return loader;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onLoadFinished(Loader<Object> loader, Object data) {
			List<Language> objects = (List<Language>) data;
			boolean updateObjects = this.checkObjects(objects);
			if (updateObjects) {
				// Update languages
				setObjects(objects);

				// Notify changed
				this.mAdapter.notifyDataSetChanged();
			}
			updateListShown(true);
		}

		@Override
		public void onLoaderReset(Loader<Object> loader) {
			setObjects(null);
		}
	}
}
