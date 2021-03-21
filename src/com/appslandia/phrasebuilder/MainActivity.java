package com.appslandia.phrasebuilder;

import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.core.utils.BackupUtils;
import com.appslandia.core.utils.PreferenceUtils;
import com.appslandia.core.views.OnSingleClickListener_PostHandler;
import com.appslandia.core.views.PressAgainActivity;
import com.appslandia.core.views.RateUsDialog;
import com.appslandia.phrasebuilder.utils.PhraseBuilderUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends PressAgainActivity implements FirstTimeUsersDialogImpl.Callbacks, RateUsDialog.Callbacks {

	// Preferences
	public static final String PREFERENCE_ID = MainActivity.class.getSimpleName();
	public static final String PREFERENCE_LEARN_WORKFLOW = "learnWorkflowShowed";

	public static final String PREFERENCE_RATE_US_DISABLED = "rateUsDisabled";
	public static final String PREFERENCE_RATE_US_LATERS = "rateUsLaters";
	public static final String PREFERENCE_RATE_US_LAST_COUNT = "rateUsLastCount";

	// Transients
	public static final String STATE_SHOW_RATE_US_TIMESTAMP = "showRateUsTimestamp";

	// Rate us
	private Runnable showRateUsRunnable = null;
	private long showRateUsTimestamp;

	public static final long RATE_US_DIALOG_AFTER_USES = 50;
	public static final long RATE_US_DIALOG_NO_AFTER_LATERS = 5;
	public static final long RATE_US_DIALOG_DELAY_MS = 1500;

	// Handler
	final Handler handler = new Handler();
	final OnSingleClickListener_PostHandler menuClickListener = new OnSingleClickListener_PostHandler() {

		@Override
		protected void onSingleClick(View v) {
			final int id = v.getId();
			Intent intent = null;

			switch (id) {
			case R.id.main_menu_manage_phrases_panel:
				intent = new Intent(MainActivity.this, PhraseListActivity.class);
				break;
			case R.id.main_menu_new_phrase_panel:
				intent = new Intent(MainActivity.this, PhraseEditActivity.class);
				break;
			case R.id.main_menu_test_phrases_panel:
				intent = new Intent(MainActivity.this, PhraseTestInputsActivity.class);
				break;
			case R.id.main_menu_manage_trash_panel:
				intent = new Intent(MainActivity.this, PhraseTrashActivity.class);
				break;
			case R.id.main_menu_languages_panel:
				intent = new Intent(MainActivity.this, LanguageListActivity.class);
				break;
			case R.id.main_menu_labels_panel:
				intent = new Intent(MainActivity.this, LabelListActivity.class);
				break;
			case R.id.main_menu_manage_backup_panel:
				intent = new Intent(MainActivity.this, ManageBackupActivity.class);
				break;
			case R.id.main_menu_share_us_panel:
				PhraseBuilderUtils.shareUs(MainActivity.this);
				return;
			case R.id.main_menu_our_products_panel:
				intent = new Intent(MainActivity.this, AppListActivityImpl.class);
				break;
			case R.id.main_menu_about_us_panel:
				intent = new Intent(MainActivity.this, AboutActivityImpl.class);
				break;
			default:
				return;
			}
			if (intent != null) {
				startActivity(intent);
			}
		}

		@Override
		protected Handler getPostHandler() {
			return handler;
		}
	};

	@Override
	protected void onDestroy() {
		menuClickListener.removeCallbacks();

		if (showRateUsRunnable != null) {
			this.handler.removeCallbacks(showRateUsRunnable);
		}

		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// showRateUsTimestamp
		outState.putLong(STATE_SHOW_RATE_US_TIMESTAMP, showRateUsTimestamp);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		// Initialize Activity
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.ActivityListBackgroundColor));

		// Main Menus
		findViewById(R.id.main_menu_manage_phrases_panel).setOnClickListener(menuClickListener);
		findViewById(R.id.main_menu_new_phrase_panel).setOnClickListener(menuClickListener);

		findViewById(R.id.main_menu_test_phrases_panel).setOnClickListener(menuClickListener);
		findViewById(R.id.main_menu_manage_trash_panel).setOnClickListener(menuClickListener);

		findViewById(R.id.main_menu_languages_panel).setOnClickListener(menuClickListener);
		findViewById(R.id.main_menu_labels_panel).setOnClickListener(menuClickListener);

		findViewById(R.id.main_menu_manage_backup_panel).setOnClickListener(menuClickListener);
		findViewById(R.id.main_menu_share_us_panel).setOnClickListener(menuClickListener);

		findViewById(R.id.main_menu_our_products_panel).setOnClickListener(menuClickListener);
		findViewById(R.id.main_menu_about_us_panel).setOnClickListener(menuClickListener);

		// showRateUsTimestamp
		this.showRateUsTimestamp = (savedInstanceState == null) ? System.currentTimeMillis() : savedInstanceState.getLong(STATE_SHOW_RATE_US_TIMESTAMP);

		// tryShowLearnWorkflow
		tryShowLearnWorkflow();

		// tryShowRateUs
		this.tryShowRateUs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_help) {
			onActionHelp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	void onActionHelp() {
		Intent intent = new Intent(this, HelpActivityImpl.class);
		startActivity(intent);
	}

	//
	// -------------------- Rate Us --------------------
	//

	Runnable getRateUsRunnable() {
		if (showRateUsRunnable == null) {
			showRateUsRunnable = new Runnable() {

				@Override
				public void run() {
					if (MainActivity.this.isStateResumed()) {
						RateUsDialog dlg = new RateUsDialog();
						int rateUsLaters = PreferenceUtils.getInt(MainActivity.this, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_LATERS, 0);

						dlg.setAddNoThanks(rateUsLaters >= RATE_US_DIALOG_NO_AFTER_LATERS);
						dlg.setUncancelable().showAllowingStateLoss(getFragmentManager());
					}
				}
			};
		}
		return showRateUsRunnable;
	}

	void tryShowRateUs() {
		boolean rateUsDisabled = PreferenceUtils.getBoolean(this, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_DISABLED, false);
		if (rateUsDisabled == false) {
			int rateUsLastCount = PreferenceUtils.getInt(this, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_LAST_COUNT, 0);
			if (rateUsLastCount >= RATE_US_DIALOG_AFTER_USES) {
				final long showingTime = System.currentTimeMillis() - this.showRateUsTimestamp;
				this.handler.postDelayed(getRateUsRunnable(), RATE_US_DIALOG_DELAY_MS - showingTime);
			} else {
				PreferenceUtils.savePreference(this, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_LAST_COUNT, ++rateUsLastCount);
			}
		}
	}

	@Override
	public void onRateUsDialogResult(int button) {
		if (button == DialogInterface.BUTTON_POSITIVE) {
			// Rate now
			PreferenceUtils.savePreference(this, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_DISABLED, true);

			ActivityUtils.navigateToApp(this);

		} else if (button == DialogInterface.BUTTON_NEUTRAL) {

			// Later - Reset rateUsLastCount
			PreferenceUtils.savePreference(this, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_LAST_COUNT, (0));

			// Save rateUsLaters
			int rateUsLaters = PreferenceUtils.getInt(this, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_LATERS, 0);
			PreferenceUtils.savePreference(this, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_LATERS, ++rateUsLaters);

		} else if (button == DialogInterface.BUTTON_NEGATIVE) {
			// No thanks
			PreferenceUtils.savePreference(this, MainActivity.PREFERENCE_ID, MainActivity.PREFERENCE_RATE_US_DISABLED, true);
		}
	}

	//
	// -------------------- Learn Work-flow --------------------
	//

	void tryShowLearnWorkflow() {
		boolean learnWorkflow = PreferenceUtils.getBoolean(this, PREFERENCE_ID, PREFERENCE_LEARN_WORKFLOW, false);
		if (learnWorkflow == false) {
			FirstTimeUsersDialogImpl dlg = new FirstTimeUsersDialogImpl();
			dlg.setUncancelable();
			dlg.show(getFragmentManager());
		}
	}

	@Override
	public void onFirstTimeUsersClosed() {
		synchronized (BackupUtils.MUTEX) {
			PreferenceUtils.savePreference(this, PREFERENCE_ID, PREFERENCE_LEARN_WORKFLOW, true);
		}
	}
}