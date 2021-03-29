package ru.overscan.lib.data;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.overscan.lib.R;
import ru.overscan.lib.data.plurals.PluralResources;
import ru.overscan.lib.sys.ErrorCollector;

public class DataUtils {
//    private static boolean pluralResourcesInitialization = false;
//    private statitc PluralResources pluralResources = null;
	
//	static SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",
//			Locale.getDefault());
//	static {
//		utcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//	}

	public static String addByComma(String s1, String s2){
		if (emptyString(s2)) return s1;
        if (emptyString(s1)) return s2;
        return s1 + ", " + s2;
	}

	public static boolean isNull(Object o){
		return o == null;
	}
	
	
	public static boolean emptyString(String s) {
		return (s == null) || (s.equals(""));
	}
	
	public static boolean stringsDiffer(String str1, String str2) {
		boolean e1 = emptyString(str1);
		boolean e2 = emptyString(str2);
		if (e1 && e2) return false;
		else if (e1 != e2) return true;
		else return !str1.equals(str2);
	}
	
	// month from 1
	public static Date createDate(int day, int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();		
	}

	public static String dateTime2shortDateTimeStr(long seconds) {
		return millis2shortDateTimeStr(seconds * 1000);
	}

    public static String dateTime2shortDateStr(long seconds) {
        return date2shortDateStr(new Date(seconds * 1000));
    }

	public static String millis2shortDateTimeStr(long millis) {
		return date2shortDateTimeStr(new Date(millis));
//		if (Locale.getDefault().getLanguage().equals("ru"))
//		  return new SimpleDateFormat("dd.MM.yy kk:mm", 
//				  Locale.getDefault()).format(new Date(millis));
//		else return DateFormat.getDateTimeInstance().format(new Date(millis)); 
	}

	public static String date2shortDateTimeStr(Date date) {
		if (Locale.getDefault().getLanguage().equals("ru"))
		  return new SimpleDateFormat("dd.MM.yy kk:mm", 
				  Locale.getDefault()).format(date);
		else return DateFormat.getDateTimeInstance().format(date); 
	}

	public static String date2dateTimeStr(Date date) {
		if (Locale.getDefault().getLanguage().equals("ru"))
		  return new SimpleDateFormat("dd.MM.yyyy kk:mm", 
				  Locale.getDefault()).format(date);
		else return DateFormat.getDateTimeInstance().format(date); 
	}

	public static String date2shortDateStr(Date date) {
		if (Locale.getDefault().getLanguage().equals("ru"))
			return new SimpleDateFormat("dd.MM.yy",
					Locale.getDefault()).format(date);
		else return DateFormat.getDateTimeInstance().format(date);
	}

	public static long getCurrentDateTime() {
	   return System.currentTimeMillis() / 1000;
	}
	
	public static Date parseDateTime(String s) {
		if (s == null || s.equals("")) return null;
	
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",
				Locale.getDefault());
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));	
		try {			
	//		Date parse = simpleDateFormat.parse( "2011-04-15T20:08:18Z" );
			return simpleDateFormat.parse(s);
		} catch (ParseException e) {
			ErrorCollector.add("Ошибка при разборе даты UTC " + s, e);
			return null;
		}
	}

	public static String getQuantityString(Context ctx, int resourceId, int quantity){
		String result = null;

//        if pluralResourcesInitialization

//		if (android.os.Build.VERSION.SDK_INT < 11) {
			try {
				PluralResources plural = new PluralResources(ctx.getResources());
                result = plural.getQuantityString(resourceId, quantity, quantity);
			} catch (Throwable t) {}
//		}

		if (result == null)
			result = ctx.getResources().getQuantityString(resourceId, quantity, quantity);
        return result;
	}

	public static Object findFragmentListener(Fragment fragment, Class klass) {
		Fragment parent = fragment.getParentFragment();
		if (parent == null) {
            Context context = fragment.getActivity();
			if (context == null) return null;
			if (klass.isInstance(context)) return context;
		} else {
			if (klass.isInstance(parent)) return parent;
		}
		return null;
	}

}
