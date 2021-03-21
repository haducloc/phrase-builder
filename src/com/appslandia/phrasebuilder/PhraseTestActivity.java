package com.appslandia.phrasebuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.core.utils.AssetDescManager;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.MediaPlayerUtils;
import com.appslandia.core.utils.ObjectUtils;
import com.appslandia.core.utils.PreferenceUtils;
import com.appslandia.core.utils.StringUtils;
import com.appslandia.core.views.HelpActivity;
import com.appslandia.core.views.LabelLayout;
import com.appslandia.core.views.LayoutSizerImpl;
import com.appslandia.core.views.LeoToggleView;
import com.appslandia.core.views.PressAgainActivity;
import com.appslandia.core.views.TaskFragment;
import com.appslandia.core.views.TextWatcherImpl;
import com.appslandia.phrasebuilder.sqlite.DbManager;
import com.appslandia.phrasebuilder.sqlite.PhraseDao;
import com.appslandia.phrasebuilder.utils.LabelUtils;
import com.appslandia.phrasebuilder.utils.PhraseTestUtils;
import com.appslandia.phrasebuilder.utils.PhraseUtils;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class PhraseTestActivity extends PressAgainActivity implements TaskFragment.Callbacks {

	// Preferences
	public static final String PREFERENCE_ID = PhraseTestActivity.class.getSimpleName();
	public static final String PREFERENCE_TEST_SOUND_ENABLED = "testSoundEnabled";

	// Parameters
	public static final String INTENT_PARAM_TEST_TYPE_ID = "testTypeId";

	// taskFragment
	TaskFragment taskFragment;

	public static List<PhraseTest> paramTestPhrases;

	@Override
	protected void onPause() {
		if (isFinishing()) {
			saveActivityPrefs();
		}
		super.onPause();
	}

	void saveActivityPrefs() {
		synchronized (BackupUtils.MUTEX) {
			PreferenceUtils.savePreference(this, PREFERENCE_ID, PREFERENCE_TEST_SOUND_ENABLED, getPlaceholderFragment().soundToggleView.isStateOn());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phrase_test_activity);

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
		getMenuInflater().inflate(R.menu.test_phrases, menu);
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
		int testType = getIntent().getIntExtra(INTENT_PARAM_TEST_TYPE_ID, 0);
		if (testType == PhraseTestUtils.TEST_TYPE_MULTIPLE_CHOICE) {
			intent.putExtra(HelpActivity.INTENT_SECTION_ID, "testPhrases_multiplechoice");

		} else if (testType == PhraseTestUtils.TEST_TYPE_FILL_IN) {
			intent.putExtra(HelpActivity.INTENT_SECTION_ID, "testPhrases_fillin");
		} else {
			intent.putExtra(HelpActivity.INTENT_SECTION_ID, "testPhrases");
		}
		startActivity(intent);
	}

	@Override
	public Object onTaskExecute(int taskId, Object[] params) {
		// Update Mastery
		PhraseTest model = (PhraseTest) params[0];
		boolean mastered = (Boolean) params[1];
		PhraseDao.updateMasteredTag(DbManager.openWrite(this), model._id, mastered);

		// Update Test Model
		model.mastered = mastered;

		// Update PhraseListModel
		PhraseListModel.updateMastered(model._id, mastered);
		PhraseEditActivity.phraseChanged = PhraseEditActivity.PHRASE_CHANGE_OTHERS;

		return null;
	}

	@Override
	public void onTaskExecuted(int taskId, Object result) {
	}

	public void updateMasteredChange(PhraseTest model, boolean mastered) {
		taskFragment.executeLoading(0, model, mastered);
	}

	PlaceholderFragment getPlaceholderFragment() {
		return (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);
	}

	public static class PlaceholderFragment extends Fragment {

		// Transients
		public static final String STATE_SOUND_ENABLED = "soundEnabled";
		public static final String STATE_TEST_PHRASES = "testPhrases";
		public static final String STATE_PHRASE_INDEX = "phraseIndex";

		public static final String STATE_LAST_QUEST_SHOWED = "lastQuestShowed";

		// Sound Volume
		public static final float TESTING_SOUND_VOLUMN = 0.50f;

		// Views
		View mtcOptionLayout;
		View fillInOptionLayout;

		TextView phraseIndexTextView;
		LeoToggleView masteryToggleView;
		TextView phraseTextTextView;
		LabelLayout labelFlowLayout;
		TextView notesTextView;
		LeoToggleView soundToggleView;

		RadioButton[] optionRadioButtons;
		ImageView[] optionResultIcons;

		ImageView fillInResultIcon;
		View fillInShowResultButton;
		EditText keywordEditText;
		TextWatcherImpl keywordWatcher;
		View prevButton, nextButton;

		// mediaPlayer
		MediaPlayer mediaPlayer;
		AssetDescManager assetDescManager;

		int phraseIndex;
		List<PhraseTest> testPhrases;
		List<FilterableItem> unlabeledList;
		boolean isLastQuestShowed;

		public List<FilterableItem> getUnlabeledList() {
			if (unlabeledList == null) {
				unlabeledList = LabelUtils.getUnlabeledList();
			}
			return unlabeledList;
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);

			outState.putBoolean(STATE_SOUND_ENABLED, soundToggleView.isStateOn());
			outState.putInt(STATE_PHRASE_INDEX, phraseIndex);
			outState.putParcelableArrayList(STATE_TEST_PHRASES, (ArrayList<PhraseTest>) testPhrases);

			outState.putBoolean(STATE_LAST_QUEST_SHOWED, isLastQuestShowed);
		}

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
			// mediaPlayer
			MediaPlayerUtils.release(mediaPlayer);
			mediaPlayer = null;

			if (assetDescManager != null) {
				assetDescManager.tryClose();
				assetDescManager = null;
			}
			super.onPause();
		}

		public AssetDescManager getAssetDescManager() {
			if (assetDescManager == null) {
				assetDescManager = new AssetDescManager(getActivity(), 2);
			}
			return assetDescManager;
		}

		int getTestTypeId() {
			return getActivity().getIntent().getIntExtra(INTENT_PARAM_TEST_TYPE_ID, 0);
		}

		void playResultSound(boolean correct) {
			try {
				if (mediaPlayer == null) {
					mediaPlayer = new MediaPlayer();
				} else {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
					}
					mediaPlayer.reset();
				}

				AssetFileDescriptor asset = getAssetDescManager().getAssetDesc(correct, R.raw.ding, R.raw.oops);
				mediaPlayer.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());

				// Sound volumes
				mediaPlayer.setVolume(TESTING_SOUND_VOLUMN, TESTING_SOUND_VOLUMN);

				mediaPlayer.prepare();
				mediaPlayer.start();

			} catch (IOException ex) {
			}
		}

		void updateMtcSelectedOption(int selectedIndex, PhraseMtc model, boolean navigated) {
			// RadionButton checked
			optionRadioButtons[selectedIndex].setChecked(true);
			boolean correct = (selectedIndex == model.correctIndex);

			// resultIcon
			ImageView resultIcon = optionResultIcons[selectedIndex];
			resultIcon.setVisibility(View.VISIBLE);
			resultIcon.setImageResource(correct ? R.drawable.ic_test_correct : R.drawable.ic_test_wrong);

			// Play sound
			if (soundToggleView.isStateOn() && navigated == false) {
				playResultSound(correct);
			}
		}

		void updateMtcNotSelectedOption(int unselectedIndex, PhraseMtc model) {
			// RadionButton checked
			optionRadioButtons[unselectedIndex].setChecked(false);

			// resultIcon
			ImageView resultIcon = optionResultIcons[unselectedIndex];
			if (model.isKeywordAvailable(unselectedIndex)) {
				resultIcon.setVisibility(View.INVISIBLE);
			} else {
				// keyword unavailable
				resultIcon.setVisibility(View.VISIBLE);
				resultIcon.setImageResource(R.drawable.ic_keyword_unavailable);
			}
		}

		void updateMtcViews(PhraseMtc model, boolean navigated) {
			for (int index = 0; index < 4; index++) {
				if (index == model.selectedIndex) {
					updateMtcSelectedOption(index, model, navigated);
				} else {
					updateMtcNotSelectedOption(index, model);
				}
			}
			phraseTextTextView.setText(model.getPhraseSpannable(model.selectedIndex));
		}

		void modelToView_Mtc(PhraseMtc model, boolean navigated) {
			// masteryToggleView
			masteryToggleView.setStateOn(model.mastered);

			// labelList
			List<FilterableItem> labelList = model.getLabelList();
			if (labelList.isEmpty() == false) {
				labelFlowLayout.setLabelList(labelList);
			} else {
				labelFlowLayout.setLabelList(getUnlabeledList());
			}
			labelFlowLayout.createLabelViews();

			// notes
			if (model.notes.isEmpty() == false) {
				notesTextView.setText(model.notes);
				notesTextView.setVisibility(View.VISIBLE);
			} else {
				notesTextView.setVisibility(View.GONE);
			}

			// RadioButton: text & enabled
			for (int index = 0; index < 4; index++) {
				optionRadioButtons[index].setText(model.keywordOptions[index]);
				if (index > 0) {
					optionRadioButtons[index].setEnabled(model.isKeywordAvailable(index));
				}
			}

			// RadioButton: checked & resultIcon
			if (model.selectedIndex < 0) {
				for (int index = 0; index < 4; index++) {
					updateMtcNotSelectedOption(index, model);
				}
				phraseTextTextView.setText(PhraseUtils.createPhraseTextSpan(model.getPhraseSegs()));
			} else {
				updateMtcViews(model, navigated);
			}
		}

		void modelToView_FillIn(PhraseFillin model, boolean navigated) {
			// masteryToggleView
			masteryToggleView.setStateOn(model.mastered);

			// labelList
			List<FilterableItem> labelList = model.getLabelList();
			if (labelList.isEmpty() == false) {
				labelFlowLayout.setLabelList(labelList);
			} else {
				labelFlowLayout.setLabelList(getUnlabeledList());
			}
			labelFlowLayout.createLabelViews();

			// notes
			if (model.notes.isEmpty() == false) {
				notesTextView.setText(model.notes);
				notesTextView.setVisibility(View.VISIBLE);
			} else {
				notesTextView.setVisibility(View.GONE);
			}

			// keywordEditText
			keywordEditText.setText(model.answered);

			updateFillInViews(model, navigated);
		}

		void updateFillInViews(PhraseFillin model, boolean navigated) {
			// Not answered?
			if (model.answered == null) {
				fillInResultIcon.setVisibility(View.GONE);
				fillInShowResultButton.setVisibility(View.VISIBLE);

				phraseTextTextView.setText(PhraseUtils.createPhraseTextSpan(model.getPhraseSegs()));
			} else {
				// Answered
				boolean correct = model.key_word.equalsIgnoreCase(model.answered);

				fillInResultIcon.setVisibility(View.VISIBLE);
				fillInResultIcon.setImageResource(correct ? R.drawable.ic_test_correct : R.drawable.ic_test_wrong);
				fillInShowResultButton.setVisibility(correct ? View.INVISIBLE : View.VISIBLE);
				phraseTextTextView.setText(PhraseUtils.createPhraseTextSpan(model.getPhraseSegs(), model.answered, correct));

				// Play sound
				if (soundToggleView.isStateOn() && navigated == false) {
					if (correct) {
						playResultSound(true);
					} else {
						if (model.answered.length() >= 2) {
							playResultSound(false);
						}
					}
				}
			}
		}

		void updateNavigationViews() {
			int count = testPhrases.size();
			phraseIndexTextView.setText((phraseIndex + 1) + "/" + count);

			prevButton.setEnabled(phraseIndex > 0);
			nextButton.setEnabled(phraseIndex < count - 1);

			if (phraseIndex == count - 1) {
				if (isLastQuestShowed == false) {
					isLastQuestShowed = true;
					Toast.makeText(getActivity(), R.string.message_last_quest_showed, Toast.LENGTH_LONG).show();
				}
			}
		}

		void handlePrevNextClicked() {
			updateNavigationViews();

			// testTypeId
			int testTypeId = getTestTypeId();

			if (testTypeId == PhraseTestUtils.TEST_TYPE_MULTIPLE_CHOICE) {
				modelToView_Mtc((PhraseMtc) testPhrases.get(phraseIndex), true);

			} else if (testTypeId == PhraseTestUtils.TEST_TYPE_FILL_IN) {

				// Disabled keywordWatcher
				keywordEditText.removeTextChangedListener(keywordWatcher);

				modelToView_FillIn((PhraseFillin) testPhrases.get(phraseIndex), true);
				// Enable keywordWatcher
				keywordEditText.addTextChangedListener(keywordWatcher);

			} else {
				// TEST_TYPE_UNSPECIFIED
				PhraseTest model = testPhrases.get(phraseIndex);
				if (model instanceof PhraseMtc) {
					mtcOptionLayout.setVisibility(View.VISIBLE);
					fillInOptionLayout.setVisibility(View.GONE);

					modelToView_Mtc((PhraseMtc) model, true);

				} else {
					mtcOptionLayout.setVisibility(View.GONE);
					fillInOptionLayout.setVisibility(View.VISIBLE);

					// Disabled keywordWatcher
					keywordEditText.removeTextChangedListener(keywordWatcher);
					modelToView_FillIn((PhraseFillin) model, true);

					// Enable keywordWatcher
					keywordEditText.addTextChangedListener(keywordWatcher);
				}
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// Handle savedInstanceState
			if (savedInstanceState != null) {
				isLastQuestShowed = savedInstanceState.getBoolean(STATE_LAST_QUEST_SHOWED);

				phraseIndex = savedInstanceState.getInt(STATE_PHRASE_INDEX);
				testPhrases = ObjectUtils.castToList(savedInstanceState.getParcelableArrayList(STATE_TEST_PHRASES));
			} else {
				testPhrases = PhraseTestActivity.paramTestPhrases;
				PhraseTestActivity.paramTestPhrases = null;
			}

			final int testTypeId = getTestTypeId();

			// formView
			View formView = null;
			if (testTypeId == PhraseTestUtils.TEST_TYPE_MULTIPLE_CHOICE) {
				formView = inflater.inflate(R.layout.phrase_test_fragment_mtc_form, container, false);
			} else if (testTypeId == PhraseTestUtils.TEST_TYPE_FILL_IN) {
				formView = inflater.inflate(R.layout.phrase_test_fragment_fillin_form, container, false);
			} else {
				formView = inflater.inflate(R.layout.phrase_test_fragment_unspecified_form, container, false);
			}

			// mtcOptionLayout, fillInOptionLayout (Mixing Type)
			if (testTypeId == PhraseTestUtils.TEST_TYPE_MIXING_TYPE) {
				mtcOptionLayout = formView.findViewById(R.id.phrase_test_fragment_mtc_option_layout);
				fillInOptionLayout = formView.findViewById(R.id.phrase_test_fragment_fillin_option_layout);
			}

			// soundToggleView
			soundToggleView = (LeoToggleView) formView.findViewById(R.id.phrase_test_fragment_sound_toggle_view);
			if (savedInstanceState != null) {
				soundToggleView.setStateOn(savedInstanceState.getBoolean(STATE_SOUND_ENABLED));
			} else {
				boolean soundEnabled = PreferenceUtils.getBoolean(getActivity(), PhraseTestActivity.PREFERENCE_ID, PhraseTestActivity.PREFERENCE_TEST_SOUND_ENABLED, true);
				soundToggleView.setStateOn(soundEnabled);
			}

			// masteryToggleView
			masteryToggleView = (LeoToggleView) formView.findViewById(R.id.phrase_test_fragment_mastery_toggle_view);
			masteryToggleView.setToggleListener(new LeoToggleView.OnToggleListener() {

				@Override
				public void onToggle(View view, boolean isOn) {
					PhraseTest model = testPhrases.get(phraseIndex);
					if (model instanceof PhraseFillin) {
						((PhraseTestActivity) getActivity()).updateMasteredChange(model, isOn);
					} else {
						((PhraseTestActivity) getActivity()).updateMasteredChange(model, isOn);
					}
				}
			});

			// Others
			phraseIndexTextView = (TextView) formView.findViewById(R.id.phrase_test_fragment_phraseindex_textview);
			phraseTextTextView = (TextView) formView.findViewById(R.id.phrase_test_fragment_phrasetext_textview);
			notesTextView = (TextView) formView.findViewById(R.id.phrase_test_fragment_notes_textview);

			// labelFlowLayout
			labelFlowLayout = (LabelLayout) formView.findViewById(R.id.phrase_test_fragment_label_flow_layout);
			labelFlowLayout.setLayoutSizer(new LayoutSizerImpl(40));

			if (testTypeId == PhraseTestUtils.TEST_TYPE_MIXING_TYPE || testTypeId == PhraseTestUtils.TEST_TYPE_MULTIPLE_CHOICE) {
				optionRadioButtons = new RadioButton[4];
				optionRadioButtons[0] = (RadioButton) formView.findViewById(R.id.phrase_test_fragment_option1_radiobutton);
				optionRadioButtons[1] = (RadioButton) formView.findViewById(R.id.phrase_test_fragment_option2_radiobutton);
				optionRadioButtons[2] = (RadioButton) formView.findViewById(R.id.phrase_test_fragment_option3_radiobutton);
				optionRadioButtons[3] = (RadioButton) formView.findViewById(R.id.phrase_test_fragment_option4_radiobutton);

				optionResultIcons = new ImageView[4];
				optionResultIcons[0] = (ImageView) formView.findViewById(R.id.phrase_test_fragment_option1_result_imageview);
				optionResultIcons[1] = (ImageView) formView.findViewById(R.id.phrase_test_fragment_option2_result_imageview);
				optionResultIcons[2] = (ImageView) formView.findViewById(R.id.phrase_test_fragment_option3_result_imageview);
				optionResultIcons[3] = (ImageView) formView.findViewById(R.id.phrase_test_fragment_option4_result_imageview);
			}

			// optionResultIcon listeners
			if (optionResultIcons != null) {
				View.OnClickListener clickListener = new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						PhraseMtc model = (PhraseMtc) testPhrases.get(phraseIndex);
						int selectedIndex = (Integer) v.getTag();
						if (model.isKeywordAvailable(selectedIndex) == false) {
							Toast.makeText(getActivity(), getString(R.string.message_why_keyword_unavailable), Toast.LENGTH_SHORT).show();
						}
					}
				};
				for (int index = 0; index < 4; index++) {
					ImageView resultIcon = optionResultIcons[index];
					resultIcon.setTag(index);
					if (index > 0) {
						resultIcon.setOnClickListener(clickListener);
					}
				}
			}

			// optionRadioButton listeners
			if (optionRadioButtons != null) {
				View.OnClickListener clickListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PhraseMtc model = (PhraseMtc) testPhrases.get(phraseIndex);
						model.selectedIndex = (Integer) v.getTag();
						updateMtcViews(model, false);
					}
				};
				for (int index = 0; index < 4; index++) {
					RadioButton btn = optionRadioButtons[index];
					btn.setTag(index);
					btn.setOnClickListener(clickListener);
				}
			}

			// keywordEditText
			if (testTypeId == PhraseTestUtils.TEST_TYPE_MIXING_TYPE || testTypeId == PhraseTestUtils.TEST_TYPE_FILL_IN) {
				keywordEditText = (EditText) formView.findViewById(R.id.phrase_test_fragment_keyword_edittext);
				keywordWatcher = new TextWatcherImpl() {

					@Override
					public void afterTextChanged(Editable answer) {
						// Update PhraseFillin model
						PhraseFillin model = (PhraseFillin) testPhrases.get(phraseIndex);
						model.answered = StringUtils.trimToNull(answer.toString());

						updateFillInViews(model, false);
					}
				};

				keywordEditText.addTextChangedListener(keywordWatcher);

				fillInResultIcon = (ImageView) formView.findViewById(R.id.phrase_test_fragment_result_icon);
				fillInShowResultButton = formView.findViewById(R.id.phrase_test_fragment_showresult_button);
				fillInShowResultButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						PhraseFillin model = (PhraseFillin) testPhrases.get(phraseIndex);

						keywordEditText.setText(model.key_word);
						fillInResultIcon.setVisibility(View.VISIBLE);
						fillInResultIcon.setImageResource(R.drawable.ic_test_correct);
						fillInShowResultButton.setVisibility(View.INVISIBLE);
					}
				});
			}

			// nextButton
			nextButton = formView.findViewById(R.id.phrase_test_fragment_next_button);
			nextButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (phraseIndex < testPhrases.size() - 1) {
						phraseIndex++;
						handlePrevNextClicked();
					}
				}
			});

			// prevButton
			prevButton = formView.findViewById(R.id.phrase_test_fragment_prev_button);
			prevButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (phraseIndex > 0) {
						phraseIndex--;
						handlePrevNextClicked();
					}
				}
			});

			// Model to views
			updateNavigationViews();

			if (testTypeId == PhraseTestUtils.TEST_TYPE_MULTIPLE_CHOICE) {
				modelToView_Mtc((PhraseMtc) testPhrases.get(phraseIndex), true);

			} else if (testTypeId == PhraseTestUtils.TEST_TYPE_FILL_IN) {
				modelToView_FillIn((PhraseFillin) testPhrases.get(phraseIndex), true);

			} else {
				// TEST_TYPE_UNSPECIFIED
				PhraseTest model = testPhrases.get(phraseIndex);
				if (model instanceof PhraseMtc) {
					mtcOptionLayout.setVisibility(View.VISIBLE);
					fillInOptionLayout.setVisibility(View.GONE);

					modelToView_Mtc((PhraseMtc) model, true);
				} else {
					mtcOptionLayout.setVisibility(View.GONE);
					fillInOptionLayout.setVisibility(View.VISIBLE);

					modelToView_FillIn((PhraseFillin) model, true);
				}
			}
			return formView;
		}
	}
}