package com.appslandia.core.utils;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.appslandia.core.adapters.SimpleItem;

public class DateUtils {

	public static List<SimpleItem> getWeekDays(Locale locale) {
		String[] weekdays = DateFormatSymbols.getInstance(locale).getWeekdays();
		List<SimpleItem> list = new ArrayList<SimpleItem>(7);

		list.add(new SimpleItem(Calendar.SUNDAY, weekdays[Calendar.SUNDAY]));
		list.add(new SimpleItem(Calendar.MONDAY, weekdays[Calendar.MONDAY]));
		list.add(new SimpleItem(Calendar.TUESDAY, weekdays[Calendar.TUESDAY]));
		list.add(new SimpleItem(Calendar.WEDNESDAY, weekdays[Calendar.WEDNESDAY]));
		list.add(new SimpleItem(Calendar.THURSDAY, weekdays[Calendar.THURSDAY]));
		list.add(new SimpleItem(Calendar.FRIDAY, weekdays[Calendar.FRIDAY]));
		list.add(new SimpleItem(Calendar.SATURDAY, weekdays[Calendar.SATURDAY]));

		return list;
	}

	public static Calendar getThisWeekMon(Locale locale) {
		Calendar cal = GregorianCalendar.getInstance(locale);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		clearTime(cal);
		return cal;
	}

	public static WeekDate[] getWeekBound(Locale locale, int weeksDis) {
		String[] weekDays = DateFormatSymbols.getInstance(locale).getShortWeekdays();

		Calendar cal = GregorianCalendar.getInstance(locale);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.add(Calendar.DATE, 7 * weeksDis);

		// Start Date
		int day = cal.get(Calendar.DAY_OF_WEEK);
		WeekDate sd = new WeekDate(cal.get(Calendar.DATE), day, weekDays[day]);

		// End Date
		cal.add(Calendar.DATE, 6);
		day = cal.get(Calendar.DAY_OF_WEEK);
		WeekDate ed = new WeekDate(cal.get(Calendar.DATE), day, weekDays[day]);

		return new WeekDate[] { sd, ed };
	}

	public static WeekDate[] getWeekDates(Locale locale, int weeksDis) {
		String[] weekDays = DateFormatSymbols.getInstance(locale).getShortWeekdays();

		Calendar cal = GregorianCalendar.getInstance(locale);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.add(Calendar.DATE, 7 * weeksDis);

		List<WeekDate> list = new ArrayList<>(7);
		for (int i = 0; i < 7; i++) {
			if (i > 0) {
				cal.add(Calendar.DATE, 1);
			}
			int day = cal.get(Calendar.DAY_OF_WEEK);
			list.add(new WeekDate(cal.get(Calendar.DATE), day, weekDays[day]));
		}
		return list.toArray(new WeekDate[7]);
	}

	public static String getDmPattern(Locale locale) {
		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
		String pattern = sdf.toPattern();

		int idx1 = pattern.indexOf('y');
		int idx2 = pattern.lastIndexOf('y');

		if (idx1 > 0) {
			idx1 -= 1;
		} else {
			idx2 += 1;
		}
		return pattern.substring(0, idx1).trim() + pattern.substring(idx2 + 1).trim();
	}

	public static Date clearTime(Date d) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);

		clearTime(cal);
		return cal.getTime();
	}

	public static void clearTime(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	public static Calendar getCalendar(int onDay, int atHour, int atMinute) {
		GregorianCalendar cal = new GregorianCalendar();

		cal.set(Calendar.DAY_OF_WEEK, onDay);
		cal.set(Calendar.HOUR_OF_DAY, atHour);
		cal.set(Calendar.MINUTE, atMinute);

		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal;
	}

	public static CharSequence getRelativeDateString(long date) {
		return android.text.format.DateUtils.getRelativeTimeSpanString(date, System.currentTimeMillis(), android.text.format.DateUtils.SECOND_IN_MILLIS);
	}

	public static String getNotificationTimestamp() {
		return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(new Date());
	}
}
