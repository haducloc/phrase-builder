package com.appslandia.core.views;

import java.util.List;

import com.appslandia.core.adapters.AboutAdapter;
import com.appslandia.core.adapters.AboutItem;
import com.appslandia.core.utils.ActivityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getActivityLayoutId());

		// Initialize ActionBar
		initActionBar();

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(getContainerViewId(), new PlaceholderFragment()).commit();
		}
	}

	protected void initActionBar() {
		ActivityUtils.initActionBarUp(getActionBar());
	}

	protected abstract int getActivityLayoutId();

	protected abstract int getContainerViewId();

	protected abstract int getListItemResourceId();

	protected abstract List<AboutItem> getListItems();

	protected abstract void onListItemSelected(AboutItem item);

	protected BaseAdapter createAdapter() {
		return new AboutAdapter<AboutItem>(this, this.getListItemResourceId(), this.getListItems());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class PlaceholderFragment extends AbstractListFragment {

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			AboutActivity aboutActivity = (AboutActivity) getActivity();
			setListAdapter(aboutActivity.createAdapter());
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			AboutItem item = (AboutItem) l.getItemAtPosition(position);
			((AboutActivity) getActivity()).onListItemSelected(item);
		}
	}
}