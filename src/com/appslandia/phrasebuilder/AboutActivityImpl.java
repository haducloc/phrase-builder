package com.appslandia.phrasebuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.appslandia.core.adapters.AboutItem;
import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.core.utils.AppsLandiaUtils;
import com.appslandia.core.views.AboutActivity;
import com.appslandia.phrasebuilder.utils.PhraseBuilderUtils;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Bundle;

public class AboutActivityImpl extends AboutActivity {

	public static final int MENU_VERSION = 1;
	public static final int MENU_RATE_US = 2;
	public static final int MENU_SHARE_US = 3;
	public static final int MENU_FEEDBACK = 4;
	public static final int MENU_OUR_PRODUCTS = 5;
	public static final int MENU_DEV = 6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize Activity
		initActivityProps();
	}

	protected void initActivityProps() {
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.ActivityListBackgroundColor));
	}

	@Override
	protected int getActivityLayoutId() {
		return R.layout.about_activity;
	}

	@Override
	protected int getContainerViewId() {
		return R.id.container;
	}

	@Override
	protected int getListItemResourceId() {
		return R.layout.libs_about_activity_list_item;
	}

	@Override
	protected List<AboutItem> getListItems() {
		List<AboutItem> items = new ArrayList<>();

		Resources res = getResources();

		// versionDesc
		PackageInfo info = ActivityUtils.getPackageInfo(this);
		String versionDesc = String.format(Locale.ENGLISH, "%s.%d", info.versionName, info.versionCode);

		items.add(new AboutItem(MENU_VERSION, res.getString(R.string.about_item_version), versionDesc));
		items.add(new AboutItem(MENU_RATE_US, res.getString(R.string.about_item_rateus), res.getString(R.string.about_item_rateus_desc)));
		items.add(new AboutItem(MENU_SHARE_US, res.getString(R.string.about_item_shareus), res.getString(R.string.about_item_shareus_desc)));
		items.add(new AboutItem(MENU_FEEDBACK, res.getString(R.string.about_item_feedback), res.getString(R.string.about_item_feedback_desc)));
		items.add(new AboutItem(MENU_OUR_PRODUCTS, res.getString(R.string.about_item_our_products), res.getString(R.string.about_item_our_products_desc)));
		items.add(new AboutItem(MENU_DEV, res.getString(R.string.about_item_dev), res.getString(R.string.about_item_dev_desc)));

		return items;
	}

	@Override
	protected void onListItemSelected(AboutItem item) {
		if (item._id == MENU_VERSION) {
			return;
		}
		if (item._id == MENU_RATE_US) {
			ActivityUtils.navigateToApp(this);
			return;
		}
		if (item._id == MENU_SHARE_US) {
			PhraseBuilderUtils.shareUs(this);
			return;
		}
		if (item._id == MENU_FEEDBACK) {
			feedback();
			return;
		}
		if (item._id == MENU_OUR_PRODUCTS) {
			openOurProducts();
			return;
		}
		if (item._id == MENU_DEV) {
			return;
		}
	}

	void feedback() {
		Bundle params = new Bundle(2);

		params.putStringArray(Intent.EXTRA_EMAIL, new String[] { AppsLandiaUtils.APPS_LANDIA_EMAIL });
		params.putString(Intent.EXTRA_SUBJECT, getString(R.string.feedback_email_subject));

		ActivityUtils.emailUs(this, params);
	}

	void openOurProducts() {
		Intent intent = new Intent(this, AppListActivityImpl.class);
		startActivity(intent);
	}
}
