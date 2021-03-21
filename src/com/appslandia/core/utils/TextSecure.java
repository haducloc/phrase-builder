package com.appslandia.core.utils;

import java.util.ArrayList;
import java.util.List;

public class TextSecure {

	private final List<Transform> impls = new ArrayList<Transform>(2);

	public TextSecure() {
	}

	public TextSecure add(Transform impl) {
		this.impls.add(impl);
		return this;
	}

	public String secure(String text, Object params) {
		StringBuilder sb = new StringBuilder(text.length()).append(text);
		int len = this.impls.size();
		for (int i = 0; i < len; i++) {
			this.impls.get(i).execute(sb, params);
		}
		return sb.toString();
	}

	public String unsecure(String text, Object params) {
		StringBuilder sb = new StringBuilder(text.length()).append(text);
		int len = this.impls.size();
		for (int i = len - 1; i >= 0; i--) {
			this.impls.get(i).execute(sb, params);
		}
		return sb.toString();
	}

	public static interface Transform {
		void execute(StringBuilder text, Object params);
	}

	public static class ForwardRevert implements Transform {
		private final float n;

		public ForwardRevert() {
			this(1.0f);
		}

		public ForwardRevert(float n) {
			this.n = n;
		}

		@Override
		public void execute(StringBuilder text, Object params) {
			int count = (int) (this.n * (Integer) params);
			int len = text.length() / count;
			int idx = 0;
			for (int i = 1; i <= count; i++) {
				StringBuilderUtils.revert(text, idx, idx + len - 1);
				idx += len;
			}
		}
	}

	public static class BackwardRevert implements Transform {
		private final float n;

		public BackwardRevert() {
			this(1.0f);
		}

		public BackwardRevert(float n) {
			this.n = n;
		}

		@Override
		public void execute(StringBuilder text, Object params) {
			int count = (int) (this.n * (Integer) params);
			int len = text.length() / count;
			int idx = text.length() - 1;
			for (int i = count; i >= 1; i--) {
				StringBuilderUtils.revert(text, idx - len + 1, idx);
				idx -= len;
			}
		}
	}
}
