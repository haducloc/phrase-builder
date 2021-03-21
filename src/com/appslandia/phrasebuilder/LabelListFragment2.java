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

public class LabelListFragment2 extends AbstractListFragment implements LoaderManager.LoaderCallbacks<Object>, LabelListActivity.LabelChangedCallbacks {

	// Arguments
	public static final String ARGUMENT_LANGUAGE_ID = "languageId";

	// Transients
	public static final String STATE_LIST_VIEW_POS = "listViewPos";

	// Loaders
	public static final int LOADER_LABELS = 1;
	public static final String LOADER_LABEL_STAT_PARAM_LANGUAGE_ID = "languageId";

	@Override
	public void loadLabels() {
		this.setListShown(false);

		Bundle args = new Bundle(1);
		args.putInt(LOADER_LABEL_STAT_PARAM_LANGUAGE_ID, getLanguageIdParam());
		getLoaderManager().restartLoader(LOADER_LABELS, args, this);
	}

	public Bundle getInputArgs() {
		Bundle args = getArguments();
		if (args == null) {
			args = new Bundle(1);
			setArguments(args);
		}
		return args;
	}

	int getLanguageIdParam() {
		return getInputArgs().getInt(ARGUMENT_LANGUAGE_ID, 0);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Label label = (Label) l.getItemAtPosition(position);
		((LabelListItemCallbacks) getActivity()).onAdapterClickLabel(label);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(STATE_LIST_VIEW_POS, this.createListViewPos());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Empty Message
		setEmptyText(getString(R.string.message_no_labels));

		// List Divider
		ViewUtils.setTransparentDivider(mList, 8); // 8dp

		// labelsAdapter
		LabelListAdapter2 labelsAdapter = new LabelListAdapter2(getActivity(), R.layout.label_list_activity_list_item2, new ArrayList<Label>());
		setListAdapter(labelsAdapter);
		setListShown(false);

		// savedInstanceState
		if (savedInstanceState != null) {

			// ListViewPos
			ListViewPos pos = savedInstanceState.getParcelable(STATE_LIST_VIEW_POS);
			this.mList.setSelectionFromTop(pos.getPosition(), pos.getTopY());
		}

		// Load labels
		Bundle args = new Bundle(1);
		args.putInt(LOADER_LABEL_STAT_PARAM_LANGUAGE_ID, getLanguageIdParam());
		getLoaderManager().initLoader(LOADER_LABELS, args, this);
	}

	@Override
	public Loader<Object> onCreateLoader(int id, final Bundle args) {

		Loader<Object> loader = new AsyncLoader<Object>(getActivity()) {
			@Override
			protected Object loadData() {
				int languageId = args.getInt(LOADER_LABEL_STAT_PARAM_LANGUAGE_ID);
				return LabelDao.queryStat(DbManager.openRead(getContext()), languageId);
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