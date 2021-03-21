package com.appslandia.phrasebuilder;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.loaders.AsyncLoader;
import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.DataList;
import com.appslandia.core.utils.PreferenceUtils;
import com.appslandia.core.utils.StringUtils;
import com.appslandia.core.utils.ViewUtils;
import com.appslandia.core.views.AbstractListFragment;
import com.appslandia.core.views.ActionsDialog;
import com.appslandia.core.views.ActivityImpl;
import com.appslandia.core.views.HelpActivity;
import com.appslandia.core.views.LeoEditTextAction;
import com.appslandia.core.views.LeoListView;
import com.appslandia.core.views.LeoSpinner;
import com.appslandia.core.views.ListViewPos;
import com.appslandia.core.views.OnItemSelectedListenerImpl;
import com.appslandia.core.views.OnTextViewActionListener;
import com.appslandia.core.views.TaskFragment;
import com.appslandia.core.views.TextWatcherImpl;
import com.appslandia.phrasebuilder.entities.Label;
import com.appslandia.phrasebuilder.entities.Language;
import com.appslandia.phrasebuilder.entities.Phrase;
import com.appslandia.phrasebuilder.sqlite.DbManager;
import com.appslandia.phrasebuilder.sqlite.LabelDao;
import com.appslandia.phrasebuilder.sqlite.LanguageDao;
import com.appslandia.phrasebuilder.sqlite.PhraseDao;
import com.appslandia.phrasebuilder.utils.PhraseBuilderUtils;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PhraseListActivity extends ActivityImpl implements PhraseListAdapter.Callbacks, TaskFragment.Callbacks, ActionsDialog.Callbacks {

	// Preferences
	public static final String PREFERENCE_ID = PhraseListActivity.class.getSimpleName();
	public static final String PREFERENCE_LANGUAGE_ID = PREFERENCE_ID + "_languageId";

	// Request Codes
	public static final int REQUEST_CODE_PHRASE_EDIT = 1;
	public static final int REQUEST_CODE_TEST_PHRASES = 2;

	// Tasks
	public static final int TASK_UPDATE_MASTERED = 1;
	public static final int TASK_DELETE_PHRASE = 2;

	// taskFragment
	TaskFragment taskFragment;

	// Views
	LeoSpinner languageSpinner;

	// activityInstanceId
	final int activityInstanceId = ActivityUtils.nextId();
	private static int lastActivityInstanceId = 0;

	@Override
	protected void onPause() {
		if (isFinishing()) {
			saveActivityPrefs();
		}
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		PhraseListModel.reset();
		super.onBackPressed();
	}

	void saveActivityPrefs() {
		synchronized (BackupUtils.MUTEX) {
			PreferenceUtils.savePreference(this, PREFERENCE_ID, PREFERENCE_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phrase_list_activity);

		// Initialize ActionBar
		final ActionBar actionBar = getActionBar();

		// Initialize Activity
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.ActivityListBackgroundColor));

		// Before KITKAT
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			// actionBar
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

			LinearLayout layout = new LinearLayout(actionBar.getThemedContext());
			layout.setOrientation(LinearLayout.HORIZONTAL);
			layout.setGravity(Gravity.CENTER_VERTICAL);

			// backView
			ImageView backView = new ImageView(actionBar.getThemedContext());
			backView.setImageResource(R.drawable.ic_lib_actionbar_back);
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layout.addView(backView, params);

			// languageSpinner
			languageSpinner = new LeoSpinner(actionBar.getThemedContext());
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layout.addView(languageSpinner, params);

			actionBar.setCustomView(layout);
		} else {

			// actionBar
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

			// Language Spinner
			languageSpinner = new LeoSpinner(actionBar.getThemedContext());
			actionBar.setCustomView(languageSpinner);
		}

		// savedInstanceState
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
		getMenuInflater().inflate(R.menu.phrase_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		}
		if (id == R.id.action_new_phrase) {
			onActionNewPhraseSelected();
			return true;
		}
		if (id == R.id.action_test_phrases) {
			onActionTestPhrasesSelected();
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
		intent.putExtra(HelpActivity.INTENT_SECTION_ID, "managePhrases");
		startActivity(intent);
	}

	//
	// -------------------- Handle Action Menus --------------------
	//

	void saveLastActivityInstanceId() {
		PhraseEditActivity.phraseChanged = PhraseEditActivity.PHRASE_CHANGE_UNCHANGED;
		PhraseListActivity.lastActivityInstanceId = this.activityInstanceId;
	}

	boolean isActivityBeingRecreated() {
		return (PhraseListActivity.lastActivityInstanceId > 0) && (PhraseListActivity.lastActivityInstanceId != this.activityInstanceId);
	}

	void onActionNewPhraseSelected() {
		saveLastActivityInstanceId();

		Intent intent = new Intent(this, PhraseEditActivity.class);
		intent.putExtra(PhraseEditActivity.INTENT_PARAM_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
		startActivityForResult(intent, REQUEST_CODE_PHRASE_EDIT);
	}

	void onActionTestPhrasesSelected() {
		saveLastActivityInstanceId();

		Intent intent = new Intent(this, PhraseTestInputsActivity.class);
		intent.putExtra(PhraseTestInputsActivity.INTENT_PARAM_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
		startActivityForResult(intent, REQUEST_CODE_TEST_PHRASES);
	}

	//
	// -------------------- onActivityResult --------------------
	//

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_PHRASE_EDIT:
			if (isActivityBeingRecreated() == false) {
				if (PhraseEditActivity.phraseChanged != PhraseEditActivity.PHRASE_CHANGE_UNCHANGED) {
					getPlaceholderFragment().postRefeshPhrases(PhraseEditActivity.phraseChanged);
				}
			}
			break;
		case REQUEST_CODE_TEST_PHRASES:
			if (isActivityBeingRecreated() == false) {
				if (PhraseEditActivity.phraseChanged != PhraseEditActivity.PHRASE_CHANGE_UNCHANGED) {
					getPlaceholderFragment().postRefeshPhrases(PhraseEditActivity.phraseChanged);
				}
			}
			break;
		default:
			break;
		}
	}

	PlaceholderFragment getPlaceholderFragment() {
		return (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);
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
	// -------------------- Handle Adapter Actions --------------------
	//
	@Override
	public void onAdapterUpdateMastery(Phrase phrase, boolean mastered) {
		taskFragment.executeLoading(TASK_UPDATE_MASTERED, phrase, mastered);
	}

	@Override
	public void onAdapterClickLabel(FilterableItem label) {
		getPlaceholderFragment().loadPhrasesByLabel(StringUtils.toDisplayLabel(label.getName()));
	}

	public void onAdapterPhraseEdit(Phrase phrase) {
		saveLastActivityInstanceId();

		Intent intent = new Intent(this, PhraseEditActivity.class);
		intent.putExtra(PhraseEditActivity.INTENT_PARAM_PHRASE, phrase);
		startActivityForResult(intent, REQUEST_CODE_PHRASE_EDIT);
	}

	@Override
	public void onAdapterDeletePhrase(Phrase phrase) {
		ActionsDialog dlg = new ActionsDialog();

		dlg.setMessage(getString(R.string.message_confirm_delete_phrase, PhraseUtils.truncatePhrase(phrase.phrase_text)));
		dlg.setPositiveText(getString(R.string.button_yes));
		dlg.setNegativeText(getString(R.string.button_no));
		dlg.getInputArgs().putInt(ActionsDialog.ARGUMENT_1, phrase._id);

		dlg.show(getFragmentManager());
	}

	//
	// -------------------- onTaskExecute --------------------
	//

	@Override
	public Object onTaskExecute(int taskId, Object[] params) {
		if (taskId == TASK_UPDATE_MASTERED) {
			Phrase phrase = (Phrase) params[0];
			boolean mastered = (Boolean) params[1];
			PhraseDao.updateMasteredTag(DbManager.openWrite(this), phrase._id, mastered);

			// Update PhraseListModel
			phrase.mastered = mastered;
			return null;
		}
		if (taskId == TASK_DELETE_PHRASE) {
			int phraseId = (Integer) params[0];
			PhraseDao.delete(DbManager.openWrite(this), phraseId);

			// Update PhraseListModel
			PhraseListModel.removePhrase(phraseId);
			return null;
		}
		return null;
	}

	//
	// -------------------- onTaskExecuted --------------------
	//

	@Override
	public void onTaskExecuted(int taskId, Object result) {
		if (taskId == TASK_UPDATE_MASTERED) {
			getPlaceholderFragment().refreshPhrases(false, false);

		} else if (taskId == TASK_DELETE_PHRASE) {
			Toast.makeText(this, getString(R.string.message_deleted_phrase_successfully), Toast.LENGTH_SHORT).show();
			getPlaceholderFragment().refreshPhrases(false, true);
		}
	}

	public static class PlaceholderFragment extends AbstractListFragment implements LoaderManager.LoaderCallbacks<Object>, LabelSelectDialog.Callbacks {

		// Model Keys
		public static final String LANGUAGE_LIST_MODEL_TAG = "languageListModel";

		// Transients
		public static final String STATE_LANGUAGE_ID = "languageId";
		public static final String STATE_PHRASE_LISTVIEW_POS = "phraseListViewPos";

		// Loaders
		public static final int LOADER_LANGUAGES = 1;
		public static final int LOADER_PHRASES = 2;
		public static final int LOADER_LABELS = 3;

		public static final String LOADER_PHRASES_PARAM_LANGUAGE_ID = "languageId";
		public static final String LOADER_PHRASES_PARAM_SEARCH_TEXT = "searchQuery";
		public static final String LOADER_PHRASES_PARAM_MAX_LAST_UPDATED = "maxLastUpdated";

		public static final String LOADER_PHRASES_PARAM_LOADING_TYPE = "loadingType";
		public static final int LOADER_PHRASES_PARAM_LOADING_TYPE_START = 0;
		public static final int LOADER_PHRASES_PARAM_LOADING_TYPE_APPENDING = 1;

		// Views
		LeoSpinner languageSpinner;
		LeoEditTextAction searchQueryEditText;
		ArrayList<Label> labels;

		@Override
		public void onDestroyView() {
			this.mList.removeCallbacks(refreshPhrasesRunnable_added);
			this.mList.removeCallbacks(refreshPhrasesRunnable_deleted);
			this.mList.removeCallbacks(refreshPhrasesRunnable_others);

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
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);

			outState.putInt(STATE_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
			outState.putParcelable(STATE_PHRASE_LISTVIEW_POS, createListViewPos());
		}

		final Runnable refreshPhrasesRunnable_added = new Runnable() {
			@Override
			public void run() {
				refreshPhrases(true, false);
			}
		};

		final Runnable refreshPhrasesRunnable_deleted = new Runnable() {
			@Override
			public void run() {
				refreshPhrases(false, true);
			}
		};

		final Runnable refreshPhrasesRunnable_others = new Runnable() {
			@Override
			public void run() {
				refreshPhrases(false, false);
			}
		};

		void postRefeshPhrases(int phraseChanged) {
			if (phraseChanged == PhraseEditActivity.PHRASE_CHANGE_ADDED) {
				this.mList.post(refreshPhrasesRunnable_added);

			} else if (phraseChanged == PhraseEditActivity.PHRASE_CHANGE_DELETED) {
				this.mList.post(refreshPhrasesRunnable_deleted);

			} else {
				this.mList.post(refreshPhrasesRunnable_others);
			}
		}

		void refreshPhrases(boolean added, boolean deleted) {
			PhraseEditActivity.phraseChanged = PhraseEditActivity.PHRASE_CHANGE_UNCHANGED;

			// Refresh phrases
			updateListShown(false);
			this.mAdapter.notifyDataSetChanged();
			updateListShown(true);

			// Added?
			if (added) {
				this.mList.setSelection(0);

			} else if (deleted) {
				// Load more?
				if (PhraseListModel.phrases.size() < PhraseBuilderUtils.PHRASES_LOAD_LIMIT) {
					loadMorePhrases();
				}
			}
		}

		String getSearchableText() {
			String text = searchQueryEditText.getText().toString().trim();
			searchQueryEditText.setText(text);
			return StringUtils.toSearchable(text);
		}

		void loadMorePhrases() {
			Bundle args = new Bundle(4);
			args.putInt(LOADER_PHRASES_PARAM_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
			args.putString(LOADER_PHRASES_PARAM_SEARCH_TEXT, PhraseListModel.searchText);

			long lastJustUpdated = PhraseListModel.getLastMemJustUpdated();
			if (lastJustUpdated > 0) {
				args.putLong(LOADER_PHRASES_PARAM_MAX_LAST_UPDATED, lastJustUpdated);
			}

			args.putInt(LOADER_PHRASES_PARAM_LOADING_TYPE, LOADER_PHRASES_PARAM_LOADING_TYPE_APPENDING);
			loadPhrases(args);
		}

		void loadPhrases(Bundle args) {
			if (args.getInt(LOADER_PHRASES_PARAM_LOADING_TYPE) != LOADER_PHRASES_PARAM_LOADING_TYPE_APPENDING) {
				updateListShown(false);
			}
			getLoaderManager().restartLoader(LOADER_PHRASES, args, this);
		}

		void loadPhrases() {
			Bundle args = new Bundle(2);
			args.putInt(LOADER_PHRASES_PARAM_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
			args.putString(LOADER_PHRASES_PARAM_SEARCH_TEXT, getSearchableText());

			loadPhrases(args);
		}

		void loadPhrasesByLabel(String label) {
			ViewUtils.hideSoftInput(searchQueryEditText);
			loadPhrases(label);
		}

		void loadPhrases(String searchText) {
			searchQueryEditText.setText(searchText);
			loadPhrases();
		}

		@Override
		protected int getViewResourceId() {
			return R.layout.phrase_list_fragment;
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			((PhraseListActivity) getActivity()).onAdapterPhraseEdit((Phrase) l.getItemAtPosition(position));
		}

		@Override
		public void onSelectLabel(Label label) {
			searchQueryEditText.setText(label.getFilterName());
			ViewUtils.hideSoftInput(searchQueryEditText);
			loadPhrases();
		}

		@Override
		protected void initializeView(View root) {

			// languageSpinner
			languageSpinner = ((PhraseListActivity) getActivity()).languageSpinner;
			languageSpinner.setOnItemSelectedListener(new OnItemSelectedListenerImpl() {

				@Override
				public void doOnItemSelected(AdapterView<?> parent, View view, int position, long id) {
					searchQueryEditText.setText(StringUtils.EMPTY_STRING);

					Bundle args = new Bundle(1);
					args.putInt(LOADER_PHRASES_PARAM_LANGUAGE_ID, (int) id);
					args.putString(LOADER_PHRASES_PARAM_SEARCH_TEXT, StringUtils.EMPTY_STRING);

					loadPhrases(args);
				}
			});

			// searchQueryEditText
			searchQueryEditText = (LeoEditTextAction) root.findViewById(R.id.phrase_list_fragment_search_query_edit);
			searchQueryEditText.setOnTextViewActionListener(new OnTextViewActionListener() {

				@Override
				public void onActionSelected(TextView textView, boolean rightAction) {
					if (rightAction) {
						textView.setText(StringUtils.EMPTY_STRING);
						ViewUtils.showSoftInput((EditText) textView);

					} else {
						if (labels != null) {
							LabelSelectDialog dlg = new LabelSelectDialog();
							dlg.getInputArgs().putParcelableArrayList(LabelSelectDialog.DIALOG_LABELS, labels);

							dlg.setHostFragment(PlaceholderFragment.this, 0);
							dlg.show(getFragmentManager());
						}
					}
				}
			});
			searchQueryEditText.addTextChangedListener(new TextWatcherImpl() {

				@Override
				public void afterTextChanged(Editable s) {
					searchQueryEditText.showRightCompound(s.toString().trim().isEmpty() == false);
				}
			});
			searchQueryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						ViewUtils.hideSoftInput((EditText) v);
						loadPhrases();
						return true;
					}
					return false;
				}
			});

			// btnSearch
			View btnSearch = root.findViewById(R.id.phrase_list_fragment_search_button);
			btnSearch.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ViewUtils.hideSoftInput(searchQueryEditText);
					loadPhrases();
				}
			});

			// listView
			LeoListView listView = (LeoListView) this.mList;
			View loadingView = root.findViewById(R.id.phrase_list_fragment_loading);
			listView.setLoadingView(loadingView);
			listView.setOnScrollListener(new LeoListView.ScrollListener() {

				@Override
				protected void loadMoreItems() {
					loadMorePhrases();
				}
			});
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			// Empty Message
			setEmptyText(getString(R.string.message_no_phrases));

			// List Divider
			ViewUtils.setTransparentDivider(mList, 8); // 8dp

			// languageSpinner
			ArrayAdapterImpl<Language> languagesAdapter = new ArrayAdapterImpl<Language>(getActivity().getActionBar().getThemedContext(), android.R.layout.simple_spinner_item, new ArrayList<Language>());
			languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			languagesAdapter.setTextColor(getResources().getColor(R.color.ActionBarTitleColor));
			languageSpinner.setBackgroundResource(R.drawable.ic_lib_actionbar_spinner);
			languageSpinner.setAdapter(languagesAdapter);

			// listView
			PhraseListAdapter phrasesAdapter = new PhraseListAdapter(getActivity(), R.layout.phrase_list_activity_list_item, PhraseListModel.phrases);
			setListAdapter(phrasesAdapter);
			setListShown(false);

			// Handle savedInstanceState
			if (savedInstanceState == null) {
				// languageId
				int languageId = PreferenceUtils.getInt(getActivity(), PREFERENCE_ID, PREFERENCE_LANGUAGE_ID, 0);
				languageSpinner.setTag(languageId);

			} else {
				// languageId
				int languageId = savedInstanceState.getInt(STATE_LANGUAGE_ID);
				languageSpinner.setTag(languageId);
				ViewUtils.setSelectionItemId(languageSpinner, languageId);

				// ListViewPos
				ListViewPos pos = (ListViewPos) savedInstanceState.getParcelable(STATE_PHRASE_LISTVIEW_POS);
				this.mList.setSelectionFromTop(pos.getPosition(), pos.getTopY());
			}

			// Load languages
			getLoaderManager().initLoader(LOADER_LANGUAGES, null, this);

			// Load Labels
			getLoaderManager().initLoader(LOADER_LABELS, null, this);
		}

		@Override
		public Loader<Object> onCreateLoader(int id, final Bundle args) {
			if (id == LOADER_LANGUAGES) {
				// Language loader
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
			} else if (id == LOADER_LABELS) {
				// Label loader
				AsyncLoader<Object> loader = new AsyncLoader<Object>(getActivity()) {
					@Override
					protected Object loadData() {
						List<Label> labels = LabelDao.queryLabels(DbManager.openRead(getContext()));
						labels.add(0, new Label(PhraseUtils.LIST_ITEM_UNLABELED, PhraseUtils.KEYWORD_UNLABELED));
						labels.add(0, new Label(PhraseUtils.LIST_ITEM_MASTERED, PhraseUtils.KEYWORD_MASTERED));
						labels.add(0, new Label(PhraseUtils.LIST_ITEM_LEARNING, PhraseUtils.KEYWORD_LEARNING));
						return labels;
					}
				};
				return loader;
			} else {
				// Phrase loader
				AsyncLoader<Object> loader = new AsyncLoader<Object>(getActivity()) {
					@Override
					protected Object loadData() {
						int languageId = args.getInt(LOADER_PHRASES_PARAM_LANGUAGE_ID);
						String searchText = args.getString(LOADER_PHRASES_PARAM_SEARCH_TEXT);
						long maxLastUpdated = args.getLong(LOADER_PHRASES_PARAM_MAX_LAST_UPDATED, System.currentTimeMillis());

						DataList<Phrase> result = new DataList<Phrase>();
						result.setArguments(args);
						result.setObjects(PhraseDao.search(DbManager.openRead(getContext()), languageId, maxLastUpdated, searchText));
						return result;
					}
				};
				return loader;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onLoadFinished(Loader<Object> loader, Object data) {
			int loaderId = loader.getId();

			// Language loader
			if (loaderId == LOADER_LANGUAGES) {
				List<Language> result = (List<Language>) data;
				boolean updateObjects = languageSpinner.checkObjects(result);
				if (updateObjects) {
					languageSpinner.setObjects(result);

					// Language selection
					int languageId = (languageSpinner.getTag() != null) ? (Integer) languageSpinner.getTag() : (0);
					if (languageSpinner.setSelectionItemId(languageId) == false) {
						languageSpinner.setSelection(0);
					}

					// Update language spinner
					languageSpinner.notifyDataSetChanged();

					// Load phrases
					Bundle args = new Bundle(2);
					args.putInt(LOADER_PHRASES_PARAM_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());
					args.putString(LOADER_PHRASES_PARAM_SEARCH_TEXT, getSearchableText());
					getLoaderManager().initLoader(LOADER_PHRASES, args, this);

				} else {
					updateListShown(true);
				}

			} else if (loaderId == LOADER_LABELS) {
				labels = (ArrayList<Label>) data;

			} else {
				// Phrase loader
				DataList<Phrase> result = (DataList<Phrase>) data;
				int loadingType = result.getInt(LOADER_PHRASES_PARAM_LOADING_TYPE, LOADER_PHRASES_PARAM_LOADING_TYPE_START);

				if (loadingType == LOADER_PHRASES_PARAM_LOADING_TYPE_APPENDING) {
					boolean updateObjects = PhraseListModel.appendPhrases(result);
					if (updateObjects) {
						// Update phrase listView
						this.mAdapter.notifyDataSetChanged();
					}

					// Remove loading
					((LeoListView) this.mList).setLoading(false);
				} else if (loadingType == LOADER_PHRASES_PARAM_LOADING_TYPE_START) {
					boolean updateObjects = PhraseListModel.setPhrases(result);

					if (updateObjects) {
						PhraseEditActivity.phraseChanged = PhraseEditActivity.PHRASE_CHANGE_UNCHANGED;

						// Update phrase listView
						this.mAdapter.notifyDataSetChanged();
						this.mList.setSelection(0);
					}
					updateListShown(true);
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Object> loader) {
			int loaderId = loader.getId();
			if (loaderId == LOADER_LANGUAGES) {
				languageSpinner.setObjects(null);
			} else {
				PhraseListModel.reset();
			}
		}
	}
}
