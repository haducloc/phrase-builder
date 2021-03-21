package com.appslandia.core.views;

import android.os.AsyncTask;

public abstract class TaskFragment2 extends RetainedFragment {

	public static interface Callbacks {

		void onTaskExecuted(int taskId, Object result);
	}

	protected Callbacks getCallbacks() {
		if (isHostFragment()) {
			return (Callbacks) getTargetFragment();
		} else {
			return (Callbacks) getActivity();
		}
	}

	protected abstract class AsyncTaskImpl extends AsyncTask<Object, Integer, Object> {

		final int taskId;
		final boolean loading;

		public AsyncTaskImpl(int taskId) {
			this(taskId, true);
		}

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
