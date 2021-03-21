package com.appslandia.core.views;

import com.appslandia.core.utils.ActivityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public abstract class HelpActivity extends Activity {

	public static final String INTENT_SECTION_ID = "sectionId";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getActivityLayoutId());

		// Initialize ActionBar
		initActionBar();

		// Initialize Activity
		initActivityProps();

		// Initialize WebView
		initWebView(getIntent().getStringExtra(INTENT_SECTION_ID));
	}

	protected abstract int getActivityLayoutId();

	protected abstract void initWebView(String sectionId);

	protected void initActionBar() {
		ActivityUtils.initActionBarUp(getActionBar());
	}

	protected abstract void initActivityProps();

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
