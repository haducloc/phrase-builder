package com.appslandia.core.views;

import android.os.AsyncTask;

public class TaskFragment extends RetainedFragment {

	public static interface Callbacks {

		Object onTaskExecute(int taskId, Object[] params);

		void onTaskExecuted(int taskId, Object result);
	}

	protected Callbacks getCallbacks() {
		if (isHostFragment()) {
			return (Callbacks) getTargetFragment();
		} else {
			return (Callbacks) getActivity();
		}
	}

	public void execute(int taskId, Object... params) {
		new AsyncTaskImpl(taskId, false).execute(params);
	}

	public void executeLoading(int taskId, Object... params) {
		new AsyncTaskImpl(taskId, true).execute(params);
	}

	private class AsyncTaskImpl extends AsyncTask<Object, Integer, Object> {
		final int taskId;
		final boolean loading;

		public AsyncTaskImpl(int taskId, boolean loading) {
			this.taskId = taskId;
			this.loading = loading;
		}

		protected void showLoading() {
			new Loading().setUncancelable().showAllowingStateLoss(getFragmentManager());
		}

		protected void dismissLoading() {
			DialogImpl.dismiss(getFragmentManager(), Loading.class);
		}

		@Override
		protected void onPreExecute() {
			if (this.loading) {
				showLoading();
			}
		}

		@Override
		protected Object doInBackground(Object... params) {
			return getCallbacks().onTaskExecute(this.taskId, params);
		}

		@Override
		protected void onPostExecute(Object result) {
			if (this.loading) {
				dismissLoading();
			}
			Callbacks callbacks = getCallbacks();
			if (callbacks != null) {
				callbacks.onTaskExecuted(this.taskId, result);
			}
		}
	}
}
