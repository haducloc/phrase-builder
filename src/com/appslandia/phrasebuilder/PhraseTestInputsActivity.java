package com.appslandia.phrasebuilder;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.adapters.LabelItem;
import com.appslandia.core.adapters.LabelListAdapter;
import com.appslandia.core.adapters.SimpleItem;
import com.appslandia.core.loaders.AsyncLoader;
import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.ObjectUtils;
import com.appslandia.core.utils.PreferenceUtils;
import com.appslandia.core.utils.ViewUtils;
import com.appslandia.core.views.ActivityImpl;
import com.appslandia.core.views.HelpActivity;
import com.appslandia.core.views.LabelEditLayout;
import com.appslandia.core.views.LayoutSizerImpl;
import com.appslandia.core.views.MessageDialog;
import com.appslandia.core.views.TaskFragment;
import com.appslandia.phrasebuilder.entities.Label;
import com.appslandia.phrasebuilder.entities.Language;
import com.appslandia.phrasebuilder.entities.Phrase;
import com.appslandia.phrasebuilder.sqlite.DbManager;
import com.appslandia.phrasebuilder.sqlite.LabelDao;
import com.appslandia.phrasebuilder.sqlite.LanguageDao;
import com.appslandia.phrasebuilder.sqlite.PhraseDao;
import com.appslandia.phrasebuilder.utils.PhraseTestUtils;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

public class PhraseTestInputsActivity extends ActivityImpl implements TaskFragment.Callbacks {

	// Preferences
	public static final String PREFERENCE_ID = PhraseTestInputsActivity.class.getSimpleName();
	public static final String PREFERENCE_LANGUAGE_ID = PREFERENCE_ID + "_languageId";

	// Parameters
	public static final String INTENT_PARAM_LANGUAGE_ID = "languageId";
	public static final String INTENT_PARAM_LABEL = "label";

	// taskFragment
	TaskFragment taskFragment;

	int getParamLanguageId() {
		int languageId = getIntent().getIntExtra(INTENT_PARAM_LANGUAGE_ID, 0);
		if (languageId == 0) {
			languageId = PreferenceUtils.getInt(this, PREFERENCE_ID, PREFERENCE_LANGUAGE_ID, 0);
		}
		return languageId;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phrase_test_inputs_activity);

		// Initialize ActionBar
		ActivityUtils.initActionBarUp(getActionBar());

		// Initialize Activity
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.ActivityEditBackgroundColor));

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
		getMenuInflater().inflate(R.menu.test_settings, menu);
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
		intent.putExtra(HelpActivity.INTENT_SECTION_ID, "testPhrases");
		startActivity(intent);
	}

	//
	// -------------------- onTaskExecute --------------------
	//

	@Override
	public Object onTaskExecute(int taskId, Object[] params) {
		TestSettingArgs args = (TestSettingArgs) params[0];
		List<Phrase> phrases = PhraseDao.loadTestPhrases(DbManager.openRead(this), args.languageId, args.masteryTypeId, args.daysAgo, args.labelList);
		List<String> keywords = PhraseDao.loadKeywords(DbManager.openRead(this), args.languageId);

		if (phrases.isEmpty() == false) {

			List<Object> testPhrases = null;
			if (args.testTypeId == PhraseTestUtils.TEST_TYPE_MULTIPLE_CHOICE) {
				testPhrases = PhraseTestUtils.generateMtcTest(phrases, keywords, getResources());

			} else if (args.testTypeId == PhraseTestUtils.TEST_TYPE_FILL_IN) {
				testPhrases = PhraseTestUtils.generateFillInTest(phrases);

			} else {
				testPhrases = PhraseTestUtils.generateUnspecifiedTest(phrases, keywords, getResources());
			}
			return new Object[] { testPhrases, args.testTypeId };
		}
		return null;
	}

	//
	// -------------------- onTaskExecuted --------------------
	//

	@SuppressWarnings("unchecked")
	@Override
	public void onTaskExecuted(int taskId, Object result) {
		Object[] params = (Object[]) result;
		if (params == null) {
			String msg = getString(R.string.message_no_matched_phrases_found);
			String okButton = getString(R.string.button_ok);
			new MessageDialog().setButtonText(okButton).setMessage(msg).show(getFragmentManager());

		} else {
			// Start Tests
			List<PhraseTest> testPhrases = (List<PhraseTest>) params[0];
			int testTypeId = (Integer) params[1];

			Intent intent = new Intent(this, PhraseTestActivity.class);
			PhraseTestActivity.paramTestPhrases = testPhrases;
			intent.putExtra(PhraseTestActivity.INTENT_PARAM_TEST_TYPE_ID, testTypeId);

			startActivity(intent);
		}
	}

	public void prepareTesting(TestSettingArgs args) {
		taskFragment.executeLoading(0, args);
	}

	PlaceholderFragment getPlaceholderFragment() {
		return (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);
	}

	private static class TestSettingArgs {
		int languageId;
		List<FilterableItem> labelList;
		int masteryTypeId;
		int daysAgo;
		int testTypeId;
	}

	public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {

		// Transients
		public static final String STATE_LABEL_LIST = "labelList";
		public static final String STATE_LANGUAGE_ID = "languageId";

		// Loaders
		public static final int LOADER_LANGUAGES = 1;
		public static final int LOADER_LABELS = 2;

		// Views
		Spinner languageSpinner;
		AutoCompleteTextView labelEditText;
		LabelEditLayout labelEditLayout;
		Spinner masteryTypeSpinner;
		Spinner dateCreatedSpinner;
		Spinner testTypeSpinner;

		View startTestButton;

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
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);

			outState.putInt(STATE_LANGUAGE_ID, (int) languageSpinner.getSelectedItemId());

			ArrayList<Parcelable> labelList = ObjectUtils.castToArrayList(labelEditLayout.getLabelList());
			outState.putParcelableArrayList(STATE_LABEL_LIST, labelList);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View formView = inflater.inflate(R.layout.phrase_test_inputs_fragment, container, false);

			// languageSpinner
			languageSpinner = (Spinner) formView.findViewById(R.id.phrase_test_inputs_fragment_language_spinner);
			ArrayAdapterImpl<Language> languagesAdapter = new ArrayAdapterImpl<Language>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<Language>());
			languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			languageSpinner.setAdapter(languagesAdapter);

			// labelEditText
			labelEditText = (AutoCompleteTextView) formView.findViewById(R.id.phrase_test_inputs_fragment_label_edittext);
			LabelListAdapter labelsAdapter = new LabelListAdapter(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<FilterableItem>());
			labelEditText.setAdapter(labelsAdapter);

			// labelEditLayout
			labelEditLayout = (LabelEditLayout) formView.findViewById(R.id.phrase_test_inputs_fragment_label_editlayout);
			labelEditLayout.setLabelEditText(labelEditText);
			labelEditLayout.setLayoutSizer(new LayoutSizerImpl(40));

			// masteryTypeSpinner
			masteryTypeSpinner = (Spinner) formView.findViewById(R.id.phrase_test_inputs_fragment_mastery_spinner);
			ArrayAdapterImpl<SimpleItem> masteryTypesAdapter = new ArrayAdapterImpl<SimpleItem>(getActivity(), android.R.layout.simple_spinner_item, PhraseTestUtils.getMasteryList(getResources()));
			masteryTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			masteryTypeSpinner.setAdapter(masteryTypesAdapter);
			ViewUtils.setSelectionItemId(masteryTypeSpinner, PhraseTestUtils.MASTERY_LEARNING);

			// dateCreatedSpinner
			dateCreatedSpinner = (Spinner) formView.findViewById(R.id.phrase_test_inputs_fragment_date_created_spinner);
			ArrayAdapterImpl<SimpleItem> dateCreatedAdapter = new ArrayAdapterImpl<SimpleItem>(getActivity(), android.R.layout.simple_spinner_item, PhraseTestUtils.getDateCreatedList(getResources()));
			dateCreatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			dateCreatedSpinner.setAdapter(dateCreatedAdapter);
			ViewUtils.setSelectionItemId(dateCreatedSpinner, 0);

			// testTypeSpinner
			testTypeSpinner = (Spinner) formView.findViewById(R.id.phrase_test_inputs_fragment_testtype_spinner);
			ArrayAdapterImpl<SimpleItem> testTypesAdapter = new ArrayAdapterImpl<SimpleItem>(getActivity(), android.R.layout.simple_spinner_item, PhraseTestUtils.getTestTypeList(getResources()));
			testTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			testTypeSpinner.setAdapter(testTypesAdapter);
			ViewUtils.setSelectionItemId(testTypeSpinner, PhraseTestUtils.TEST_TYPE_MULTIPLE_CHOICE);

			// startTestButton
			startTestButton = formView.findViewById(R.id.phrase_test_inputs_fragment_starttest_button);
			startTestButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					TestSettingArgs args = new TestSettingArgs();
					args.languageId = (int) languageSpinner.getSelectedItemId();
					if (args.languageId == 0) {
						return;
					}
					args.labelList = labelEditLayout.getLabelList();
					args.masteryTypeId = (int) masteryTypeSpinner.getSelectedItemId();
					args.daysAgo = (int) dateCreatedSpinner.getSelectedItemId();
					args.testTypeId = (int) testTypeSpinner.getSelectedItemId();

					((PhraseTestInputsActivity) getActivity()).prepareTesting(args);
				}
			});

			// cancelButton
			View cancelButton = formView.findViewById(R.id.phrase_test_inputs_fragment_cancel_button);
			cancelButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					getActivity().onBackPressed();
				}
			});

			// Handle savedInstanceState
			if (savedInstanceState == null) {

				// Language ID
				languageSpinner.setTag(((PhraseTestInputsActivity) getActivity()).getParamLanguageId());

				// label
				Label label = getActivity().getIntent().getParcelableExtra(INTENT_PARAM_LABEL);
				if (label != null) {
					List<FilterableItem> labelList = new ArrayList<FilterableItem>();
					labelList.add(new LabelItem(label._id, label.name, label.s_name));
					labelEditLayout.setLabelList(labelList);
				}

			} else {
				languageSpinner.setTag(savedInstanceState.getInt(STATE_LANGUAGE_ID));

				List<FilterableItem> labelList = ObjectUtils.castToList(savedInstanceState.getParcelableArrayList(STATE_LABEL_LIST));
				labelEditLayout.setLabelList(labelList);
			}

			labelEditLayout.createLabelViews();
			return formView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			// Load references
			getLoaderManager().initLoader(LOADER_LANGUAGES, null, this);
			getLoaderManager().initLoader(LOADER_LABELS, null, this);
		}

		@Override
		public Loader<Object> onCreateLoader(int id, Bundle args) {
			if (id == LOADER_LANGUAGES) {
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

			} else {
				AsyncLoader<Object> loader = new AsyncLoader<Object>(getActivity()) {
					@Override
					protected Object loadData() {
						return LabelDao.queryLabels(DbManager.openRead(getContext()));
					}
				};
				return loader;
			}
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void onLoadFinished(Loader<Object> loader, Object data) {
			int id = loader.getId();
			if (id == LOADER_LANGUAGES) {
				boolean updateObjects = ((ArrayAdapterImpl) languageSpinner.getAdapter()).setObjects((List<Language>) data);
				if (updateObjects) {
					if (languageSpinner.getTag() != null) {
						ViewUtils.setSelectionItemId(languageSpinner, (Integer) languageSpinner.getTag());
					}
				}
			} else {
				((LabelListAdapter) labelEditText.getAdapter()).setObjects((List<Label>) data);
			}
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void onLoaderReset(Loader<Object> loader) {
			int id = loader.getId();
			if (id == LOADER_LANGUAGES) {
				((ArrayAdapterImpl) languageSpinner.getAdapter()).setObjects(null);

			} else {
				((LabelListAdapter) labelEditText.getAdapter()).setObjects(null);
			}
		}
	}
}
