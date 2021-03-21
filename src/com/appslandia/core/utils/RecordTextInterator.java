package com.appslandia.core.utils;

import java.util.Iterator;

public class RecordTextInterator implements Iterator<String> {

	private final String text;
	private final char separator;

	int index1 = 0;
	int index2 = 0;

	public RecordTextInterator(String text, char separator) {
		this.text = text;
		this.separator = separator;
	}

	@Override
	public boolean hasNext() {
		if (text.isEmpty())
			return false;

		this.index2 = this.text.indexOf(this.separator, this.index1);
		if (this.index2 < 0) {
			if (this.index1 >= text.length()) {
				return false;
			} else {
				this.index2 = text.length();
				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public String next() {
		String next = text.substring(index1, index2);
		this.index1 = this.index2 + 1;
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
