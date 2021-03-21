package com.appslandia.core.views;

import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.phrasebuilder.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class AbstractListFragment extends Fragment {
	final protected Handler mHandler = new Handler();

	final private Runnable mRequestFocus = new Runnable() {
		public void run() {
			mList.focusableViewAvailable(mList);
		}
	};

	final private OnListItemClickListener mOnClickListener = new OnListItemClickListener() {

		@Override
		protected void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
			onListItemClick((ListView) parent, view, position, id);
		}
	};

	protected BaseAdapter mAdapter;
	protected ListView mList;

	TextView mStandardEmptyView;
	View mProgressContainer;
	View mListContainer;
	CharSequence mEmptyText;
	boolean mListShown;

	public AbstractListFragment() {
	}

	protected int getViewResourceId() {
		return R.layout.libs_list_fragment;
	}

	protected void initializeView(View root) {
	}

	protected ListViewPos createListViewPos() {
		View view = mList.getChildAt(0);
		return new ListViewPos(mList.getFirstVisiblePosition(), (view != null) ? view.getTop() : (0));
	}

	protected void updateListShown(boolean shown) {
		if (isResumed()) {
			setListShown(shown);
		} else {
			setListShownNoAnimation(shown);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> boolean checkObjects(List<T> objects) {
		return ((ArrayAdapterImpl<T>) mAdapter).checkObjects(objects);
	}

	@SuppressWarnings("unchecked")
	public <T> boolean setObjects(List<T> objects) {
		return ((ArrayAdapterImpl<T>) mAdapter).setObjects(objects);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getViewResourceId(), container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ensureList();
	}

	@Override
	public void onDestroyView() {
		mHandler.removeCallbacks(mRequestFocus);
		mOnClickListener.removeCallbackOn(mList);

		mList = null;
		mListShown = false;
		mProgressContainer = mListContainer = null;
		mStandardEmptyView = null;

		super.onDestroyView();
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
	}

	public void setListAdapter(BaseAdapter adapter) {
		boolean hadAdapter = mAdapter != null;
		mAdapter = adapter;
		if (mList != null) {
			mList.setAdapter(adapter);
			if (!mListShown && !hadAdapter) {
				// The list was hidden, and previously didn't have an
				// adapter. It is now time to show it.
				setListShown(true, getView().getWindowToken() != null);
			}
		}
	}

	public void setSelection(int position) {
		ensureList();
		mList.setSelection(position);
	}

	public int getSelectedItemPosition() {
		ensureList();
		return mList.getSelectedItemPosition();
	}

	public long getSelectedItemId() {
		ensureList();
		return mList.getSelectedItemId();
	}

	public ListView getListView() {
		ensureList();
		return mList;
	}

	public void setEmptyText(CharSequence text) {
		ensureList();
		mStandardEmptyView.setText(text);
		if (mEmptyText == null) {
			mList.setEmptyView(mStandardEmptyView);
		}
		mEmptyText = text;
	}

	public void setListShown(boolean shown) {
		setListShown(shown, true);
	}

	public void setListShownNoAnimation(boolean shown) {
		setListShown(shown, false);
	}

	private void setListShown(boolean shown, boolean animate) {
		ensureList();
		if (mListShown == shown) {
			return;
		}
		mListShown = shown;
		if (shown) {
			if (animate) {
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
				mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			} else {
				mProgressContainer.clearAnimation();
				mListContainer.clearAnimation();
			}
			mProgressContainer.setVisibility(View.GONE);
			mListContainer.setVisibility(View.VISIBLE);
		} else {
			if (animate) {
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
				mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
			} else {
				mProgressContainer.clearAnimation();
				mListContainer.clearAnimation();
			}
			mProgressContainer.setVisibility(View.VISIBLE);
			mListContainer.setVisibility(View.GONE);
		}
	}

	public ListAdapter getListAdapter() {
		return mAdapter;
	}

	private void ensureList() {
		if (mList != null) {
			return;
		}
		View root = getView();

		mStandardEmptyView = (TextView) root.findViewById(R.id.libs_list_fragment_empty_textview);
		mStandardEmptyView.setVisibility(View.GONE);

		mProgressContainer = root.findViewById(R.id.libs_list_fragment_progress_container);
		mListContainer = root.findViewById(R.id.libs_list_fragment_list_container);
		mList = (ListView) root.findViewById(R.id.libs_list_fragment_list_view);

		this.initializeView(root);

		if (mEmptyText != null) {
			mStandardEmptyView.setText(mEmptyText);
			mList.setEmptyView(mStandardEmptyView);
		}

		mListShown = true;
		mList.setOnItemClickListener(mOnClickListener);
		if (mAdapter != null) {
			BaseAdapter adapter = mAdapter;
			mAdapter = null;
			setListAdapter(adapter);
		} else {
			// We are starting without an adapter, so assume we won't
			// have our data right away and start with the progress indicator.
			if (mProgressContainer != null) {
				setListShown(false, false);
			}
		}
		mHandler.post(mRequestFocus);
	}
}