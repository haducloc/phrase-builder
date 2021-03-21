package com.appslandia.core.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IndexRandom implements Iterator<Integer> {

	private final int n;
	private int[] flags;
	private int remainingCount;

	public IndexRandom(int n) {
		this.n = n;
	}

	private void initialize() {
		if (flags == null) {
			flags = new int[n];
			remainingCount = n;
		}
	}

	@Override
	public Integer next() {
		initialize();
		if (remainingCount == 0) {
			throw new NoSuchElementException("IndexRandom.next()");
		}
		int rdIndex = RandomUtils.nextInt(0, remainingCount - 1);
		int index = -1;
		for (int i = 0; i < n; i++) {
			if (flags[i] == 0) {
				index++;
				if (index == rdIndex) {
					flags[i] = 1;
					remainingCount--;
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public boolean hasNext() {
		initialize();
		return remainingCount > 0;
	}

	public void reset() {
		if (flags == null) {
			flags = new int[n];
		} else {
			Arrays.fill(flags, 0);
		}
		remainingCount = n;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
