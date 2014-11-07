package br.com.rafael.jpdroid.converters;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;

public class JpdroidDateUtil {

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("unchecked")
	public static <T> T convert(String string, Class<T> type) {
		try {
			if(string == null || string.trim().length() == 0){
				return null;
			}
			if (type.getName().equals("java.util.Date")) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				return (T) dateFormat.parse(string);

			}
			if (type.getName().equals("java.sql.Date")) {
				return (T) new java.sql.Date(java.sql.Date.parse(string));
			}
			if (type.getSimpleName().equals("Calendar")) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateFormat.parse(string));
				return (T) calendar;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
