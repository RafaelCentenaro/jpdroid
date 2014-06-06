package br.com.rafael.jpdroid.util;

import br.com.rafael.jpdroid.core.JpdroidWriteFile;
import android.database.Cursor;

/**
 * Classe responsável por exportar arquivos no formato JSON para o cartão de memória.
 * 
 * @author Rafael Centenaro
 */
public class JpdroidJsonFile extends JpdroidWriteFile {

	public static void export(Cursor cursor) {
		export(cursor, "JsonFile" + getDateNow() + ".JSON");
	}

	public static void export(Cursor cursor, String fileName) {
		String card_string;
		try {
			card_string = JpdroidConverter.toJson(cursor).toString();

			writeFile(card_string, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void export(Object entity) {
		export(entity, "JsonFile" + getDateNow() + ".JSON");
	}

	public static void export(Object entity, String fileName) {
		String card_string;
		try {
			card_string = JpdroidConverter.toJson(entity).toString();

			writeFile(card_string, fileName);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}