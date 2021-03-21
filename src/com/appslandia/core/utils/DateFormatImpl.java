package com.appslandia.core.utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

public class DateFormatImpl extends DateFormat {
	private static final long serialVersionUID = 1L;

	private final DateFormat dateFormat;
	private final Serializable mutex = new Mutex();

	public DateFormatImpl(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public StringBuffer format(Date date, StringBuffer buffer, FieldPosition field) {
		synchronized (mutex) {
			return this.dateFormat.format(date, buffer, field);
		}
	}

	@Override
	public Date parse(String string, ParsePosition position) {
		synchronized (mutex) {
			return this.dateFormat.parse(string, position);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}
}