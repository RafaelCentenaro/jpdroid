package br.com.rafael.jpdroid.converters;

import java.lang.reflect.Field;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;
import br.com.rafael.jpdroid.annotations.Entity;

/**
 * Classe responsável pela conversão objetos para o formato JSON.
 * 
 * @author Rafael Centenaro
 *
 */
public class JpdroidJsonConverter {

	public static JSONArray toJson(Object entity) throws Exception {
	 
	  return getJSONArray(entity);
  }
	
	public static JSONArray toJson(Cursor cursor) throws Exception {
		 
	  return getJSONArray(cursor);
  }

	public static JSONArray getJSONArray(Object entity) throws Exception {
		JSONArray resultSet = new JSONArray();
		JSONObject jsonObject = null;

		Object[] element = null;

		if (entity instanceof List) {
			element = ((List<?>) entity).toArray(new Object[((List<?>) entity).size()]);

		} else {
			element = new Object[1];
			element[0] = entity;
		}

		for (Object item : element) {
			jsonObject = new JSONObject();
			Class<?> classe = item.getClass();
			Field[] declaredFields = classe.getDeclaredFields();
			for (Field field : declaredFields) {
				field.setAccessible(true);
				Object child = field.get(item);
				if (child != null) {
					if (child instanceof List || child.getClass().getAnnotation(Entity.class) != null) {
						jsonObject.accumulate(field.getName(), getJSONArray(child));
					} else {
						jsonObject.put(field.getName(), child.toString());
					}
				}
			}
			resultSet.put(jsonObject);
		}

		return resultSet;
	}

	public static JSONArray getJSONArray(Cursor cursor) {
		JSONArray resultSet = new JSONArray();

		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {

			int totalColumn = cursor.getColumnCount();
			JSONObject rowObject = new JSONObject();

			for (int i = 0; i < totalColumn; i++) {
				if (cursor.getColumnName(i) != null) {

					try {

						if (cursor.getString(i) != null) {
							Log.d("TAG_NAME", cursor.getString(i));
							rowObject.put(cursor.getColumnName(i), cursor.getString(i));
						} else {
							rowObject.put(cursor.getColumnName(i), "");
						}
					} catch (Exception e) {
						Log.d("TAG_NAME", e.getMessage());
					}
				}

			}

			resultSet.put(rowObject);
			cursor.moveToNext();
		}

		cursor.close();
		Log.d("TAG_NAME", resultSet.toString());
		return resultSet;
	}
}
