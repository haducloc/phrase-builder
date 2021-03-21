package com.appslandia.core.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.res.Resources;

public abstract class AsyncLoader<E> extends AsyncTaskLoader<E> {

	protected E data;

	protected abstract E loadData();

	public AsyncLoader(Context context) {
		super(context);
	}

	@Override
	public E loadInBackground() {
		return this.loadData();
	}

	protected void release(E data) {
	}

	protected Resources getLoaderResources() {
		return this.getContext().getResources();
	}

	/**
	 * Called when there is new data to deliver to the client. The super class will take care of delivering it; the implementation here just adds a little more logic.
	 */
	@Override
	public void deliverResult(E data) {
		if (isReset()) {
			if (data != null) {
				release(data);
			}
			return;
		}

		E oldData = this.data;
		this.data = data;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(data);
		}

		if ((oldData != null) && (oldData != data)) {
			release(oldData);
		}
	}

	protected void registerObservers() {
	}

	protected void unregisterObservers() {
	}

	/**
	 * Starts an asynchronous load of the data. When the result is ready the callbacks will be called on the UI thread. If a previous load has been completed and is still valid the result may be passed to the callbacks immediately.
	 * 
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading() {
		if (this.data != null) {
			// If we currently have a result available, deliver it immediately.
			deliverResult(this.data);
		}

		// Register the observers
		registerObservers();

		if (takeContentChanged() || (this.data == null)) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	public void onCanceled(E data) {
		super.onCanceled(data);

		if (data != null) {
			release(data);
		}
	}

	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		if (this.data != null) {
			release(this.data);

			this.data = null;
		}

		// Unregister the observers
		unregisterObservers();
	}
}
