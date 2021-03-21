package com.appslandia.phrasebuilder;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.core.loaders.AsyncLoader;
import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.PreferenceUtils;
import com.appslandia.core.utils.ServiceResults;
import com.appslandia.core.utils.ViewUtils;
import com.appslandia.core.views.AbstractListFragment;
import com.appslandia.core.views.ActionsDialog;
import com.appslandia.core.views.ActivityImpl;
import com.appslandia.core.views.HelpActivity;
import com.appslandia.core.views.LeoSpinner;
import com.appslandia.core.views.ListViewPos;
import com.appslandia.core.views.OnItemSelectedListenerImpl;
import com.appslandia.core.views.TaskFragment;
import com.appslandia.phrasebuilder.entities.Language;
import com.appslandia.phrasebuilder.entities.Phrase;
import com.appslandia.phrasebuilder.sqlite.DbManager;
import com.appslandia.phrasebuilder.sqlite.LanguageDao;
import com.appslandia.phrasebuilder.sqlite.PhraseDao;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class PhraseTrashActivity extends ActivityImpl implements PhraseTrashAdapter.Callbacks, TaskFragment.Callbacks, ActionsDialog.Callbacks {

	// Preferences
	public static final String PREFERENCE_ID = PhraseTrashActivity.class.getSimpleName();
	public static final String PREFERENCE_LANGUAGE_ID = PREFERENCE_ID + "_languageId";

	// Dialogs
	public static final int DIALOG_RESTORE_PHRASE = 1;
	public static final int DIALOG_DELETE_FOREVER_PHRASE = 2;
	public static final int DIALOG_EMPTY_TRASH = 3;

	// Tasks
	public static final int TASK_RESTORE_PHRASE = 1;
	public static final int TASK_DELETE_FOREVER_PHRASE = 2;
	public static final int TASK_EMPTY_TRASH = 3;

	// taskFragment
	TaskFragment taskFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phrase_trash_activity);

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

	int getParamLanguageId() {
		return PreferenceUtils.getInt(this, PREFERENCE_ID, PREFERENCE_LANGUAGE_ID, 0);
	}

	@Override
	protected void onPause() {
		if (isFinishing()) {
			synchronized (BackupUtils.MUTEX) {
				saveActivityPrefs();
			}
		}
		super.onPause();
	}

	void saveActivityPrefs() {
		synchronized (BackupUtils.MUTEX) {
			PreferenceUtils.savePreference(this, PREFERENCE_ID, PREFERENCE_LANGUAGE_ID, (int) getPlaceholderFragment().languageSpinner.getSelectedItemId());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.phrase_trash, menu);
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
		intent.putExtra(HelpActivity.INTENT_SECTION_ID, "manageTrash");
		startActivity(intent);
	}

	//
	// -------------------- Handle ConfirmDialog --------------------
	//

	@Override
	public void doPositiveClick(ActionsDialog dlg) {
		int dialogId = dlg.getDialogId();

		if (dialogId == DIALOG_RESTORE_PHRASE) {

			// @formatter:off
			taskFragment.executeLoading(TASK_RESTORE_PHRASE, 
					dlg.getInputArgs().getInt(ActionsDialog.ARGUMENT_1), 
					dlg.getInputArgs().getInt(ActionsDialog.ARGUMENT_2));
			
			// @formatter:on
			return;
		}
		if (dialogId == DIALOG_DELETE_FOREVER_PHRASE) {
			taskFragment.executeLoading(TASK_DELETE_FOREVER_PHRASE, dlg.getInputArgs().getInt(ActionsDialog.ARGUMENT_1));
			return;
		}
		if (dialogId == DIALOG_EMPTY_TRASH) {
			taskFragment.executeLoading(TASK_EMPTY_TRASH, dlg.getInputArgs().getInt(ActionsDialog.ARGUMENT_1));
			return;
		}
	}

	@Override
	public void doNegativeClick(ActionsDialog dlg) {
	}

	void confirmDialog(int dialogId, int messageId, Phrase phrase) {
		ActionsDialog dlg = new ActionsDialog();

		dlg.setDialogId(dialogId);
		dlg.setMessage(getString(messageId, PhraseUtils.truncatePhrase(phrase.phrase_text)));
		dlg.setPositiveText(getString(R.string.button_yes));
		dlg.setNegativeText(getString(R.string.button_no));

		dlg.getInputArgs().putInt(ActionsDialog.ARGUMENT_1, phrase._id);
		if (dialogId == DIALOG_RESTORE_PHRASE) {
			dlg.getInputArgs().putInt(ActionsDialog.ARGUMENT_2, phrase.language_id);
		}
		dlg.show(getFragmentManager());
	}

	//
	// -------------------- Handle Adapter Actions --------------------
	//

	@Override
	public void onAdapterRestorePhrase(Phrase phrase) {
		confirmDialog(DIALOG_RESTORE_PHRASE, R.string.message_confirm_restore_phrase, phrase);
	}

	@Override
	public void onAdapterDeleteForeverPhrase(Phrase phrase) {
		confirmDialog(DIALOG_DELETE_FOREVER_PHRASE, R.string.message_confirm_delete_forever_phrase, phrase);
	}

	public void onEmptyTrashSelected(int languageId) {
		ActionsDialog dlg = new ActionsDialog();

		dlg.setDialogId(DIALOG_EMPTY_TRASH);
		dlg.setMessage(getString(R.string.message_confirm_empty_trash));
		dlg.setPositiveText(getString(R.string.button_yes));
		dlg.setNegativeText(getString(R.string.button_no));

		dlg.getInputArgs().putInt(ActionsDialog.ARGUMENT_1, languageId);
		dlg.show(getFragmentManager());
	}

	//
	// -------------------- onTaskExecute --------------------
	//

	int restorePhrase(int phraseId, int languageId) {
		PhraseDao.restore(DbManager.openWrite(this), phraseId, System.currentTimeMillis());
		return ServiceResults.SUCCESS;
	}

	@Override
	public Object onTaskExecute(int taskId, Object[] params) {
		if (taskId == TASK_RESTORE_PHRASE) {
			return restorePhrase((Integer) params[0], (Integer) params[1]);
		}
		if (taskId == TASK_DELETE_FOREVER_PHRASE) {
			int phraseId = (Integer) params[0];
			PhraseDao.deleteForever(DbManager.openWrite(this), phraseId);
			return null;
		}
		if (taskId == TASK_EMPTY_TRASH) {
			int languageId = (Integer) params[0];
			PhraseDao.deleteTrash(DbManager.openWrite(this), languageId);
			return null;
		}
		return null;
	}

	//
	// -------------------- onTaskExecuted --------------------
	//

	@Override
	public void onTaskExecuted(int taskId, Object result) {
		if (taskId == TASK_RESTORE_PHRASE) {
			int bizResult = (Integer) result;
			if (bizResult == ServiceResults.SUCCESS) {
				Toast.makeText(this, getString(R.string.message_restored_phrase_successfully), Toast.LENGTH_SHORT).show();
				refreshPhrases();
			}
			return;
		}
		if (taskId == TASK_DELETE_FOREVER_PHRASE) {
			Toast.makeText(this, getString(R.string.message_deleted_forever_phrase_successfully), Toast.LENGTH_SHORT).show();
			refreshPhrases();
			return;
		}
		if (taskId == TASK_EMPTY_TRASH) {
			Toast.makeText(this, getString(R.string.message_empty_trash_successfully), Toast.LENGTH_SHORT).show();
			refreshPhrases();
			return;
		}
	}

	void refreshPhrases() {
		getPlaceholderFragment().refreshPhrases();
	}

	PlaceholderFragment getPlaceholderFragment() {
		return (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);
	}

	public static class PlaceholderFragment extends AbstractListFragment implements LoaderManager.LoaderCallbacks<Object> {

		// Loaders
		public static final int LOADER_LANGUAGES = 1;
		public static final int LOADER_PHRASES = 2;
		public static final String LOADER_PHRASES_PARAM_LANGUAGE_ID = "languageId";

		// Transient States
		public static final String STATE_LANGUAGE_ID = "languageId";
		public static final String STATE_LIST_VIEW_POS = "listViewPos";

		// Fields
		LeoSpinner languageSpinner;
		View emptyTrashBtn;

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

		void refreshPhrases() {
			this.setListShown(false);

			Bundle args = new Bundle(1);
			args.putInt(LOADER_PHRASES_PARAM_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
			getLoaderManager().restartLoader(LOADER_PHRASES, args, this);
		}

		@Override
		protected int getViewResourceId() {
			return R.layout.phrase_trash_fragment;
		}

		@Override
		protected void initializeView(View root) {

			// languageSpinner
			languageSpinner = (LeoSpinner) root.findViewById(R.id.phrase_trash_fragment_language_spinner);
			languageSpinner.setOnItemSelectedListener(new OnItemSelectedListenerImpl() {

				@Override
				public void doOnItemSelected(AdapterView<?> parent, View view, int position, long id) {
					refreshPhrases();
				}
			});

			// emptyTrashBtn
			emptyTrashBtn = root.findViewById(R.id.phrase_trash_fragment_empty_button);
			emptyTrashBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					int languageId = (int) languageSpinner.getSelectedItemId();
					((PhraseTrashActivity) getActivity()).onEmptyTrashSelected(languageId);
				}
			});
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);

			outState.putInt(STATE_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
			outState.putParcelable(STATE_LIST_VIEW_POS, this.createListViewPos());
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			// Empty Message
			setEmptyText(getString(R.string.message_no_phrases));

			// List Divider
			ViewUtils.setTransparentDivider(mList, 8); // 8dp

			// languageSpinner
			ArrayAdapterImpl<Language> languagesAdapter = new ArrayAdapterImpl<Language>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<Language>());
			languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			languageSpinner.setAdapter(languagesAdapter);

			// phrasesAdapter
			PhraseTrashAdapter phrasesAdapter = new PhraseTrashAdapter(getActivity(), R.layout.phrase_trash_activity_list_item, new ArrayList<Phrase>());
			setListAdapter(phrasesAdapter);
			setListShown(false);

			// Handle savedInstanceState
			if (savedInstanceState == null) {
				int languageId = ((PhraseTrashActivity) getActivity()).getParamLanguageId();
				languageSpinner.setTag(languageId);
			} else {
				// languageId
				int languageId = savedInstanceState.getInt(STATE_LANGUAGE_ID);
				languageSpinner.setTag(languageId);

				// ListViewPos
				ListViewPos pos = savedInstanceState.getParcelable(STATE_LIST_VIEW_POS);
				this.mList.setSelectionFromTop(pos.getPosition(), pos.getTopY());
			}

			// Load languages
			getLoaderManager().initLoader(LOADER_LANGUAGES, null, this);

		}

		@Override
		public Loader<Object> onCreateLoader(int id, final Bundle args) {
			if (id == LOADER_LANGUAGES) {
				Loader<Object> loader = new AsyncLoader<Object>(getActivity()) {
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
			} else {
				Loader<Object> loader = new AsyncLoader<Object>(getActivity()) {
					@Override
					protected Object loadData() {
						int languageId = args.getInt(LOADER_PHRASES_PARAM_LANGUAGE_ID);
						return PhraseDao.loadDeletedPhrases(DbManager.openRead(getContext()), languageId);
					}
				};
				return loader;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onLoadFinished(Loader<Object> loader, Object data) {
			int id = loader.getId();
			if (id == LOADER_LANGUAGES) {
				List<Language> languages = (List<Language>) data;

				boolean updateObjects = languageSpinner.checkObjects(languages);
				if (updateObjects) {
					languageSpinner.setObjects(languages);

					// Language selection
					Integer languageId = (Integer) languageSpinner.getTag();
					if (languageSpinner.setSelectionItemId(languageId) == false) {
						languageSpinner.setSelection(0);
					}

					languageSpinner.notifyDataSetChanged();

					// Load labels
					Bundle args = new Bundle(1);
					args.putInt(LOADER_PHRASES_PARAM_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
					getLoaderManager().initLoader(LOADER_PHRASES, args, this);

				} else {
					updateListShown(true);
				}
			} else {
				// Loader Phrases
				List<Phrase> phrases = (List<Phrase>) data;
				boolean updateObjects = this.checkObjects(phrases);
				if (updateObjects) {
					setObjects(phrases);

					this.mAdapter.notifyDataSetChanged();
					emptyTrashBtn.setEnabled(phrases.isEmpty() == false);
				}
				updateListShown(true);
			}
		}

		@Override
		public void onLoaderReset(Loader<Object> loader) {
			int id = loader.getId();
			if (id == LOADER_LANGUAGES) {
				languageSpinner.setObjects(null);
			} else {

				// Loader Phrases
				setObjects(null);
			}
		}
	}
}
