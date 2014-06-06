package br.com.rafael.jpdroid.util;

import org.json.JSONArray;

import android.database.Cursor;
import android.database.MatrixCursor;
import br.com.rafael.jpdroid.converters.JpdroidCsvConverter;
import br.com.rafael.jpdroid.converters.JpdroidJsonConverter;
import br.com.rafael.jpdroid.converters.JpdroidXmlConverter;
import br.com.rafael.jpdroid.converters.JpdroidMatrixCursorConverter;

/**
 * Classe responsável pela conversão de objetos.
 * 
 * @author Rafael Centenaro
 *
 */
public class JpdroidConverter {

	public static JSONArray toJson(Cursor cursor) throws Exception {
	 
		return JpdroidJsonConverter.toJson(cursor);
  }
	
	public static JSONArray toJson(Object object) throws Exception {
		 
		return JpdroidJsonConverter.toJson(object);
  }
	
	public static String toXml(Cursor cursor) throws Exception {
	  
	  return JpdroidXmlConverter.toXml(cursor);
  }
	
	public static String toXml(Object object) throws Exception {
	  
	  return JpdroidXmlConverter.toXml(object);
  }
	
public static String toCsv(Cursor cursor) throws Exception {
	  
	  return JpdroidCsvConverter.toCsv(cursor);
  }
	
	public static String toCsv(Object object) throws Exception {
	  
	  return JpdroidCsvConverter.toCsv(object);
  }
	
	public static MatrixCursor toMatrixCursor(Object entity){
		return JpdroidMatrixCursorConverter.toMatrixCursor(entity);
	}
	
	public static MatrixCursor toMatrixCursor(Object entity, boolean onlyColumn){
		return JpdroidMatrixCursorConverter.toMatrixCursor(entity,onlyColumn);
	}


}
