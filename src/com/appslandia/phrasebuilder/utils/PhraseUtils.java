package com.appslandia.phrasebuilder.utils;

import com.appslandia.core.utils.ArrayUtils;
import com.appslandia.core.utils.StringUtils;
import com.appslandia.phrasebuilder.R;
import com.appslandia.phrasebuilder.entities.Language;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

@SuppressLint("DefaultLocale")
public class PhraseUtils {

	public static final String KEYWORD_UNLABELED = "unlabeled";
	public static final String KEYWORD_MASTERED = "mastered";
	public static final String KEYWORD_LEARNING = "learning";

	public static final String LIST_ITEM_UNLABELED = "> unlabeled";
	public static final String LIST_ITEM_MASTERED = "> mastered";
	public static final String LIST_ITEM_LEARNING = "> learning";

	public static Language getNoLanguage(Resources res) {
		return new Language(0, res.getString(R.string.no_language));
	}

	public static String truncatePhrase(String phraseText) {
		return StringUtils.truncate(phraseText, 25);
	}

	public static Spannable createPhraseTextSpan(String phraseText, String keyword) {
		SpannableString span = new SpannableString(phraseText);
		int idx = phraseText.toLowerCase().indexOf(keyword.toLowerCase());
		span.setSpan(new RelativeSizeSpan(1.25f), idx, idx + keyword.length(), 0);
		span.setSpan(new ForegroundColorSpan(Color.BLACK), idx, idx + keyword.length(), 0);
		return span;
	}

	public static PhraseSegs parsePhraseSegs(String phraseText, String keyword) {
		int idx = phraseText.toLowerCase().indexOf(keyword.toLowerCase());
		PhraseSegs segs = new PhraseSegs();
		segs.text1 = phraseText.substring(0, idx);
		segs.text3 = phraseText.substring(idx + keyword.length());
		return segs;
	}

	public static final String KEYWORD_BLANK1 = new String(ArrayUtils.fill(new char[32], '_'));
	public static final String KEYWORD_BLANK2 = new String(ArrayUtils.fill(new char[64], '_'));

	public static Spannable createPhraseTextSpan(PhraseSegs phraseSegs) {
		int len1 = phraseSegs.text1.length();
		String keyworkBlank = null;
		if (len1 > 0 || phraseSegs.text3.length() > 0) {
			keyworkBlank = KEYWORD_BLANK1;
		} else {
			keyworkBlank = KEYWORD_BLANK2;
		}

		StringBuilder sb = new StringBuilder(len1 + phraseSegs.text3.length() + keyworkBlank.length());
		sb.append(phraseSegs.text1);
		sb.append(keyworkBlank);
		sb.append(phraseSegs.text3);

		SpannableString span = new SpannableString(sb.toString());
		int end1 = len1 + keyworkBlank.length();

		span.setSpan(new RelativeSizeSpan(0.25f), len1, end1, 0);
		span.setSpan(new StyleSpan(Typeface.NORMAL), len1, end1, 0);
		span.setSpan(new ForegroundColorSpan(Color.DKGRAY), len1, end1, 0);

		return span;
	}

	public static Spannable createPhraseTextSpan(PhraseSegs phraseSegs, String answer, boolean answerMatched) {
		int len1 = phraseSegs.text1.length();
		StringBuilder sb = new StringBuilder(len1 + phraseSegs.text3.length() + answer.length());

		sb.append(phraseSegs.text1);
		sb.append(answer);
		sb.append(phraseSegs.text3);

		SpannableString span = new SpannableString(sb.toString());
		if (answerMatched) {
			span.setSpan(new RelativeSizeSpan(1.20f), len1, len1 + answer.length(), 0);
		} else {
			span.setSpan(new StrikethroughSpan(), len1, len1 + answer.length(), 0);
		}
		return span;
	}

}
