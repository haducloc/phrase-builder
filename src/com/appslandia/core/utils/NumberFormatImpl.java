package com.appslandia.core.utils;

import java.io.Serializable;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class NumberFormatImpl extends NumberFormat {
	private static final long serialVersionUID = 1L;

	private final NumberFormat numberFormat;
	private final Serializable mutex = new Mutex();

	public NumberFormatImpl(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	@Override
	public Number parse(String string, ParsePosition position) {
		synchronized (this.mutex) {
			return this.numberFormat.parse(string, position);
		}
	}

	@Override
	public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
		synchronized (this.mutex) {
			return this.numberFormat.format(value, buffer, field);
		}
	}

	@Override
	public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
		synchronized (this.mutex) {
			return this.numberFormat.format(value, buffer, field);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}
}