package com.appslandia.core.utils;

import java.io.Serializable;

public class TextBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String LINE_BREAK = System.getProperty("line.separator", "\r\n");

	private final StringBuilder sb;

	public TextBuilder() {
		this.sb = new StringBuilder();
	}

	public TextBuilder(int capacity) {
		this.sb = new StringBuilder(capacity);
	}

	public TextBuilder appendln() {
		this.sb.append(LINE_BREAK);
		return this;
	}

	public TextBuilder insert(int offset, String str) {
		this.sb.insert(offset, str);
		return this;
	}

	public TextBuilder inserttab(int offset, int tab) {
		for (int i = 0; i < tab; i++) {
			this.sb.insert(offset, '\t');
		}
		return this;
	}

	public TextBuilder append(String str) {
		this.sb.append(str);
		return this;
	}

	public TextBuilder appendlntab(int tab) {
		return this.appendln().appendtab(tab);
	}

	public TextBuilder appendtab(int tab) {
		for (int i = 0; i < tab; i++) {
			this.sb.append('\t');
		}
		return this;
	}

	public TextBuilder append(Object obj) {
		this.sb.append(obj);
		return this;
	}

	public TextBuilder append(char chr) {
		this.sb.append(chr);
		return this;
	}

	public TextBuilder append(int val) {
		this.sb.append(val);
		return this;
	}

	public TextBuilder append(long val) {
		this.sb.append(val);
		return this;
	}

	public int length() {
		return this.sb.length();
	}

	public void clear() {
		this.sb.setLength(0);
	}

	@Override
	public String toString() {
		return this.sb.toString();
	}
}
