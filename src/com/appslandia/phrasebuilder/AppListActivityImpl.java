package com.appslandia.phrasebuilder;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.AboutItem;
import com.appslandia.core.adapters.AppItem;
import com.appslandia.core.views.AppListActivity;

import android.content.res.Resources;
import android.os.Bundle;

public class AppListActivityImpl extends AppListActivity {

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
		return R.layout.app_list_activity;
	}

	@Override
	protected int getContainerViewId() {
		return R.id.container;
	}

	@Override
	protected int getListItemResourceId() {
		return R.layout.libs_app_list_activity_list_item;
	}

	@Override
	protected List<AboutItem> getListItems() {
		List<AboutItem> items = new ArrayList<>();

		Resources res = getResources();
		items.add(new AppItem(res.getString(R.string.app_item_phrasebuilder), res.getString(R.string.app_item_phrasebuilder_desc), getPackageName(), R.drawable.ic_app_phrasebuilder));

		return items;
	}
}
