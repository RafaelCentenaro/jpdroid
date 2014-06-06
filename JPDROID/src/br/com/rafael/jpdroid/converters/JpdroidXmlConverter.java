package br.com.rafael.jpdroid.converters;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.database.Cursor;
import android.util.Xml;
import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.Entity;
import br.com.rafael.jpdroid.annotations.RelationClass;

/**
 * Classe responsável pela conversão objetos para o formato XML.
 * 
 * @author Rafael Centenaro
 */
public class JpdroidXmlConverter {
	private static XmlSerializer xmlSerializer = null;
	private static StringWriter writer = null;

	public static String toXml(Cursor cursor) throws Exception {
		createDocument(cursor);
		return writer.toString();
	}

	public static String toXml(Object entity) throws Exception {
		createDocument(entity);
		return writer.toString();
	}

	private static void createDocument(Object entity) throws Exception {
		xmlSerializer = Xml.newSerializer();
		writer = new StringWriter();

		xmlSerializer.setOutput(writer);
		// start DOCUMENT
		xmlSerializer.startDocument("UTF-8", true);

		createElement(entity);

		// end DOCUMENT
		xmlSerializer.endDocument();

	}

	private static void createDocument(Cursor cursor) throws Exception {
		xmlSerializer = Xml.newSerializer();
		writer = new StringWriter();

		xmlSerializer.setOutput(writer);
		// start DOCUMENT
		xmlSerializer.startDocument("UTF-8", true);

		createElement(cursor);

		// end DOCUMENT
		xmlSerializer.endDocument();

	}

	public static void createElement(Cursor cursor) throws Exception {
		cursor.moveToFirst();
		if (cursor.getCount() > 1) {
			xmlSerializer.startTag("", "Cursor");
		}
		while (cursor.isAfterLast() == false) {
			xmlSerializer.startTag("", "Registro");

			int totalColumn = cursor.getColumnCount();

			for (int i = 0; i < totalColumn; i++) {
				if (cursor.getColumnName(i) != null) {
					String value = "";
					if (cursor.getString(i) != null) {
						value = cursor.getString(i);
					}
					xmlSerializer.startTag("", cursor.getColumnName(i));
					xmlSerializer.text(value);
					xmlSerializer.endTag("", cursor.getColumnName(i));
				}
			}
			xmlSerializer.endTag("", "Registro");
			cursor.moveToNext();
		}
		if (cursor.getCount() > 1) {
			xmlSerializer.endTag("", "Cursor");
		}

	}

	public static void createElement(Object entity) throws Exception {
		Object[] element = null;

		if (entity instanceof List) {
			element = ((List<?>) entity).toArray(new Object[((List<?>) entity).size()]);
			if (element.length > 1) {
				xmlSerializer.startTag("", "List_" + element[0].getClass().getSimpleName());
			}
		} else {
			element = new Object[1];
			element[0] = entity;
		}

		for (Object item : element) {
			Class<?> classe = item.getClass();

			xmlSerializer.startTag("", classe.getSimpleName());

			Field[] declaredFields = classe.getDeclaredFields();
			//Collections.reverse(Arrays.asList(declaredFields));
			for (Field field : declaredFields) {
				Column column = field.getAnnotation(Column.class);
				RelationClass relationClass = field.getAnnotation(RelationClass.class);
				if (column != null || relationClass != null) {
					field.setAccessible(true);
					Object child = field.get(item);
					if (child != null) {
						if (child instanceof List || child.getClass().getAnnotation(Entity.class) != null) {

							createElement(child);
						} else {
							// open tag: <topic>
							xmlSerializer.startTag("", field.getName());
							xmlSerializer.text(child.toString());
							// close tag: </topic>
							xmlSerializer.endTag("", field.getName());
						}
					}
				}
			}

			// close tag: </record>
			xmlSerializer.endTag("", classe.getSimpleName());

		}
		if (element.length > 1) {
			xmlSerializer.endTag("", "List_" + element[0].getClass().getSimpleName());
		}

	}

}
