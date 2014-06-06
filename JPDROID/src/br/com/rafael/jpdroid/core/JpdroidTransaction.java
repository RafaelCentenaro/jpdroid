package br.com.rafael.jpdroid.core;

import android.database.sqlite.SQLiteDatabase;
import br.com.rafael.jpdroid.interfaces.ITransaction;

/**
 * Classe que implementa transações para o banco SqLite.
 * 
 * @author Rafael Centenaro
 */
public class JpdroidTransaction implements ITransaction {

	private SQLiteDatabase database;

	public JpdroidTransaction(SQLiteDatabase db) {
		database = db;

	}

	@Override
	public void begin() {
		database.beginTransaction();
	}

	@Override
	public void commit() {
		database.setTransactionSuccessful();
	}

	@Override
	public void end() {
		if (database.inTransaction()) {
			database.endTransaction();
		}
	}

}
