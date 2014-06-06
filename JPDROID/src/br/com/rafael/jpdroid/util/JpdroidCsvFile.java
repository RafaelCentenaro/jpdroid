package br.com.rafael.jpdroid.util;

import br.com.rafael.jpdroid.core.JpdroidWriteFile;
import android.database.Cursor;

/**
 * Classe responsável por exportar arquivos no formato CSV para o cartão de memória.
 * 
 * @author Rafael Centenaro
 */
public class JpdroidCsvFile extends JpdroidWriteFile {

	public static void export(Cursor cursor) {
		export(cursor, "CsvFile" + getDateNow() + ".csv");
	}

	public static void export(Cursor cursor, String fileName) {
		String card_string;
		try {
			
			card_string = JpdroidConverter.toCsv(cursor).toString();
			writeFile(card_string, fileName);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void export(Object entity) {
		export(entity, "CsvFile" + getDateNow() + ".csv");
	}

	public static void export(Object entity, String fileName) {
		String card_string;
		try {

			card_string = JpdroidConverter.toCsv(entity).toString();
			writeFile(card_string, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}