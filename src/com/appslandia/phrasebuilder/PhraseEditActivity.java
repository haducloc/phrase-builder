package com.appslandia.phrasebuilder;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.adapters.LabelListAdapter;
import com.appslandia.core.loaders.AsyncLoader;
import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.Bool;
import com.appslandia.core.utils.ObjectUtils;
import com.appslandia.core.utils.PreferenceUtils;
import com.appslandia.core.utils.ServiceResults;
import com.appslandia.core.utils.StringUtils;
import com.appslandia.core.utils.Validator;
import com.appslandia.core.utils.ViewUtils;
import com.appslandia.core.views.ActionsDialog;
import com.appslandia.core.views.ActivityImpl;
import com.appslandia.core.views.DialogImpl;
import com.appslandia.core.views.HelpActivity;
import com.appslandia.core.views.LabelEditLayout;
import com.appslandia.core.views.LayoutSizerImpl;
import com.appslandia.core.views.LeoEditTextVal;
import com.appslandia.core.views.TaskFragment;
import com.appslandia.core.views.TextWatcherImpl;
import com.appslandia.phrasebuilder.entities.Label;
import com.appslandia.phrasebuilder.entities.Language;
import com.appslandia.phrasebuilder.entities.Phrase;
import com.appslandia.phrasebuilder.sqlite.DbManager;
import com.appslandia.phrasebuilder.sqlite.LabelDao;
import com.appslandia.phrasebuilder.sqlite.LanguageDao;
import com.appslandia.phrasebuilder.sqlite.PhraseDao;
import com.appslandia.phrasebuilder.sqlite.PhraseLabelDao;
import com.appslandia.phrasebuilder.utils.LabelUtils;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class PhraseEditActivity extends ActivityImpl implements TaskFragment.Callbacks, LabelEditDialog2.Callbacks, ActionsDialog.Callbacks {

	// Parameters
	public static final String INTENT_PARAM_LANGUAGE_ID = "languageId";
	public static final String INTENT_PARAM_PHRASE = "phrase";

	// Preferences
	public static final String PREFERENCE_ID = PhraseEditActivity.class.getSimpleName();
	public static final String PREFERENCE_LANGUAGE_ID = PREFERENCE_ID + "_languageId";

	// Tasks
	public static final int TASK_NEW_PHRASE = 1;
	public static final int TASK_UPDATE_PHRASE = 2;
	public static final int TASK_DELETE_PHRASE = 3;

	public static final int TASK_DIALOG_NEW_LABEL = 4;
	public static final int TASK_DIALOG_CHECK_LABEL = 5;

	// taskFragment
	TaskFragment taskFragment;

	//
	// phraseChanged
	//
	public static int PHRASE_CHANGE_UNCHANGED = 0;
	public static int PHRASE_CHANGE_ADDED = 1;
	public static int PHRASE_CHANGE_DELETED = 2;
	public static int PHRASE_CHANGE_OTHERS = 3;

	public static int phraseChanged = PHRASE_CHANGE_UNCHANGED;

	protected void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(getUpdatePhrase() == null ? R.string.title_new_phrase : R.string.title_edit_phrase);
		ActivityUtils.initActionBarUp(actionBar);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phrase_edit_activity);

		// Initialize ActionBar
		initActionBar();

		// Initialize Activity
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.ActivityEditBackgroundColor));

		// savedInstanceState
		if (savedInstanceState == null) {
			PhraseEditActivity.phraseChanged = PhraseEditActivity.PHRASE_CHANGE_UNCHANGED;
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
	protected void onPause() {
		if (isFinishing()) {
			saveActivityPrefs();
		}
		super.onPause();
	}

	void saveActivityPrefs() {
		synchronized (BackupUtils.MUTEX) {
			PreferenceUtils.savePreference(this, PREFERENCE_ID, PREFERENCE_LANGUAGE_ID, (int) getPlaceholderFragment().languageSpinner.getSelectedItemId());
		}
	}

	Phrase getUpdatePhrase() {
		return (Phrase) getIntent().getParcelableExtra(INTENT_PARAM_PHRASE);
	}

	int getParamLanguageId() {
		int languageId = getIntent().getIntExtra(INTENT_PARAM_LANGUAGE_ID, 0);
		if (languageId == 0) {
			languageId = PreferenceUtils.getInt(this, PREFERENCE_ID, PREFERENCE_LANGUAGE_ID, 0);
		}
		return languageId;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.phrase_edit, menu);

		// Show deleteMenuItem?
		MenuItem deleteMenuItem = menu.findItem(R.id.action_delete_phrase);
		deleteMenuItem.setVisible(getUpdatePhrase() != null);

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
		if (id == R.id.action_delete_phrase) {
			onActionDeletePhraseSelected();
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
		intent.putExtra(HelpActivity.INTENT_SECTION_ID, "editPhrase");
		startActivity(intent);
	}

	//
	// -------------------- Handle Menu Actions --------------------
	//
	void onActionNewLabelSelected() {
		LabelEditDialog2 dlg = new LabelEditDialog2();
		dlg.show(getFragmentManager());
	}

	void onActionDeletePhraseSelected() {
		Phrase phrase = getUpdatePhrase();
		ActionsDialog dlg = new ActionsDialog();

		dlg.setMessage(getString(R.string.message_confirm_delete_phrase, PhraseUtils.truncatePhrase(phrase.phrase_text)));
		dlg.setPositiveText(getString(R.string.button_yes));
		dlg.setNegativeText(getString(R.string.button_no));

		dlg.getInputArgs().putInt(ActionsDialog.ARGUMENT_1, phrase._id);
		dlg.show(getFragmentManager());
	}

	//
	// -------------------- Handle ConfirmDialog --------------------
	//
	@Override
	public void doPositiveClick(ActionsDialog dlg) {
		taskFragment.executeLoading(TASK_DELETE_PHRASE, dlg.getInputArgs().getInt(ActionsDialog.ARGUMENT_1));
	}

	@Override
	public void doNegativeClick(ActionsDialog dlg) {
	}

	//
	// -------------------- Handle Dialog Actions --------------------
	//
	@Override
	public void onDialogSaveLabel(Label label, boolean tagLabel) {
		taskFragment.executeLoading(TASK_DIALOG_NEW_LABEL, label, tagLabel);
	}

	@Override
	public void onDialogCheckLabel(String sName) {
		taskFragment.execute(TASK_DIALOG_CHECK_LABEL, sName);
	}

	//
	// -------------------- Handle Fragment Actions --------------------
	//
	void onFragmentNewPhrase(Phrase phrase) {
		taskFragment.executeLoading(TASK_NEW_PHRASE, phrase);
	}

	void onFragmentUpdatePhrase(Phrase phrase, Phrase dbPhrase) {
		taskFragment.executeLoading(TASK_UPDATE_PHRASE, phrase, dbPhrase);
	}

	//
	// -------------------- Handle DataAccess --------------------
	//

	int insertPhrase(Phrase phrase) {
		SQLiteDatabase db = DbManager.openWrite(this);
		db.beginTransaction();
		try {

			PhraseDao.insert(db, phrase);

			List<FilterableItem> labelList = phrase.getLabelList();
			for (FilterableItem item : labelList) {
				PhraseLabelDao.insert(db, phrase._id, item.getId());
			}
			db.setTransactionSuccessful();
			return ServiceResults.SUCCESS;

		} finally {
			db.endTransaction();
		}
	}

	int updatePhrase(Phrase phrase, Phrase dbPhrase) {
		SQLiteDatabase db = DbManager.openWrite(this);
		db.beginTransaction();
		try {

			PhraseDao.update(db, phrase);

			List<FilterableItem> labelList = phrase.getLabelList();
			List<FilterableItem> dbLabelList = dbPhrase.getLabelList();

			// Insert synchronize
			for (FilterableItem label : labelList) {
				boolean isNew = true;
				for (FilterableItem dbLabel : dbLabelList) {
					if (label.getId() == dbLabel.getId()) {
						isNew = false;
						break;
					}
				}
				if (isNew) {
					PhraseLabelDao.insert(db, phrase._id, label.getId());
				}
			}

			// Delete synchronize
			for (FilterableItem dbLabel : dbLabelList) {
				boolean isDeleted = true;
				for (FilterableItem label : labelList) {
					if (label.getId() == dbLabel.getId()) {
						isDeleted = false;
						break;
					}
				}
				if (isDeleted) {
					PhraseLabelDao.delete(db, phrase._id, dbLabel.getId());
				}
			}
			db.setTransactionSuccessful();

			return ServiceResults.SUCCESS;
		} finally {
			db.endTransaction();
		}
	}

	//
	// -------------------- onTaskExecute --------------------
	//

	@Override
	public Object onTaskExecute(int taskId, Object[] params) {
		if (taskId == TASK_NEW_PHRASE) {
			Phrase phrase = (Phrase) params[0];
			int result = insertPhrase(phrase);

			// Update PhraseListModel
			if (result == ServiceResults.SUCCESS) {
				PhraseListModel.addPhrase(phrase);
				PhraseEditActivity.phraseChanged = PhraseEditActivity.PHRASE_CHANGE_ADDED;
			}
			return result;
		}
		if (taskId == TASK_UPDATE_PHRASE) {
			Phrase updatedPhrase = (Phrase) params[0];
			int result = updatePhrase(updatedPhrase, (Phrase) params[1]);

			// Update PhraseListModel
			if (result == ServiceResults.SUCCESS) {
				PhraseListModel.updatePhrase(updatedPhrase);
				PhraseEditActivity.phraseChanged = PhraseEditActivity.PHRASE_CHANGE_OTHERS;
			}
			return result;
		}
		if (taskId == TASK_DELETE_PHRASE) {
			int phraseId = (Integer) params[0];
			PhraseDao.delete(DbManager.openWrite(this), phraseId);

			// Update PhraseListModel
			PhraseListModel.removePhrase(phraseId);
			PhraseEditActivity.phraseChanged = PhraseEditActivity.PHRASE_CHANGE_DELETED;
			return null;
		}
		if (taskId == TASK_DIALOG_NEW_LABEL) {
			Label label = (Label) params[0];
			LabelDao.insert(DbManager.openWrite(this), label);
			return params;
		}
		if (taskId == TASK_DIALOG_CHECK_LABEL) {
			String sName = (String) params[0];
			return LabelDao.checkLabel(DbManager.openWrite(this), sName, null);
		}
		return null;
	}

	//
	// -------------------- onTaskExecuted --------------------
	//

	@Override
	public void onTaskExecuted(int taskId, Object result) {
		if (taskId == TASK_NEW_PHRASE || taskId == TASK_UPDATE_PHRASE) {
			int bizResult = (Integer) result;
			if (bizResult == ServiceResults.SUCCESS) {
				Toast.makeText(this, getString(R.string.message_saved_phrase_successfully), Toast.LENGTH_SHORT).show();
				this.finish();
			}
		} else if (taskId == TASK_DELETE_PHRASE) {
			Toast.makeText(this, getString(R.string.message_deleted_phrase_successfully), Toast.LENGTH_SHORT).show();
			this.finish();

		} else if (taskId == TASK_DIALOG_NEW_LABEL) {
			DialogImpl.dismiss(getFragmentManager(), LabelEditDialog2.class);
			Toast.makeText(this, getString(R.string.message_saved_label_successfully), Toast.LENGTH_SHORT).show();

			Object[] params = (Object[]) result;
			onNewLabelCreated((Label) params[0], (Boolean) params[1]);

		} else if (taskId == TASK_DIALOG_CHECK_LABEL) {
			LabelEditDialog2 dialog = LabelEditDialog2.find(getFragmentManager(), LabelEditDialog2.class);
			dialog.onCheckLabelResult((Boolean) result);
		}
	}

	void onNewLabelCreated(Label newLabel, boolean tagLabel) {
		getPlaceholderFragment().onNewLabelCreated(newLabel, tagLabel);
	}

	PlaceholderFragment getPlaceholderFragment() {
		return (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);
	}

	public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {

		// Loaders
		public static final int LOADER_LABELS = 1;
		public static final int LOADER_LANGUAGES = 2;

		// Transients
		public static final String STATE_LABEL_LIST = "labelList";
		public static final String STATE_LANGUAGE_ID = "languageId";

		// Views
		LeoEditTextVal phraseTextEditText;
		LeoEditTextVal keywordEditText;
		AutoCompleteTextView labelEditText;
		LabelEditLayout labelEditLayout;
		EditText notesEditText;

		Spinner languageSpinner;
		RadioButton masteredRadioButton, learningRadioButton;
		View saveButton;

		@Override
		public void onDestroyView() {
			phraseTextEditText.removeCallbacks();
			keywordEditText.removeCallbacks();

			super.onDestroyView();
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);

			outState.putInt(STATE_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());

			ArrayList<Parcelable> labelList = ObjectUtils.castToArrayList(labelEditLayout.getLabelList());
			outState.putParcelableArrayList(STATE_LABEL_LIST, labelList);
		}

		void onNewLabelCreated(Label newLabel, boolean tagLabel) {
			if (tagLabel) {
				labelEditLayout.getLabelList().add(newLabel);
				labelEditLayout.createLabelViews();
			}
			labelEditText.requestFocus();
			getLoaderManager().restartLoader(LOADER_LABELS, null, this);
		}

		@SuppressLint("DefaultLocale")
		String parsePhraseKeyword(String textedPhraseText, String textedKeyword) {
			int idx = textedPhraseText.toLowerCase().indexOf(textedKeyword.toLowerCase());
			return textedPhraseText.substring(idx, idx + textedKeyword.length());
		}

		@SuppressLint("DefaultLocale")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View formView = inflater.inflate(R.layout.phrase_edit_fragment, container, false);

			// phraseTextEditText
			phraseTextEditText = (LeoEditTextVal) formView.findViewById(R.id.phrase_edit_fragment_phrasetext_edittext);
			phraseTextEditText.setTempTextWatcher(new TextWatcherImpl() {

				@Override
				public void afterTextChanged(Editable s) {
					String keyword = keywordEditText.getText().toString().trim();
					if (keyword.isEmpty() == false) {
						String phraseText = StringUtils.toText(s.toString(), false);
						if (phraseText != null) {
							if (phraseText.toLowerCase().indexOf(keyword.toLowerCase()) < 0) {
								keywordEditText.setError(getString(R.string.message_error_keywords_invalid));
								return;
							}
						}
					}
					keywordEditText.setError(null);
				}
			});
			phraseTextEditText.postAttachTextWatcherRunnable();

			// keywordEditText
			keywordEditText = (LeoEditTextVal) formView.findViewById(R.id.phrase_edit_fragment_keyword_edittext);
			keywordEditText.setTempTextWatcher(new TextWatcherImpl() {

				@Override
				public void afterTextChanged(Editable s) {
					String keyword = StringUtils.toText(s.toString(), false);
					if (keyword != null) {
						String phraseText = StringUtils.toText(phraseTextEditText.getText().toString(), false);
						if (phraseText != null) {
							if (phraseText.toLowerCase().indexOf(keyword.toLowerCase()) < 0) {
								keywordEditText.setError(getString(R.string.message_error_keywords_invalid));
								return;
							}
						}
					}
					keywordEditText.setError(null);
				}
			});
			keywordEditText.postAttachTextWatcherRunnable();

			// labelEditText
			labelEditText = (AutoCompleteTextView) formView.findViewById(R.id.phrase_edit_fragment_labels_edittext);
			LabelListAdapter labelAdapter = new LabelListAdapter(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<FilterableItem>());
			labelEditText.setAdapter(labelAdapter);

			// labelEditLayout
			labelEditLayout = (LabelEditLayout) formView.findViewById(R.id.phrase_edit_fragment_labels_editlayout);
			labelEditLayout.setLabelEditText(labelEditText);
			labelEditLayout.setLayoutSizer(new LayoutSizerImpl(40));

			// notesEditText
			notesEditText = (EditText) formView.findViewById(R.id.phrase_edit_fragment_notes_edittext);

			// languageSpinner
			languageSpinner = (Spinner) formView.findViewById(R.id.phrase_edit_fragment_language_spinner);
			ArrayAdapterImpl<Language> languageAdapter = new ArrayAdapterImpl<Language>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<Language>());
			languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			languageSpinner.setAdapter(languageAdapter);

			// masteredRadioButton
			masteredRadioButton = (RadioButton) formView.findViewById(R.id.phrase_edit_fragment_mastered_radiobutton);

			// learningRadioButton
			learningRadioButton = (RadioButton) formView.findViewById(R.id.phrase_edit_fragment_learning_radiobutton);

			// saveButton
			saveButton = formView.findViewById(R.id.phrase_edit_fragment_save_button);
			saveButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// No language?
					if (languageSpinner.getSelectedItemId() <= 0) {
						return;
					}
					// Validation
					Bool isValid = new Bool(true);
					Validator.validateRequired(phraseTextEditText, R.string.message_error_field_required, isValid);
					Validator.validateRequired(keywordEditText, R.string.message_error_field_required, isValid);

					if (isValid.getValue()) {

						Phrase dbPhrase = ((PhraseEditActivity) getActivity()).getUpdatePhrase();
						Phrase phrase = new Phrase();

						if (dbPhrase == null) {
							phrase.last_updated = System.currentTimeMillis();
							phrase.mem_just_updated = phrase.last_updated;
						} else {
							phrase.last_updated = dbPhrase.last_updated;
							phrase._id = dbPhrase._id;
						}

						phrase.phrase_text = StringUtils.toText(phraseTextEditText.getText().toString(), true);
						String keyword = StringUtils.toText(keywordEditText.getText().toString(), false);
						phrase.key_word = parsePhraseKeyword(phrase.phrase_text, keyword);
						phrase.s_keyword = StringUtils.toSearchable(phrase.key_word);

						phrase.notes = StringUtils.toText(notesEditText.getText().toString(), true);
						if (phrase.notes == null) {
							phrase.notes = StringUtils.EMPTY_STRING;
						}

						phrase.language_id = (int) languageSpinner.getSelectedItemId();
						phrase.mastered = masteredRadioButton.isChecked();

						// Labels
						List<FilterableItem> labelList = labelEditLayout.getLabelList();
						LabelUtils.sortLabelList(labelList);

						phrase.labels = LabelUtils.toLabels(labelList);
						phrase.setLabelList(labelList);

						// Save Phrase
						if (dbPhrase == null) {
							((PhraseEditActivity) getActivity()).onFragmentNewPhrase(phrase);
						} else {
							((PhraseEditActivity) getActivity()).onFragmentUpdatePhrase(phrase, dbPhrase);
						}
					}
				}
			});

			// cancelButton
			View cancelButton = formView.findViewById(R.id.phrase_edit_fragment_cancel_button);
			cancelButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					getActivity().onBackPressed();
				}
			});

			// savedInstanceState
			if (savedInstanceState == null) {
				Phrase dbPhrase = ((PhraseEditActivity) getActivity()).getUpdatePhrase();
				if (dbPhrase != null) {

					// Edit Phrase
					phraseTextEditText.setText(dbPhrase.phrase_text);
					keywordEditText.setText(dbPhrase.key_word);
					labelEditLayout.setLabelList(LabelUtils.copyLabelList(dbPhrase.getLabelList()));

					notesEditText.setText(dbPhrase.notes);
					languageSpinner.setTag(dbPhrase.language_id);
					masteredRadioButton.setChecked(dbPhrase.mastered);
				} else {
					int languageId = ((PhraseEditActivity) getActivity()).getParamLanguageId();
					languageSpinner.setTag(languageId);
				}
			} else {
				// savedInstanceState != null
				languageSpinner.setTag(savedInstanceState.getInt(STATE_LANGUAGE_ID));

				List<FilterableItem> labelList = ObjectUtils.castToList(savedInstanceState.getParcelableArrayList(STATE_LABEL_LIST));
				labelEditLayout.setLabelList((labelList));
			}

			labelEditLayout.createLabelViews();
			return formView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			getLoaderManager().initLoader(LOADER_LABELS, null, this);
			getLoaderManager().initLoader(LOADER_LANGUAGES, null, this);
		}

		@Override
		public Loader<Object> onCreateLoader(int id, final Bundle args) {
			if (id == LOADER_LABELS) {
				AsyncLoader<Object> loader = new AsyncLoader<Object>(getActivity()) {
					@Override
					protected Object loadData() {
						return LabelDao.queryLabels(DbManager.openRead(getContext()));
					}
				};
				return loader;
			} else {
				AsyncLoader<Object> loader = new AsyncLoader<Object>(getActivity()) {
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
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void onLoadFinished(Loader<Object> loader, Object data) {
			int id = loader.getId();
			if (id == LOADER_LABELS) {
				((LabelListAdapter) labelEditText.getAdapter()).setObjects((List<Label>) data);

			} else {
				boolean updateObjects = ((ArrayAdapterImpl) languageSpinner.getAdapter()).setObjects((List<Language>) data);
				if (updateObjects) {
					if (languageSpinner.getTag() != null) {
						ViewUtils.setSelectionItemId(languageSpinner, (Integer) languageSpinner.getTag());
					}
				}
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void onLoaderReset(Loader<Object> loader) {
			int id = loader.getId();
			if (id == LOADER_LABELS) {
				((LabelListAdapter) labelEditText.getAdapter()).setObjects(null);

			} else {
				((ArrayAdapterImpl) languageSpinner.getAdapter()).setObjects(null);
			}
		}
	}
}
