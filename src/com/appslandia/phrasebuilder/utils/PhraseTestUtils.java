package com.appslandia.phrasebuilder.utils;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.SimpleItem;
import com.appslandia.core.utils.IndexRandom;
import com.appslandia.core.utils.RandomUtils;
import com.appslandia.phrasebuilder.PhraseFillin;
import com.appslandia.phrasebuilder.PhraseMtc;
import com.appslandia.phrasebuilder.R;
import com.appslandia.phrasebuilder.entities.Phrase;

import android.content.res.Resources;

public class PhraseTestUtils {

	public static final int MASTERY_ALL = 1;
	public static final int MASTERY_LEARNING = 2;
	public static final int MASTERY_MASTERED = 3;

	public static List<SimpleItem> getMasteryList(Resources res) {
		List<SimpleItem> list = new ArrayList<SimpleItem>(3);

		list.add(new SimpleItem(MASTERY_ALL, res.getString(R.string.mastery_all)));
		list.add(new SimpleItem(MASTERY_LEARNING, res.getString(R.string.mastery_learning)));
		list.add(new SimpleItem(MASTERY_MASTERED, res.getString(R.string.mastery_mastered)));

		return list;
	}

	public static final int TEST_TYPE_MIXING_TYPE = 1;
	public static final int TEST_TYPE_MULTIPLE_CHOICE = 2;
	public static final int TEST_TYPE_FILL_IN = 3;

	public static List<SimpleItem> getTestTypeList(Resources res) {
		List<SimpleItem> list = new ArrayList<SimpleItem>(3);

		list.add(new SimpleItem(TEST_TYPE_MIXING_TYPE, res.getString(R.string.test_type_mixing_type)));
		list.add(new SimpleItem(TEST_TYPE_MULTIPLE_CHOICE, res.getString(R.string.test_type_multiple_choice)));
		list.add(new SimpleItem(TEST_TYPE_FILL_IN, res.getString(R.string.test_type_fill_in)));

		return list;
	}

	public static List<SimpleItem> getDateCreatedList(Resources res) {
		List<SimpleItem> list = new ArrayList<SimpleItem>();

		list.add(new SimpleItem(0, res.getString(R.string.date_created_all)));
		list.add(new SimpleItem(1, res.getString(R.string.date_created_a_day_ago)));

		// days: 2-6
		for (int d = 2; d < 7; d++) {
			list.add(new SimpleItem(d, d + " " + res.getString(R.string.date_created_days_ago)));
		}

		// 1 week ago
		list.add(new SimpleItem(7, res.getString(R.string.date_created_a_week_ago)));

		// 2-10 weeks ago
		for (int w = 2; w < 11; w++) {
			int d = w * 7;
			list.add(new SimpleItem(d, w + " " + res.getString(R.string.date_created_weeks_ago)));
		}
		return list;
	}

	public static List<Object> generateUnspecifiedTest(List<Phrase> phrases, List<String> keywords, Resources res) {
		OptIndexes optIndexes = new OptIndexes();
		int count = phrases.size();

		IndexRandom indexRd = new IndexRandom(count);
		List<Object> list = new ArrayList<Object>(count);

		while (indexRd.hasNext()) {
			int index = indexRd.next();
			Phrase phrase = phrases.get(index);

			if (RandomUtils.nextInt(0, 1) == 0) {
				list.add(buildPhraseFillIn(phrase));
			} else {
				list.add(buildPhraseMtc(phrase, keywords, optIndexes, res));
			}
		}
		return list;
	}

	public static List<Object> generateMtcTest(List<Phrase> phrases, List<String> keywords, Resources res) {
		OptIndexes optIndexes = new OptIndexes();
		int count = phrases.size();

		IndexRandom indexRd = new IndexRandom(count);
		List<Object> list = new ArrayList<Object>(count);

		while (indexRd.hasNext()) {
			int index = indexRd.next();
			Phrase phrase = phrases.get(index);
			list.add(buildPhraseMtc(phrase, keywords, optIndexes, res));
		}
		return list;
	}

	private static PhraseMtc buildPhraseMtc(Phrase phrase, List<String> keywords, OptIndexes optIndexes, Resources res) {
		PhraseMtc model = new PhraseMtc();
		model._id = phrase._id;
		model.phrase_text = phrase.phrase_text;
		model.key_word = phrase.key_word;
		model.labels = phrase.labels;
		model.notes = phrase.notes;
		model.mastered = phrase.mastered;

		randomOptIndexes(keywords.size(), optIndexes);

		String keyword0 = keywords.get(optIndexes.index0);
		String keyword1 = optIndexes.index1 >= 0 ? keywords.get(optIndexes.index1) : (null);
		String keyword2 = optIndexes.index2 >= 0 ? keywords.get(optIndexes.index2) : (null);
		String keyword3 = optIndexes.index3 >= 0 ? keywords.get(optIndexes.index3) : (null);

		// correctIndex
		if (phrase.key_word.equals(keyword0)) {
			model.correctIndex = 0;

		} else if (phrase.key_word.equals(keyword1)) {
			model.correctIndex = 1;

		} else if (phrase.key_word.equals(keyword2)) {
			model.correctIndex = 2;

		} else if (phrase.key_word.equals(keyword3)) {
			model.correctIndex = 3;
		} else {
			if (keyword1 == null) {
				model.correctIndex = RandomUtils.nextInt(0, 0);

			} else if (keyword2 == null) {
				model.correctIndex = RandomUtils.nextInt(0, 1);

			} else if (keyword3 == null) {
				model.correctIndex = RandomUtils.nextInt(0, 2);
			} else {
				model.correctIndex = RandomUtils.nextInt(0, 3);
			}
		}

		if (keyword1 == null) {
			keyword1 = res.getString(R.string.keywords_unavailable);
		} else {
			model.optionEnabledBitMask += 2;
		}

		if (keyword2 == null) {
			keyword2 = res.getString(R.string.keywords_unavailable);
		} else {
			model.optionEnabledBitMask += 4;
		}

		if (keyword3 == null) {
			keyword3 = res.getString(R.string.keywords_unavailable);
		} else {
			model.optionEnabledBitMask += 8;
		}

		model.keywordOptions[0] = model.correctIndex == 0 ? phrase.key_word : keyword0;
		model.keywordOptions[1] = model.correctIndex == 1 ? phrase.key_word : keyword1;
		model.keywordOptions[2] = model.correctIndex == 2 ? phrase.key_word : keyword2;
		model.keywordOptions[3] = model.correctIndex == 3 ? phrase.key_word : keyword3;

		return model;
	}

	public static List<Object> generateFillInTest(List<Phrase> phrases) {
		int count = phrases.size();
		IndexRandom indexRd = new IndexRandom(count);
		List<Object> list = new ArrayList<Object>(count);

		while (indexRd.hasNext()) {
			int index = indexRd.next();
			Phrase phrase = phrases.get(index);
			list.add(buildPhraseFillIn(phrase));
		}
		return list;
	}

	private static PhraseFillin buildPhraseFillIn(Phrase phrase) {
		PhraseFillin model = new PhraseFillin();
		model._id = phrase._id;
		model.phrase_text = phrase.phrase_text;
		model.key_word = phrase.key_word;
		model.labels = phrase.labels;
		model.notes = phrase.notes;
		model.mastered = phrase.mastered;

		return model;
	}

	private static void randomOptIndexes(int count, OptIndexes optIndexes) {
		// index0
		optIndexes.index0 = RandomUtils.nextInt(0, count - 1);

		if (count >= 2) {
			// index1
			int index1 = RandomUtils.nextInt(0, count - 2);

			int j = -1;
			for (int i = 0; i < count; i++) {
				if (optIndexes.index0 != i) {
					j++;
					if (j == index1) {
						optIndexes.index1 = i;
						break;
					}
				}
			}

			if (count >= 3) {
				// index2
				int index2 = RandomUtils.nextInt(0, count - 3);
				j = -1;
				for (int i = 0; i < count; i++) {
					if (optIndexes.index0 != i && optIndexes.index1 != i) {
						j++;
						if (j == index2) {
							optIndexes.index2 = i;
							break;
						}
					}
				}

				if (count >= 4) {
					// index3
					int index3 = RandomUtils.nextInt(0, count - 4);
					j = -1;
					for (int i = 0; i < count; i++) {
						if (optIndexes.index0 != i && optIndexes.index1 != i && optIndexes.index2 != i) {
							j++;
							if (j == index3) {
								optIndexes.index3 = i;
								break;
							}
						}
					}
				}
			}
		}
	}

	private static class OptIndexes {
		public int index0 = -1;
		public int index1 = -1;
		public int index2 = -1;
		public int index3 = -1;
	}
}
