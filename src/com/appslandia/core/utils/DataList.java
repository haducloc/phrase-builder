package com.appslandia.core.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Bundle;

public class DataList<T> {
	private static final AtomicInteger idSeq = new AtomicInteger(0);

	private final int id;
	private List<T> objects;
	private Bundle args;

	public DataList() {
		id = idSeq.incrementAndGet();
	}

	public int getId() {
		return id;
	}

	public List<T> getObjects() {
		return objects;
	}

	public void setObjects(List<T> objects) {
		this.objects = objects;
	}

	public int getCount() {
		return this.objects != null ? this.objects.size() : 0;
	}

	public void setArguments(Bundle args) {
		this.args = args;
	}

	public int getInt(String key) {
		return this.args.getInt(key);
	}

	public int getInt(String key, int defaultValue) {
		return this.args.getInt(key, defaultValue);
	}

	public String getString(String key) {
		return this.args.getString(key);
	}

	public String getString(String key, String defaultValue) {
		return this.args.getString(key, defaultValue);
	}

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}
}
