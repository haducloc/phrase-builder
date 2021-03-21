package com.appslandia.core.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class QueryParams implements Serializable {
	private static final long serialVersionUID = 1L;

	protected final Map<String, String> entriesMap;

	public QueryParams() {
		this(16);
	}

	public QueryParams(int initialCapacity) {
		this.entriesMap = this.createEntryMap(initialCapacity);
	}

	protected Map<String, String> createEntryMap(int initialCapacity) {
		return new HashMap<>(initialCapacity);
	}

	public void put(String key, String value) {
		this.entriesMap.put(key, value);
	}

	public void put(String key, int value) {
		this.entriesMap.put(key, String.valueOf(value));
	}

	public void put(String key, long value) {
		this.entriesMap.put(key, String.valueOf(value));
	}

	public void put(String key, boolean value) {
		this.entriesMap.put(key, String.valueOf(value));
	}

	public String getString(String key) {
		return this.entriesMap.get(key);
	}

	public String getString(String key, String def) {
		String value = this.entriesMap.get(key);
		return (value != null) ? value : def;
	}

	public void parse(String queryString) {
		if (queryString == null) {
			return;
		}
		String[] entries = SplitterUtils.splitAndRemoveEmptyEntries(queryString, '&');
		for (String entry : entries) {
			int idx = entry.indexOf('=');
			if (idx > 0) {
				String name = entry.substring(0, idx);
				String value = null;
				if (idx < entry.length() - 1) {
					value = URLUtils.decodeQueryParam(entry.substring(idx + 1));
				}
				this.entriesMap.put(name, value);
			}
		}
	}

	public String toQueryString() {
		StringBuilder sb = new StringBuilder();
		boolean firstParam = true;
		for (Map.Entry<String, String> entry : this.entriesMap.entrySet()) {
			if (entry.getValue() != null) {
				if (firstParam) {
					firstParam = false;
				} else {
					sb.append('&');
				}
				sb.append(entry.getKey()).append('=').append(URLUtils.encodeQueryParam(entry.getValue()));
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return this.toQueryString();
	}
}
