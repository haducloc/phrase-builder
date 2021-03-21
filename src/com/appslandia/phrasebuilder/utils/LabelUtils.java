package com.appslandia.phrasebuilder.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.adapters.LabelItem;

public class LabelUtils {

	public static final List<FilterableItem> EMPTY_LABELS = Collections.emptyList();
	public static final Comparator<FilterableItem> LABEL_COMPARATOR = new Comparator<FilterableItem>() {

		@Override
		public int compare(FilterableItem item1, FilterableItem item2) {
			return item1.getFilterName().compareTo(item2.getFilterName());
		}
	};

	private static List<FilterableItem> createLabelList() {
		return new ArrayList<FilterableItem>(4);
	}

	// labels: |%_id:%name:%s_name|%_id:%name:%s_name|
	public static List<FilterableItem> toLabelList(String labels) {
		if (labels == null) {
			return EMPTY_LABELS;
		}
		List<FilterableItem> labelList = createLabelList();

		int startPos = 0;
		int next = 0;
		while ((next = labels.indexOf('|', startPos)) != -1) {
			String label = labels.substring(startPos, next);
			if (label.isEmpty() == false) {
				labelList.add(parseLabel(label));
			}
			startPos = next + 1;
		}
		if (startPos == 0) {
			if (labels.isEmpty() == false) {
				labelList.add(parseLabel(labels));
			}
		}

		Collections.sort(labelList, LABEL_COMPARATOR);
		return labelList;
	}

	public static void sortLabelList(List<FilterableItem> labelList) {
		Collections.sort(labelList, LABEL_COMPARATOR);
	}

	static FilterableItem parseLabel(String label) {
		int idx1 = label.indexOf(':');
		int idx2 = label.lastIndexOf(':');
		return new LabelItem(Integer.parseInt(label.substring(0, idx1)), label.substring(idx1 + 1, idx2), label.substring(idx2 + 1));
	}

	public static String toLabels(List<FilterableItem> labelList) {
		if (labelList.isEmpty()) {
			return (null);
		}
		StringBuilder sb = new StringBuilder();
		for (FilterableItem item : labelList) {
			sb.append('|').append(item.getId()).append(':').append(item.getName()).append(':').append(item.getFilterName());
		}
		sb.append('|');
		return sb.toString();
	}

	public static List<FilterableItem> copyLabelList(List<FilterableItem> labelList) {
		List<FilterableItem> list = createLabelList();
		for (FilterableItem item : labelList) {
			list.add(new LabelItem(item.getId(), item.getName(), item.getFilterName()));
		}
		return list;
	}

	public static List<FilterableItem> getUnlabeledList() {
		List<FilterableItem> list = new ArrayList<FilterableItem>(1);
		list.add(new LabelItem(0, PhraseUtils.KEYWORD_UNLABELED, PhraseUtils.KEYWORD_UNLABELED));
		return list;
	}
}
