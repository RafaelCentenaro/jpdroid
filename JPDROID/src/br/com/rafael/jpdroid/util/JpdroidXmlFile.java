package br.com.rafael.jpdroid.util;


import java.io.File;

import br.com.rafael.jpdroid.core.JpdroidWriteFile;
import android.database.Cursor;
/**
 * Classe responsável por exportar arquivos no formato XML para o cartão de memória.
 * 
 * @author Rafael Centenaro
 *
 */
public class JpdroidXmlFile extends JpdroidWriteFile {

	public static void export(Object entity) {
		export(entity, "XmlFile" + getDateNow() + ".XML");
	}
	
	public static void export(Cursor cursor, File file) {
		String card_string;
		try {

			card_string = JpdroidConverter.toXml(cursor).toString();
			writeFile(file, card_string);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void export(Object entity, String fileName) {

		try {

			writeFile(JpdroidConverter.toXml(entity), fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void export(Cursor cursor) {
		export(cursor, "XmlFile" + getDateNow() + ".XML");
	}
	
	public static void export(Object entity, File file) {
		String card_string;
		try {

			card_string = JpdroidConverter.toXml(entity).toString();
			writeFile(file, card_string);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void export(Cursor cursor, String fileName) {

		try {

			writeFile(JpdroidConverter.toXml(cursor), fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
