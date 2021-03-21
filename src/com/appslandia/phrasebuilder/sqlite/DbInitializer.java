package com.appslandia.phrasebuilder.sqlite;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.appslandia.core.utils.SplitterUtils;
import com.appslandia.core.utils.StringUtils;
import com.appslandia.phrasebuilder.entities.Label;
import com.appslandia.phrasebuilder.entities.Language;
import com.appslandia.phrasebuilder.entities.Phrase;

import android.database.sqlite.SQLiteDatabase;

public class DbInitializer {

	public static void initialize(SQLiteDatabase db) {

		// Languages
		final int ENGLISH = insertLanguage(db, "English", -9000);
		final int JAPANESE = insertLanguage(db, "Japanese", 0);

		// Labels
		final Map<String, Integer> labelMap = new HashMap<String, Integer>();

		insertLabel(db, "adjectives", labelMap);
		insertLabel(db, "expressions", labelMap);
		insertLabel(db, "verbs", labelMap);
		insertLabel(db, "idioms", labelMap);

		insertLabel(db, "at-work", labelMap);
		insertLabel(db, "meeting", labelMap);
		insertLabel(db, "restaurant", labelMap);
		insertLabel(db, "shopping", labelMap);
		insertLabel(db, "traveling", labelMap);
		insertLabel(db, "banking", labelMap);

		// Phrases
		insertPhrase(db, "We can invite 100 people at the most to our wedding|at the most||-", ENGLISH, false, labelMap);
		insertPhrase(db, "I can spend $100 at the most|at the most|Not more than|shopping", ENGLISH, false, labelMap);
		insertPhrase(db, "Can I have a table for three, please?|have a table||restaurant", ENGLISH, true, labelMap);
		insertPhrase(db, "I'll take a coke|take a coke||restaurant", ENGLISH, true, labelMap);
		insertPhrase(db, "I'll have the same as you|the same as||-", ENGLISH, true, labelMap);

		insertPhrase(db, "Lunch is on me today|on me||restaurant", ENGLISH, false, labelMap);
		insertPhrase(db, "Please move out of the way. I need space!|move out||-", ENGLISH, false, labelMap);
		insertPhrase(db, "I can't get it to work|can't get||-", ENGLISH, true, labelMap);
		insertPhrase(db, "I can't get it open|can't get||-", ENGLISH, true, labelMap);

		insertPhrase(db, "Sorry I'm late. I was stuck in traffic for an hour|was stuck|In a traffic jam|-", ENGLISH, false, labelMap);
		insertPhrase(db, "We are out of luck. The store is already closed|out of luck|Unlucky|-", ENGLISH, false, labelMap);
		insertPhrase(db, "I am not following you. Can you start again?|not following||meeting", ENGLISH, false, labelMap);

		insertPhrase(db, "I am tied up now. Can we discuss it tomorrow?|tied up||at-work", ENGLISH, false, labelMap);
		insertPhrase(db, "Can you put it away? I might need it|put it away|Don't throw it away|-", ENGLISH, false, labelMap);
		insertPhrase(db, "This is the best I can tell|the best|Can't tell more|-", ENGLISH, false, labelMap);
		insertPhrase(db, "I didn't see it coming|see it coming||-", ENGLISH, false, labelMap);

		insertPhrase(db, "when is a good time for you?|a good time|When is a convenient time for you|-", ENGLISH, false, labelMap);
		insertPhrase(db, "Can I make a suggestion?|make a suggestion|Ask this if you want to make a suggestion|at-work", ENGLISH, false, labelMap);
		insertPhrase(db, "We are running short of time|short of time||at-work", ENGLISH, true, labelMap, System.currentTimeMillis());
		insertPhrase(db, "Why don't you bring it up in the meeting?|bring it up||at-work", ENGLISH, true, labelMap);
		insertPhrase(db, "Can I get our agreement in writing?|in writing|Get a signed agreement|-", ENGLISH, true, labelMap);

		insertPhrase(db, "How much longer do you need?|How much longer||-", ENGLISH, true, labelMap);
		insertPhrase(db, "Can someone fill me in what was discussed yesterday?|fill me in||meeting+at-work", ENGLISH, false, labelMap);
		insertPhrase(db, "You are right to some extent|to some extent||-", ENGLISH, true, labelMap);

		insertPhrase(db, "Ranch|ranch|A large farm|-", ENGLISH, true, labelMap);
		insertPhrase(db, "Entrepreneur|entrepreneur|A person who organizes and operates a business|-", ENGLISH, true, labelMap);
		insertPhrase(db, "Wrap up|wrap up|Finish a meeting|-", ENGLISH, true, labelMap);

		insertPhrase(db, "You should marry him. He is a good catch.|a good catch|a suitable husband/mate for you.|-", ENGLISH, true, labelMap);
		insertPhrase(db, "We are ahead of schedule|ahead of schedule||meeting+at-work", ENGLISH, true, labelMap);
		insertPhrase(db, "We are on schedule|on schedule||meeting+at-work", ENGLISH, true, labelMap);

		insertPhrase(db, "Sorry I'm late. I got held up in the meeting|got held up||-", ENGLISH, false, labelMap);
		insertPhrase(db, "Let's get a move on|move on|Go faster or be late|-", ENGLISH, false, labelMap);
		insertPhrase(db, "We are behind schedule|behind schedule||meeting+at-work", ENGLISH, true, labelMap);

		// Others
		insertPhrase(db, "あなたは日本語が話せるのですか？|話せる|Can you speak Japanese?|-", JAPANESE, false, labelMap);
	}

	static int insertLanguage(SQLiteDatabase db, String name, int lang_pos) {
		Language language = new Language();
		language.name = name;
		language.lang_pos = lang_pos;

		LanguageDao.insert(db, language);
		return language._id;
	}

	static void insertLabel(SQLiteDatabase db, String name, Map<String, Integer> labelMap) {
		Label label = new Label();
		label.name = name;
		label.s_name = StringUtils.toSearchable(label.name);

		LabelDao.insert(db, label);
		labelMap.put(name, label._id);
	}

	static void insertPhrase(SQLiteDatabase db, String input, int languageId, boolean mastered, Map<String, Integer> labelMap) {
		insertPhrase(db, input, languageId, mastered, labelMap, 0);
	}

	// input: phraseText|keyword|notes
	static void insertPhrase(SQLiteDatabase db, String input, int languageId, boolean mastered, Map<String, Integer> labelMap, long deleted) {
		String[] items = SplitterUtils.split(input, '|');
		Phrase phrase = new Phrase();

		phrase.phrase_text = StringUtils.toText(items[0], true);
		phrase.key_word = items[1].trim();

		phrase.notes = StringUtils.toText(items[2], true);
		if (phrase.notes == null) {
			phrase.notes = StringUtils.EMPTY_STRING;
		}

		phrase.language_id = languageId;
		phrase.mastered = mastered;
		phrase.last_updated = System.currentTimeMillis();
		phrase.deleted = deleted;

		phrase.s_keyword = StringUtils.toSearchable(phrase.key_word);

		// Insert Phrase
		PhraseDao.insert(db, phrase);

		// Insert Labels
		String labels = items[3].trim().toLowerCase(Locale.ENGLISH);
		if ("-".equals(labels) == false) {
			String[] labs = SplitterUtils.split(labels, '+');
			for (String label : labs) {
				Integer labelId = labelMap.get(label);
				if (labelId != null) {
					PhraseLabelDao.insert(db, phrase._id, labelId);
				}
			}
		}
	}
}
