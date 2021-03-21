package com.appslandia.core.views;

import com.appslandia.core.adapters.AboutItem;
import com.appslandia.core.adapters.AppItem;
import com.appslandia.core.adapters.AppListAdapter;
import com.appslandia.core.utils.ActivityUtils;

import android.widget.BaseAdapter;

public abstract class AppListActivity extends AboutActivity {

	@Override
	protected BaseAdapter createAdapter() {
		return new AppListAdapter<AboutItem>(this, this.getListItemResourceId(), this.getListItems());
	}

	@Override
	protected void onListItemSelected(AboutItem item) {
		ActivityUtils.navigateToApp(this, ((AppItem) item).packageName);
	}
}