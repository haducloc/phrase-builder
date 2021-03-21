package com.appslandia.core.utils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.appslandia.core.adapters.SimpleItem;

import android.content.Context;

public class LocaleContext {

	protected Context context;
	private Locale locale;

	private NumberFormat numberFormat;
	private NumberFormat currencyFormat;
	private NumberFormat percentFormat;

	private Currency currency;

	private DateFormat dateFormatMd;
	private DateFormat timeFormaSh;
	private DateFormat dateTimeFormat;

	private final Object mutex = new Object();

	private static LocaleContext instance;
	private static final Object MUTEX = new Object();

	public static interface LocaleProvider {
		Locale get(Context context);
	}

	private static LocaleProvider localeProvider;

	public static void setLocaleProvider(LocaleProvider localeProvider) {
		LocaleContext.localeProvider = localeProvider;
	}

	public static LocaleContext getInstance(Context context) {
		if (instance == null) {
			synchronized (MUTEX) {
				if (instance == null) {
					instance = new LocaleContext(context);
				}
			}
		}
		return instance;
	}

	protected LocaleContext(Context context) {
		this.context = context.getApplicationContext();
	}

	public Locale getLocale() {
		if (locale == null) {
			synchronized (mutex) {
				if (locale == null) {
					if (localeProvider == null) {
						locale = Locale.getDefault();
					} else {
						locale = localeProvider.get(context);
						localeProvider = null;
					}
				}
			}
		}
		return locale;
	}

	public Calendar getCalendar() {
		return GregorianCalendar.getInstance(getLocale());
	}

	public Calendar getThisWeekMon() {
		return DateUtils.getThisWeekMon(getLocale());
	}

	public List<SimpleItem> getWeekDays() {
		return DateUtils.getWeekDays(getLocale());
	}

	public WeekDate[] getWeekDates(int weeksDis) {
		return DateUtils.getWeekDates(getLocale(), weeksDis);
	}

	public String getDmPattern() {
		return DateUtils.getDmPattern(getLocale());
	}

	public NumberFormat getNumberFormat() {
		if (numberFormat == null) {
			synchronized (mutex) {
				if (numberFormat == null) {
					numberFormat = new NumberFormatImpl(NumberFormat.getNumberInstance(getLocale()));
				}
			}
		}
		return numberFormat;
	}

	public NumberFormat getCurrencyFormat() {
		if (currencyFormat == null) {
			synchronized (mutex) {
				if (currencyFormat == null) {
					currencyFormat = new NumberFormatImpl(NumberFormat.getCurrencyInstance(getLocale()));
				}
			}
		}
		return currencyFormat;
	}

	public NumberFormat getPercentFormat() {
		if (percentFormat == null) {
			synchronized (mutex) {
				if (percentFormat == null) {
					percentFormat = new NumberFormatImpl(NumberFormat.getPercentInstance(getLocale()));
				}
			}
		}
		return percentFormat;
	}

	public Currency getCurrency() {
		if (currency == null) {
			synchronized (mutex) {
				if (currency == null) {
					currency = Currency.getInstance(getLocale());
				}
			}
		}
		return currency;
	}

	public DateFormat getDateFormatMd() {
		if (dateFormatMd == null) {
			synchronized (mutex) {
				if (dateFormatMd == null) {
					dateFormatMd = new DateFormatImpl(DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale()));
				}
			}
		}
		return dateFormatMd;
	}

	public DateFormat getTimeFormatSht() {
		if (timeFormaSh == null) {
			synchronized (mutex) {
				if (timeFormaSh == null) {
					timeFormaSh = new DateFormatImpl(DateFormat.getTimeInstance(DateFormat.SHORT, getLocale()));
				}
			}
		}
		return timeFormaSh;
	}

	public DateFormat getDateTimeFormat() {
		if (dateTimeFormat == null) {
			synchronized (mutex) {
				if (dateTimeFormat == null) {
					dateTimeFormat = new DateFormatImpl(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, getLocale()));
				}
			}
		}
		return dateTimeFormat;
	}

	@Override
	public String toString() {
		return new ToStringBuilder().toString(this);
	}
}
