package com.appslandia.core.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class LeoListView extends ListView {

	protected View loadingView;
	protected boolean loading = false;

	public LeoListView(Context context) {
		super(context);
	}

	public LeoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LeoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setLoadingView(View view) {
		view.setVisibility(View.GONE);
		this.loadingView = view;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
		this.loadingView.setVisibility(loading ? View.VISIBLE : View.GONE);
	}

	public abstract static class ScrollListener implements AbsListView.OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == SCROLL_STATE_IDLE) {

				LeoListView listView = (LeoListView) view;
				if (listView.loading) {
					return;
				}

				final int lastIndex = view.getAdapter().getCount() - 1;
				if (view.getLastVisiblePosition() == lastIndex) {

					listView.setLoading(true);
					loadMoreItems();
				}
			}
		}

		protected abstract void loadMoreItems();
	}

}