package com.appslandia.phrasebuilder;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.loaders.AsyncLoader;
import com.appslandia.core.utils.ViewUtils;
import com.appslandia.core.views.AbstractListFragment;
import com.appslandia.core.views.ListViewPos;
import com.appslandia.phrasebuilder.entities.Label;
import com.appslandia.phrasebuilder.sqlite.DbManager;
import com.appslandia.phrasebuilder.sqlite.LabelDao;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class LabelListFragment1 extends AbstractListFragment implements LoaderManager.LoaderCallbacks<Object>, LabelListActivity.LabelChangedCallbacks {

	// Loaders
	public static final int LOADER_LABELS = 1;

	// Transients
	public static final String STATE_LABEL_LIST_VIEW_POS = "labelListViewPos";

	@Override
	public void loadLabels() {
		this.setListShown(false);
		getLoaderManager().restartLoader(LOADER_LABELS, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Label label = (Label) l.getItemAtPosition(position);
		((LabelListItemCallbacks) getActivity()).onAdapterClickLabel(label);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(STATE_LABEL_LIST_VIEW_POS, createListViewPos());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Empty Message
		setEmptyText(getString(R.string.message_no_labels));

		// List Divider
		ViewUtils.setTransparentDivider(mList, 8); // 8dp

		// labelsAdapter
		LabelListAdapter1 labelsAdapter = new LabelListAdapter1(getActivity(), R.layout.label_list_activity_list_item1, new ArrayList<Label>());
		setListAdapter(labelsAdapter);
		setListShown(false);

		// savedInstanceState
		if (savedInstanceState != null) {

			// ListViewPos
			ListViewPos pos = savedInstanceState.getParcelable(STATE_LABEL_LIST_VIEW_POS);
			this.mList.setSelectionFromTop(pos.getPosition(), pos.getTopY());
		}

		// Load labels
		getLoaderManager().initLoader(LOADER_LABELS, null, this);
	}

	@Override
	public Loader<Object> onCreateLoader(int id, Bundle args) {
		Loader<Object> loader = new AsyncLoader<Object>(getActivity()) {
			@Override
			protected Object loadData() {
				return LabelDao.queryLabels(DbManager.openRead(getContext()));
			}
		};
		return loader;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLoadFinished(Loader<Object> loader, Object data) {
		List<Label> labels = (List<Label>) data;
		boolean updateObjects = this.checkObjects(labels);
		if (updateObjects) {
			// Update labels
			setObjects(labels);

			// Notify labels updated
			this.mAdapter.notifyDataSetChanged();
		}
		updateListShown(true);
	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {
		setObjects(null);
	}
}
