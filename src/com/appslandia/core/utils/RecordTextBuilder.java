package com.appslandia.core.utils;

public class RecordTextBuilder {

	private int sequence = 1;
	private int recordCount = 0;

	private final StringBuilder builder;
	private final char recordSep;
	private final char fieldSep;

	public RecordTextBuilder(char recordSep, char fieldSep) {
		this(recordSep, fieldSep, 16);
	}

	public RecordTextBuilder(char recordSep, char fieldSep, int capacity) {
		this.recordSep = recordSep;
		this.fieldSep = fieldSep;
		this.builder = new StringBuilder(capacity);
	}

	// appends

	public RecordTextBuilder append(String value) {
		this.builder.append(value).append(fieldSep);
		return this;
	}

	public RecordTextBuilder append(char value) {
		this.builder.append(value).append(fieldSep);
		return this;
	}

	public RecordTextBuilder append(boolean value) {
		this.builder.append(value).append(fieldSep);
		return this;
	}

	public RecordTextBuilder append(int value) {
		this.builder.append(value).append(fieldSep);
		return this;
	}

	public RecordTextBuilder append(long value) {
		this.builder.append(value).append(fieldSep);
		return this;
	}

	public RecordTextBuilder append(float value) {
		this.builder.append(value).append(fieldSep);
		return this;
	}

	public RecordTextBuilder append(double value) {
		this.builder.append(value).append(fieldSep);
		return this;
	}

	// appendLasts
	public RecordTextBuilder appendLast(String value) {
		this.builder.append(value);
		return this;
	}

	public RecordTextBuilder appendLast(char value) {
		this.builder.append(value);
		return this;
	}

	public RecordTextBuilder appendLast(boolean value) {
		this.builder.append(value);
		return this;
	}

	public RecordTextBuilder appendLast(int value) {
		this.builder.append(value);
		return this;
	}

	public RecordTextBuilder appendLast(long value) {
		this.builder.append(value);
		return this;
	}

	public RecordTextBuilder appendLast(float value) {
		this.builder.append(value);
		return this;
	}

	public RecordTextBuilder appendLast(double value) {
		this.builder.append(value);
		return this;
	}

	public int getSequence() {
		return this.sequence;
	}

	public int getRecordCount() {
		return this.recordCount;
	}

	public int length() {
		return this.builder.length();
	}

	public boolean isEmpty() {
		return this.builder.length() == 0;
	}

	public void beginRecord() {
		if (this.recordCount > 0) {
			this.builder.append(recordSep);
		}
	}

	public void finishRecord() {
		this.recordCount += 1;
	}

	public void reset() {
		this.sequence += 1;
		this.builder.setLength(0);
		this.recordCount = 0;
	}

	public byte[] toBytes() {
		return CharsetUtils.toBytes(this.builder.toString());
	}

	@Override
	public String toString() {
		return this.builder.toString();
	}
}
