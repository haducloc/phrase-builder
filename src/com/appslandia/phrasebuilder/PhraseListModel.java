package com.appslandia.phrasebuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.appslandia.core.utils.DataList;
import com.appslandia.core.utils.StringUtils;
import com.appslandia.phrasebuilder.entities.Phrase;

public class PhraseListModel {

	public static int languageId;
	public static String searchText = StringUtils.EMPTY_STRING;

	public final static Set<Integer> listIds = new HashSet<Integer>();
	public final static List<Phrase> phrases = new ArrayList<Phrase>();

	public static boolean setPhrases(DataList<Phrase> dataList) {
		if (listIds.contains(dataList.getId()) == false) {

			languageId = dataList.getInt(PhraseListActivity.PlaceholderFragment.LOADER_PHRASES_PARAM_LANGUAGE_ID);
			searchText = dataList.getString(PhraseListActivity.PlaceholderFragment.LOADER_PHRASES_PARAM_SEARCH_TEXT);

			listIds.clear();
			phrases.clear();

			listIds.add(dataList.getId());
			phrases.addAll(dataList.getObjects());
			return true;
		}
		return false;
	}

	public static boolean appendPhrases(DataList<Phrase> dataList) {
		if (listIds.contains(dataList.getId()) == false) {
			listIds.add(dataList.getId());
			phrases.addAll(dataList.getObjects());

			return true;
		}
		return false;
	}

	public static void removePhrase(int phraseId) {
		int count = phrases.size();
		for (int i = 0; i < count; i++) {
			if (phrases.get(i)._id == phraseId) {
				phrases.remove(i);
				break;
			}
		}
	}

	public static void addPhrase(Phrase phrase) {
		phrases.add(0, phrase);
	}

	public static void updatePhrase(Phrase phrase) {
		Phrase mPhrase = null;
		List<Phrase> phrases = PhraseListModel.phrases;
		for (Phrase obj : phrases) {
			if (obj._id == phrase._id) {
				mPhrase = obj;
				break;
			}
		}
		if (mPhrase == null) {
			return;
		}
		mPhrase.phrase_text = phrase.phrase_text;
		mPhrase.key_word = phrase.key_word;
		mPhrase.s_keyword = phrase.s_keyword;
		mPhrase.notes = phrase.notes;
		mPhrase.language_id = phrase.language_id;

		mPhrase.last_updated = phrase.last_updated;
		mPhrase.mastered = phrase.mastered;
		// mPhrase.deleted = phrase.deleted;

		// mPhrase.mem_just_updated = phrase.mem_just_updated;
		mPhrase.labels = phrase.labels;
		mPhrase.setLabelList(phrase.getLabelList());
	}

	public static void updateMastered(int phraseId, boolean mastered) {
		List<Phrase> phrases = PhraseListModel.phrases;
		for (Phrase phrase : phrases) {
			if (phrase._id == phraseId) {
				phrase.mastered = mastered;
				break;
			}
		}
	}

	public static long getLastMemJustUpdated() {
		int count = phrases.size();
		if (count > 0) {
			return phrases.get(count - 1).mem_just_updated;
		}
		return 0;
	}

	public static void reset() {
		languageId = 0;
		searchText = StringUtils.EMPTY_STRING;

		listIds.clear();
		phrases.clear();
	}
}
