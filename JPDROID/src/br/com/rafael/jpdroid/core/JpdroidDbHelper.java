package br.com.rafael.jpdroid.core;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.ForeignKey;
import br.com.rafael.jpdroid.annotations.PrimaryKey;
import br.com.rafael.jpdroid.exceptions.JpdroidException;

/**
 * Classe responsável pela criação, atualização e conexão do banco de dados.
 * 
 * @author Rafael Centenaro
 */
public class JpdroidDbHelper extends SQLiteOpenHelper {

	private JpdroidConfiguration dbConfiguration = new JpdroidConfiguration();
	private boolean dropTable = false;
	private boolean create = false;

	public boolean isDropTable() {
		return dropTable;
	}

	public void setDropTable(boolean dropTable) {
		this.dropTable = dropTable;
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public JpdroidDbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
		super(context, databaseName, factory, databaseVersion);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		setCreate(true);
		createTables(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(JpdroidDbHelper.class.getName(), "Atualizando versão do banco " + oldVersion + " para " + newVersion
		    + ", todos os dados serão destrídos.");
		this.setDropTable(true);
		onCreate(db);
		this.setDropTable(false);

	}

	protected String getSqlType(Class<?> propertyType) throws JpdroidException {
		String value = "";
		if ("String".equalsIgnoreCase(propertyType.getSimpleName())
				|| ("Boolean".equalsIgnoreCase(propertyType.getSimpleName()))
		    || ("java.util.Date".equals(propertyType.getName())) || ("java.sql.Date".equals(propertyType.getName()))
		    || ("Calendar".equals(propertyType.getSimpleName()))) {
			value = "TEXT";
		} else if (("Double".equalsIgnoreCase(propertyType.getSimpleName()))
		    || ("Float".equalsIgnoreCase(propertyType.getSimpleName()))) {
			value = "REAL";
		} else if (("Integer".equals(propertyType.getSimpleName())) || ("int".equals(propertyType.getSimpleName()))
		    || ("Long".equalsIgnoreCase(propertyType.getSimpleName()))
		    || ("Short".equalsIgnoreCase(propertyType.getSimpleName()))){
			value = "INTEGER";
		} else if (("Byte[]".equalsIgnoreCase(propertyType.getSimpleName()))
		    || ("Bitmap".equalsIgnoreCase(propertyType.getSimpleName()))) {
			value = "BLOB";
		} else {
			throw new JpdroidException("O tipo " + propertyType + " não suportado por este banco de dados.");
		}
		return value;
	}

	public void createTables(SQLiteDatabase db) {

		String tableName;
		String columnName;
		String columnType;
		int count, pos = 0, index = 0;
		try {
			List<Class<?>> entidades = dbConfiguration.getEntidades();
			for (Class<?> entity : entidades) {
				pos = 0;
				index++;
				StringBuilder createTable = new StringBuilder();
				StringBuilder createPrimaryKey = new StringBuilder();
				StringBuilder createColuns = new StringBuilder();
				StringBuilder createForeinKey = new StringBuilder();
				StringBuilder createUnique = new StringBuilder();
				tableName = entity.getSimpleName();

				if (isDropTable()) {
					createTable.append(" DROP TABLE IF EXISTS " + tableName + "; ");
				}
				createTable.append(" CREATE TABLE IF NOT EXISTS ");

				createTable.append(tableName + "(");

				Field[] fields = entity.getDeclaredFields();
				Collections.reverse(Arrays.asList(fields));
				count = fields.length;
				for (Field field : fields) {
					pos++;
					PrimaryKey annotationPrimaryKey = field.getAnnotation(PrimaryKey.class);
					Column annotationColumn = field.getAnnotation(Column.class);
					ForeignKey annotationForeingKey = field.getAnnotation(ForeignKey.class);

					if (annotationColumn != null) {

						if ("".equals(annotationColumn.name())) {
							columnName = field.getName();
						} else {
							columnName = annotationColumn.name();
						}

						columnType = getSqlType(field.getType());

						if (annotationPrimaryKey != null) {
							createPrimaryKey.append(columnName + " " + columnType);
							createPrimaryKey.append(" PRIMARY KEY ");
							if (annotationPrimaryKey.autoGenerate()) {
								createPrimaryKey.append(" AUTOINCREMENT ");
							}
							createPrimaryKey.append(" NOT NULL ");

						} else {

							createColuns.append(",");
							createColuns.append(columnName + " " + columnType);
							if (!annotationColumn.nullable()) {
								createColuns.append(" NOT NULL ");
							}
							if (annotationColumn.unique()) {
								if (createUnique.length() > 0) {
									createUnique.append(",");
								}
								createUnique.append(columnName);
							}
							if (annotationForeingKey != null) {
								createForeinKey.append(",");
								createForeinKey.append(" FOREIGN KEY(" + columnName + ") REFERENCES "
								    + annotationForeingKey.joinEntity() + "(" + annotationForeingKey.joinPrimaryKey() + ") ");
								if (annotationForeingKey.deleteCascade()) {
									createForeinKey.append(" ON DELETE CASCADE ");
								}
							}
						}
					}

					if (pos == count) {
						createTable.append(createPrimaryKey);
						createTable.append(createColuns);
						createTable.append(createForeinKey);
						createTable.append("); ");
					}
				}
				
				db.execSQL(createTable.toString());
				
				if (createUnique.length() > 0) {
					db.execSQL("CREATE UNIQUE INDEX index" + index + " ON " + tableName + "(" + createUnique.toString() + ");");
				}
			}

		} catch (Exception e) {
			Log.e("Erro createTables()", e.getMessage());
		}

		dbConfiguration = null;
	}

	public void addClass(Class<?> class1) throws JpdroidException {
		dbConfiguration.addClass(class1);

	}

	public boolean isValid() {
		if (isCreate() && dbConfiguration != null) {
			return dbConfiguration.getEntidades().size() > 0;
		}
		return true;
	}

}
