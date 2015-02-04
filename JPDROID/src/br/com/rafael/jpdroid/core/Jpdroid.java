package br.com.rafael.jpdroid.core;

import static br.com.rafael.jpdroid.core.JpdroidObjectMap.getContentvalues;
import static br.com.rafael.jpdroid.core.JpdroidObjectMap.getDefaultOrderBy;
import static br.com.rafael.jpdroid.core.JpdroidObjectMap.getFieldByAnnotation;
import static br.com.rafael.jpdroid.core.JpdroidObjectMap.getFieldsByForeignKey;
import static br.com.rafael.jpdroid.core.JpdroidObjectMap.getFieldsByRelationClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.PrimaryKey;
import br.com.rafael.jpdroid.annotations.RelationClass;
import br.com.rafael.jpdroid.annotations.ViewColumn;
import br.com.rafael.jpdroid.converters.JpdroidDateUtil;
import br.com.rafael.jpdroid.enums.RelationType;
import br.com.rafael.jpdroid.enums.ScriptPath;
import br.com.rafael.jpdroid.exceptions.JpdroidException;

/**
 * Classe singleton, responsável pelas operações de banco de dados.
 * 
 * @author Rafael Centenaro
 */
public class Jpdroid {

	private TreeMap<String, String> entidades = new TreeMap<String, String>();

	private SQLiteDatabase database;

	private JpdroidDbHelper dbHelper;

	private Context context;

	private JpdroidTransaction transaction = null;

	private static Jpdroid jpdroid = null;

	private String databaseName = "JpdroidDB.db";

	private CursorFactory factory;

	private int databaseVersion = 1;

	/**
	 * Retorna instância da classe Jpdroid.
	 * 
	 * @return
	 */
	public static Jpdroid getInstance() {
		if (jpdroid == null) {
			jpdroid = new Jpdroid();
			return jpdroid;
		}
		return jpdroid;
	}

	private Jpdroid() {

	}

	/**
	 * Indica se existe conexão aberta.
	 * 
	 * @return
	 */
	public boolean isOpen() {
		if (database == null) {
			return false;
		}
		return database.isOpen();
	}

	/**
	 * Retorna informações sobre o framework.
	 * 
	 * @return
	 */
	public JpdroidAbout getAbout() {
		return new JpdroidAbout();
	}

	/**
	 * Retorna o contexto.
	 * 
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	public Date getDate() {
		Date d = Calendar.getInstance().getTime();
		return d;
	}

	/**
	 * Atribui o context
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Retorna o nome do banco.
	 * 
	 * @return
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * Atribui um nome para o banco de dados. Nome padrão:JpdroidDB Obs:Não é
	 * necessário informar as extensão do banco.
	 * 
	 * @param databaseName
	 */
	public void setDatabaseName(String databaseName) {

		if (databaseName.indexOf(".db") < 0) {
			this.databaseName = databaseName + ".db";
		} else {
			this.databaseName = databaseName;
		}
	}

	/**
	 * Retorna a versão do banco.
	 * 
	 * @return
	 */
	public int getDatabaseVersion() {
		return databaseVersion;
	}

	/**
	 * Versão do banco, por default a versão é 1. Quando a versão do banco for
	 * diferente a base de dados será atualizada.
	 * 
	 * @param databaseVersion
	 */
	public void setDatabaseVersion(int databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	/**
	 * Retorna instância do SQLiteDatabase.
	 * 
	 * @return
	 */
	public SQLiteDatabase getDatabase() {
		return database;
	}

	/**
	 * Abre conexão com o banco de dados.
	 * 
	 * @throws JpdroidException
	 * @throws SQLException
	 */
	public void open() {

		if (!isOpen()) {

			try {
				validar();

				if (dbHelper == null) {
					dbHelper = new JpdroidDbHelper(getContext(),
							getDatabaseName(), factory, databaseVersion);
				}

				database = dbHelper.getWritableDatabase();

				transaction = new JpdroidTransaction(database);

				if (!database.isReadOnly()) {
					database.execSQL("PRAGMA foreign_keys = ON;");
				}
			} catch (Exception e) {

				Log.w("Erro open()", e.getMessage());
			}

		}
	}

	/**
	 * Fecha conexão com o banco de dados.
	 */
	public void close() {
		if (isOpen()) {
			dbHelper.close();
		}
	}

	/**
	 * Restaura o banco de dados a partir do arquivo de backup
	 * ("backupJpdroid.jpdroid") criado no cartão sd. Atenção: Este método irá
	 * reconstruir a base de dados para depois importar e executar o script de
	 * backup, ao reconstuir a base todos os dados serão perdidos, e os
	 * registros do script serão inseridos.
	 * 
	 * @return
	 */
	public int importDbScript() {
		String processName = this.getContext().getApplicationInfo().processName;
		processName = processName.replace('.', '_');
		return importDbScript(processName + ".bkp");
	}

	/**
	 * Restaura o banco de dados a partir do arquivo de backup cujo nome será
	 * passado por parâmetro, arquivo que deve estar criado no cartão sd.
	 * Atenção: Este método irá reconstruir a base de dados para depois importar
	 * e executar o script de backup, ao reconstuir a base todos os dados serão
	 * perdidos, e os registros do script serão inseridos.
	 * 
	 * @return
	 */
	public int importDbScript(String fileName) {
		int retorno = -1;

		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File dir = Environment.getExternalStorageDirectory();
			File file = new File(dir, fileName);
			if (file.exists()) {

				dbHelper.regenerateDB(database);
				retorno = importSqlScript(ScriptPath.SdCard, fileName);
				database.execSQL("PRAGMA foreign_keys = ON;");
			}
		}
		return retorno;
	}

	/**
	 * Permite forçar a cricação de uma tabela.
	 * 
	 * @param entity
	 */
	public void createTable(Class<?> entity) {
		try {
			dbHelper.setDropTable(true);

			dbHelper.createTable(database, entity);

			dbHelper.setDropTable(false);
		} catch (JpdroidException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Habilita as constraints para chaves estrangeiras.
	 */
	public void foreignKeysOn() {
		database.execSQL("PRAGMA foreign_keys = ON;");
	}

	/**
	 * Desabilita as constraints para chaves estrangeiras.
	 */
	public void foreignKesOff() {
		database.execSQL("PRAGMA foreign_keys = OFF;");
	}

	private static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}

	/**
	 * Cria arquivo de backup chamado "br_com_nome_pacote.bkp" no cartão sd.
	 */
	public int exportDbScript() {
		String processName = this.getContext().getApplicationInfo().processName;
		processName = processName.replace('.', '_');
		return exportDbScript(processName + ".bkp");
	}

	/**
	 * Cria arquivo de backup cujo nome será passado por parâmetro e este será
	 * criado no cartão sd.
	 */

	public int exportDbScript(String fileName) {
		File dir = Environment.getExternalStorageDirectory();
		File file = new File(dir, fileName);
		return exportDbScript(file);
	}

	/**
	 * Exporta script dos dados do banco para referência do arquivo instânciado
	 * através classe "File".
	 * 
	 * @param file
	 * @return
	 */
	public int exportDbScript(File file) {

		try {
			StringBuilder arquivo = new StringBuilder();
			for (Map.Entry<String, String> entry : entidades.entrySet()) {
				Cursor cursor = query(entry.getKey());

				if (cursor.getCount() > 0) {
					String linha = "";
					String columns = "", values = "";
					cursor.moveToFirst();

					SQLiteCursor sqLiteCursor = (SQLiteCursor) cursor;
					CursorWindow cursorWindow = sqLiteCursor.getWindow();

					while (cursor.isAfterLast() == false) {
						int totalColumn = cursor.getColumnCount();
						linha = "INSERT INTO " + entry.getKey() + " ";
						columns = "";
						values = "";
						for (int i = 0; i < totalColumn; i++) {
							if (cursor.getColumnName(i) != null) {
								if (columns.length() == 0) {
									columns = cursor.getColumnName(i);
								} else {
									columns += "," + cursor.getColumnName(i);
								}
								try {

									if (cursorWindow.isBlob(
											cursor.getPosition(), i)
											&& !cursorWindow.isNull(
													cursor.getPosition(), i)) {
										if (values.length() == 0) {

											values = "X'"
													+ byteArrayToHex(cursor
															.getBlob(i)) + "'";
										} else {
											values += ",X'"
													+ byteArrayToHex(cursor
															.getBlob(i)) + "'";
										}
									} else if (cursorWindow.isNull(
											cursor.getPosition(), i)) {
										if (values.length() == 0) {
											values = "null";
										} else {
											values += ",null";
										}
									} else {
										if (cursor.getString(i) != null) {
											if (values.length() == 0) {
												if (cursorWindow
														.isString(cursor
																.getPosition(),
																i)) {
													values = "'"
															+ cursor.getString(i)
															+ "'";
												} else {
													values = cursor
															.getString(i);
												}

											} else {
												if (cursorWindow
														.isString(cursor
																.getPosition(),
																i)) {
													values += ", '"
															+ cursor.getString(i)
															+ "'";
												} else {
													values += ","
															+ cursor.getString(i);
												}

											}
										} else {
											if (values.length() == 0) {
												values = "null";
											} else {
												values += ",null";
											}
										}
									}
								} catch (Exception e) {
									Log.d("error", e.getMessage());
								}
							}

						}
						linha += "(" + columns + ") VALUES (" + values + ");";
						arquivo.append(linha);
						arquivo.append("\n");
						cursor.moveToNext();
					}
				}
			}

			if (arquivo.length() == 0) {
				return 0;
			}

			JpdroidWriteFile.writeFile(file, arquivo.toString());

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 1;

	}

	/**
	 * Importa arquivo databaseName.db do cartão. Atenção: Este método irá
	 * substituir a base de dados atual pela versão do backup, este processo uma
	 * vez executado não permite o cancelamento.
	 * 
	 * @return
	 */
	public int importDbFile() {
		File sd = Environment.getExternalStorageDirectory();
		File currentDB = new File(sd, databaseName);
		return importDbFile(currentDB);
	}

	/**
	 * Importa arquivo arquivo através da referência instanciada pela classe
	 * "File" Atenção: Este método irá substituir a base de dados atual pela
	 * versão do backup, este processo uma vez executado não permite o
	 * cancelamento.
	 * 
	 * @return
	 */
	@SuppressWarnings("resource")
	public int importDbFile(File currentDB) {
		try {
			File data = Environment.getDataDirectory();

			if (currentDB.canRead()) {
				String currentDBPath = "//data//"
						+ this.getContext().getPackageName() + "//databases//"
						+ databaseName;

				File backupDB = new File(data, currentDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();

			} else {
				return 0;
			}
		} catch (Exception e) {

			return -1;

		}
		return 1;
	}

	/**
	 * Exporta copia do arquivo do banco de dados para o cartão.
	 * 
	 * @return
	 */
	public int exportDbFile() {

		File sd = Environment.getExternalStorageDirectory();
		File backupDB = new File(sd, databaseName);
		return exportDbFile(backupDB);
	}

	/**
	 * Exporta copia do arquivo do banco de dados para referencia do arquivo
	 * instanciada através da classe "File"
	 * 
	 * @param backupDB
	 * @return
	 */
	@SuppressWarnings("resource")
	public int exportDbFile(File backupDB) {
		try {

			File data = Environment.getDataDirectory();
			if (!backupDB.exists()) {
				backupDB.createNewFile();
			}

			if (backupDB.canWrite()) {
				String currentDBPath = "//data//"
						+ this.getContext().getPackageName() + "//databases//"
						+ databaseName;

				File currentDB = new File(data, currentDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();

			} else {
				return 0;
			}
		} catch (Exception e) {

			return -1;

		}
		return 1;
	}

	private void validar() throws JpdroidException {
		if (getContext() == null) {
			throw new JpdroidException(
					"O atributo context é nulo, configure o contexto através do método setContext()");
		}
		if (dbHelper != null && !dbHelper.isValid()) {
			throw new JpdroidException(
					"Nenhuma entidade foi configurada, adicione as entidades através do método addEntity().");
		}

	}

	/**
	 * Deleta registros da tabela de acordo com os parâmetros.
	 * 
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return 1:Sucesso, -1:Erro, 0:Falha
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		int retorno = 0;
		try {
			transaction.begin();
			retorno = database.delete(table, whereClause, whereArgs);
			transaction.commit();
		} catch (Exception e) {
			transaction.end();
			Log.e("Erro Deletar", e.getMessage());
		} finally {
			transaction.end();
		}
		return retorno;
	}

	/**
	 * Deleta todos os registros da entidade.
	 * 
	 * @param entity
	 * @return 1:Sucesso, -1:Erro, 0:Falha
	 */
	public int deleteAll(Class<?> entity) {

		int retorno = delete(entity.getSimpleName(), "", null);

		return retorno;

	}

	/**
	 * Deleta registro referente a instância do objeto.
	 * 
	 * @param entity
	 */
	public void delete(Object entity) {

		delete(entity.getClass(), entity);
	}

	/**
	 * Deleta registros da entidade.
	 * 
	 * @param entity
	 * @param object
	 *            - Pode ser uma lista de objetos ou um cursor.
	 * @return 1:Sucesso, -1:Erro, 0:Falha
	 */
	public int delete(Class<?> entity, Object object) {

		int retorno = 0;
		try {
			if (object instanceof List) {
				for (Object item : ((List<?>) object)) {
					delete(item);
				}
				retorno = 1;
			} else {
				StringBuilder whereClause = new StringBuilder();
				List<String> whereArgs = new ArrayList<String>();

				String columnName = null;

				Field[] fields = entity.getDeclaredFields();
				for (Field field : fields) {

					if (field.getType().getSimpleName()
							.equalsIgnoreCase("Bitmap")
							|| field.getType().getSimpleName()
									.equalsIgnoreCase("Byte[]")) {
						continue;
					}
					Column annotationColumn = field.getAnnotation(Column.class);

					if (annotationColumn != null) {
						if ("".equals(annotationColumn.name())) {
							columnName = field.getName();
						} else {
							columnName = annotationColumn.name();
						}

						try {
							if (object instanceof Cursor) {
								Cursor cursor = (Cursor) object;
								if (cursor.getColumnIndex(columnName) >= 0) {

									if (whereClause.length() > 0) {
										whereClause.append(" AND ");
									}
									whereClause.append(columnName + " = ?");

									whereArgs.add(cursor.getString(cursor
											.getColumnIndex(columnName)));
								}
							} else {

								if (whereClause.length() > 0) {
									whereClause.append(" AND ");
								}

								whereClause.append(columnName + " = ?");

								field.setAccessible(true);
								whereArgs
										.add(String.valueOf(field.get(object)));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
				if (whereClause.length() > 0) {
					retorno = delete(entity.getSimpleName(),
							whereClause.toString(),
							whereArgs.toArray(new String[whereArgs.size()]));
				}
			}
		} catch (Exception e) {
			retorno = -1;
			Log.e("Erro delete()", e.getMessage());
		}
		return retorno;
	}

	/**
	 * Insere objeto no banco.
	 * 
	 * @param entity
	 *            - Instância da entidade.
	 * @return - retorna o id
	 */
	private Long insert(Object entity) {

		ContentValues values = getContentvalues(entity);

		return insert(values, entity.getClass().getSimpleName());
	}

	private Long insert(ContentValues values, String tableName) {

		Long insertId = Long.valueOf(database.insert(tableName, null, values));

		return insertId;
	}

	/**
	 * Atualiza registro no banco referente ao objeto.
	 * 
	 * @param entity
	 *            - Instância da entidade.
	 * @return 1:Sucesso, -1:Erro, 0:falhou
	 */
	private Long update(Object entity) {

		Long insertId = 0L;
		try {

			ContentValues values = getContentvalues(entity);

			StringBuilder whereClause = new StringBuilder();
			List<String> whereArgs = new ArrayList<String>();

			String columnName = null;

			Field[] fields = entity.getClass().getDeclaredFields();
			for (Field field : fields) {

				PrimaryKey annotationId = field.getAnnotation(PrimaryKey.class);
				Column annotationColumn = field.getAnnotation(Column.class);

				if (annotationId != null && annotationColumn != null) {

					if ("".equals(annotationColumn.name())) {
						columnName = field.getName();
					} else {
						columnName = annotationColumn.name();
					}
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(columnName + " = ?");

					field.setAccessible(true);
					if (field.get(entity) == null
							|| String.valueOf(field.get(entity)).equals("0")) {
						throw new JpdroidException("A coluna "
								+ field.getName() + " não possui valor!");
					}
					whereArgs.add(String.valueOf(field.get(entity)));

				}

			}

			insertId = Long.valueOf(database.update(entity.getClass().getSimpleName(),
					values, whereClause.toString(),
					whereArgs.toArray(new String[whereArgs.size()])));

		} catch (Exception e) {

			Log.e("Erro Update()", e.getMessage());
		}

		return insertId;
	}

	/**
	 * Cria consulta sql de acordo com os parâmetros.
	 * 
	 * @param entity
	 *            - Entidade
	 * @param restrictions
	 *            - Clasula where
	 * @return - Cursor <br>
	 *         Dica:http://developer.android.com/reference/android/database/
	 *         sqlite/SQLiteQueryBuilder.html
	 */
	@SuppressLint("DefaultLocale")
	public Cursor createQuery(Class<?> entity, String restrictions, String order) {
		if (!restrictions.toUpperCase().contains("WHERE")
				&& restrictions.length() > 0) {
			restrictions = " WHERE " + restrictions;
		}

		String orderBy = order;
		if (orderBy == null || orderBy.length() == 0) {
			orderBy = getDefaultOrderBy(entity);
		}

		if (orderBy != null && orderBy.length() > 0) {
			restrictions += " Order By " + orderBy;
		}

		Cursor cursor = database.rawQuery(
				"select * from " + entity.getSimpleName() + restrictions, null);
		return cursor;
	}

	/**
	 * Retorna todos os registros da entidade.
	 * 
	 * @param entity
	 *            - Entidade
	 * @return - Cursor <br>
	 *         Dica:http://developer.android.com/reference/android/database/
	 *         sqlite/SQLiteQueryBuilder.html
	 */
	public Cursor createQuery(Class<?> entity) {
		Cursor cursor = createQuery(entity, "", null);
		return cursor;
	}

	public Cursor createQuery(Class<?> entity, String restrictions) {
		Cursor cursor = createQuery(entity, restrictions, null);
		return cursor;
	}

	/**
	 * Adiciona as entidades para validação.
	 * 
	 * @param entity
	 */
	public void addEntity(Class<?> entity) {
		try {
			if (dbHelper == null) {
				dbHelper = new JpdroidDbHelper(getContext(), getDatabaseName(),
						factory, databaseVersion);
			}
			dbHelper.addClass(entity);
			entidades.put(entity.getSimpleName().toUpperCase(),
					entity.getName());
		} catch (Exception e) {
			Log.e("Erro addEntity()", e.getMessage());
		}

	}

	/**
	 * Retorna uma lista de objetos preenchidos.
	 * 
	 * @param entity
	 * @return List<Object>
	 */
	public <T> List<T> retrieve(Class<T> entity) {
		return retrieve(entity, "", null, false, null);
	}

	/**
	 * Retorna uma lista de objetos preenchidos.
	 * 
	 * @param entity
	 * @param fillRelationClass
	 *            - Indica se deve preencher as classes relacionadas.
	 * @return List<Object>
	 */
	public <T> List<T> retrieve(Class<T> entity, boolean fillRelationClass) {
		return retrieve(entity, "", null, fillRelationClass, null);
	}

	/**
	 * Retorna uma lista de objetos preenchidos.
	 * 
	 * @param entity
	 * @param restrictions
	 * @return List<Object>
	 */
	public <T> List<T> retrieve(Class<T> entity, String restrictions) {
		return retrieve(entity, restrictions, null, false, null);
	}

	/**
	 * Retorna uma lista de objetos preenchidos.
	 * 
	 * @param entity
	 * @param restrictions
	 *            - Cláusula where.
	 * @param fillRelationClass
	 *            - Indica se deve preencher as classes relacionadas.
	 * @return List<Object>
	 */
	public <T> List<T> retrieve(Class<T> entity, String restrictions,
			boolean fillRelationClass) {
		return retrieve(entity, restrictions, null, fillRelationClass, null);
	}

	/**
	 * Retorna uma lista de objetos preenchidos.
	 * 
	 * @param entity
	 * @param restrictions
	 *            - Cláusula where.
	 * @param fillRelationClass
	 *            - Indica se deve preencher as classes relacionadas.
	 * @param order
	 * @return List<Object>
	 */
	public <T> List<T> retrieve(Class<T> entity, String restrictions,
			String order, boolean fillRelationClass) {
		return retrieve(entity, restrictions, order, fillRelationClass, null);
	}

	/**
	 * Retorna uma lista de objetos preenchidos.
	 * 
	 * @param entity
	 * @param lastEntity
	 *            - Para casos que existe relacionamento ManyToMany
	 * @param restrictions
	 * @param order
	 * @param fillRelationClass
	 * @return
	 */
	private <T> List<T> retrieve(Class<T> entity, String restrictions,
			String order, boolean fillRelationClass, Class<?> lastEntity) {

		Object retorno = null;
		String orderBy = order;

		if (orderBy == null || orderBy.length() == 0) {
			orderBy = getDefaultOrderBy(entity);
		}

		List<T> entityList = new ArrayList<T>();
		try {

			if (restrictions.length() > 0) {
				restrictions = " where " + restrictions;
			}
			if (orderBy != null && orderBy.length() > 0) {
				restrictions += " Order By " + orderBy;
			}
			String columnName;
			Cursor cursor = database.rawQuery(
					"select * from " + entity.getSimpleName() + restrictions,
					null);
			cursor.moveToFirst();
			if (cursor.getCount() == 0) {
				// entityList.add(entity.newInstance());
				return entityList;
			}
			do {
				retorno = entity.newInstance();

				Field fieldPk = getFieldByAnnotation(retorno, PrimaryKey.class);

				Field[] fields = entity.getDeclaredFields();
				for (Field field : fields) {

					if (fillRelationClass) {

						RelationClass relationClass = field
								.getAnnotation(RelationClass.class);
						if (relationClass != null) {

							Class<? extends Object> ob = field.getType();

							field.setAccessible(true);

							String sql = "";

							if (ob.isAssignableFrom(List.class)) {

								ParameterizedType fieldGenericType = (ParameterizedType) field
										.getGenericType();
								Class<?> fieldTypeParameterType = (Class<?>) fieldGenericType
										.getActualTypeArguments()[0];

								boolean ignoreChild = lastEntity != null
										&& lastEntity
												.equals(fieldTypeParameterType)
										&& relationClass.relationType().equals(
												RelationType.ManyToMany);

								if (relationClass.relationType().equals(
										RelationType.ManyToMany)) {
									sql = "_id in (SELECT _id"
											+ fieldTypeParameterType
													.getSimpleName()
											+ " from "
											+ relationClass.joinTable()
											+ " where _id"
											+ entity.getSimpleName()
											+ " = "
											+ cursor.getLong(cursor.getColumnIndex(String
													.valueOf(fieldPk.getName())))
											+ " )";
								} else {

									sql = relationClass.joinColumn()
											+ " = "
											+ cursor.getLong(cursor.getColumnIndex(String
													.valueOf(fieldPk.getName())));
								}

								if (!ignoreChild) {
									List<?> objetos = retrieve(
											fieldTypeParameterType, sql, null,
											fillRelationClass, entity);
									if (objetos.size() > 0) {
										field.set(retorno, objetos);
									}
								}

							} else {
								if ((relationClass.relationType() == RelationType.OneToMany)
										|| (relationClass.relationType() == RelationType.OneToOne)) {
									sql = "_id = ";
									sql += cursor.getLong(cursor
											.getColumnIndex(String
													.valueOf(relationClass
															.joinColumn())));
								} else {
									sql = relationClass.joinColumn() + " = ";
									sql += cursor
											.getLong(cursor.getColumnIndex(String
													.valueOf(fieldPk.getName())));
								}

								List<?> objetos = retrieve(ob, sql,
										fillRelationClass);
								if (objetos.size() > 0) {
									field.set(retorno, objetos.get(0));
								}
							}

						}
					}
					Column annotationColumn = field.getAnnotation(Column.class);
					if (annotationColumn != null) {
						if ("".equals(annotationColumn.name())) {
							columnName = field.getName();
						} else {
							columnName = annotationColumn.name();
						}
						field.setAccessible(true);

						if ("String".equalsIgnoreCase(field.getType()
								.getSimpleName())) {

							field.set(retorno, cursor.getString(cursor
									.getColumnIndex(columnName)));

						} else if (("java.util.Date".equals(field.getType()
								.getName()))
								|| ("java.sql.Date".equals(field.getType()
										.getName()))
								|| ("Calendar".equals(field.getType()
										.getSimpleName())))
							field.set(retorno, JpdroidDateUtil.convert(cursor
									.getString(cursor
											.getColumnIndex(columnName)), field
									.getType()));

						else if ("Boolean".equalsIgnoreCase(field.getType()
								.getSimpleName())) {

							field.set(retorno, Boolean.valueOf(cursor
									.getString(cursor
											.getColumnIndex(columnName))));

						} else if ("Double".equalsIgnoreCase(field.getType()
								.getSimpleName())) {

							field.set(retorno, cursor.getDouble(cursor
									.getColumnIndex(columnName)));

						} else if ("Float".equalsIgnoreCase(field.getType()
								.getSimpleName())) {

							field.set(retorno, cursor.getFloat(cursor
									.getColumnIndex(columnName)));

						} else if (("Integer".equals(field.getType()
								.getSimpleName()))
								|| ("int".equals(field.getType()
										.getSimpleName()))) {

							field.set(retorno, cursor.getInt(cursor
									.getColumnIndex(columnName)));

						} else if ("Long".equalsIgnoreCase(field.getType()
								.getSimpleName())) {
							field.set(retorno, cursor.getLong(cursor
									.getColumnIndex(columnName)));

						} else if ("Short".equalsIgnoreCase(field.getType()
								.getSimpleName())) {
							field.set(retorno, cursor.getShort(cursor
									.getColumnIndex(columnName)));
						} else if (("Byte[]".equalsIgnoreCase(field.getType()
								.getSimpleName()))
								|| ("Bitmap".equalsIgnoreCase(field.getType()
										.getSimpleName()))) {
							byte[] blob = cursor.getBlob(cursor
									.getColumnIndex(columnName));
							if (blob != null) {
								Bitmap bmp = BitmapFactory.decodeByteArray(
										blob, 0, blob.length);
								field.set(retorno, bmp);
							}
						}
					}
					ViewColumn viewColumn = field
							.getAnnotation(ViewColumn.class);
					if (viewColumn != null) {
						field.setAccessible(true);
						field.set(
								retorno,
								this.rawQuery(
										"SELECT "
												+ viewColumn.atributo()
												+ " FROM "
												+ viewColumn.entity()
														.getSimpleName()
												+ " WHERE _id = "
												+ cursor.getLong(cursor
														.getColumnIndex(viewColumn
																.foreignKey())),
										null).getString(0));
					}

				}
				entityList.add(entity.cast(retorno));
			} while (cursor.moveToNext());
		} catch (Exception e) {
			Log.e("Erro getObjects()", e.getMessage());
		}

		return entityList;
	}

	/**
	 * Persiste um objeto ou uma lista no banco de dados.
	 * 
	 * @param entity
	 * @throws JpdroidException
	 */
	public void persist(Object entity) throws JpdroidException {

		try {
			transaction.begin();
			persistRecursivo(entity);
			transaction.commit();
		} catch (Exception e) {
			transaction.end();
			throw new JpdroidException(e.getMessage());
		} finally {
			transaction.end();
		}

	}

	/**
	 * Método recursivo para persistência de objetos.
	 * 
	 * @param entity
	 * @return
	 */
	private Long persistRecursivo(Object entity) {

		Long idMaster = 0L;
		try {
			// Persiste objetos da lista
			if (entity instanceof List) {
				for (Object item : ((List<?>) entity)) {
					persistRecursivo(item);
				}
			} else {
				// Classe relacionada de um para muitos
				Field[] fieldRelationClassOneToMany = getFieldsByRelationClass(
						entity, RelationType.OneToMany);
				if (fieldRelationClassOneToMany != null) {
					for (int i = 0; i < fieldRelationClassOneToMany.length; i++) {

						fieldRelationClassOneToMany[i].setAccessible(true);

						Object child = fieldRelationClassOneToMany[i]
								.get(entity);
						if (child != null) {
							if (child instanceof List) {
								for (Object item : ((List<?>) child)) {
									Long idItem = persistRecursivo(item);

									Field[] fieldForeingKeyList = getFieldsByForeignKey(
											entity, item.getClass()
													.getSimpleName());

									if (fieldForeingKeyList != null) {

										for (int u = 0; u < fieldForeingKeyList.length; u++) {
											fieldForeingKeyList[u]
													.setAccessible(true);
											fieldForeingKeyList[u].set(
													entity, idItem);
										}

									}
								}
							} else {
								Long idItem = persistRecursivo(child);
								Field[] fieldForeingKeyList = getFieldsByForeignKey(
										entity, child.getClass()
												.getSimpleName());

								if (fieldForeingKeyList != null) {

									for (int u = 0; u < fieldForeingKeyList.length; u++) {

										fieldForeingKeyList[u]
												.setAccessible(true);
										fieldForeingKeyList[u].set(entity,
												idItem);
									}

								}
							}
						}
					}
				}

				Field fieldPk = getFieldByAnnotation(entity, PrimaryKey.class);
				if (fieldPk != null) {
					fieldPk.setAccessible(true);

					if (fieldPk.get(entity) == null
							|| String.valueOf(fieldPk.get(entity)).equals("0")) {
						idMaster = insert(entity);
					} else {
						idMaster = Long.parseLong(String.valueOf(fieldPk
								.get(entity)));
						update(entity);
					}
				}
				// Pode existir mais de uma classe relacionada
				Field[] fieldRelationClassManyToOne = getFieldsByRelationClass(
						entity, RelationType.ManyToOne);

				if (fieldRelationClassManyToOne != null) {
					for (int i = 0; i < fieldRelationClassManyToOne.length; i++) {

						fieldRelationClassManyToOne[i].setAccessible(true);

						Object child = fieldRelationClassManyToOne[i]
								.get(entity);

						RelationClass relationClass = fieldRelationClassManyToOne[i]
								.getAnnotation(RelationClass.class);

						List<ContentValues> values = null;

						if (child != null
								&& relationClass != null
								&& relationClass.relationType().equals(
										RelationType.ManyToMany)) {
							values = new ArrayList<ContentValues>();
						}
						if (child != null) {
							if (child instanceof List) {
								for (Object item : ((List<?>) child)) {

									if (relationClass != null
											&& relationClass
													.relationType()
													.equals(RelationType.ManyToMany)) {
										ContentValues val = new ContentValues();
										val.put("_id"
												+ entity.getClass()
														.getSimpleName(),
												idMaster);
										val.put("_id"
												+ item.getClass()
														.getSimpleName(),
												JpdroidObjectMap.getFieldPk(
														item).getLong(item));
										values.add(val);
									} else {
										// Pode existir mais de uma coluna
										// foreinkey
										Field[] fieldForeingKeyList = getFieldsByForeignKey(
												item, entity.getClass()
														.getSimpleName());

										if (fieldForeingKeyList != null) {

											for (int u = 0; u < fieldForeingKeyList.length; u++) {

												fieldForeingKeyList[u]
														.setAccessible(true);
												fieldForeingKeyList[u].set(
														item, idMaster);
											}

										}
										persistRecursivo(item);
									}

								}
								if (relationClass != null
										&& relationClass.relationType().equals(
												RelationType.ManyToMany)) {
									persistRelationEntity(values,
											relationClass.joinTable(), "_id"
													+ entity.getClass()
															.getSimpleName()
													+ " = " + idMaster);
								}

							} else {
								Field[] fieldForeingKey = getFieldsByForeignKey(
										child, entity.getClass()
												.getSimpleName());

								if (fieldForeingKey != null) {

									for (int u = 0; u < fieldForeingKey.length; u++) {

										fieldForeingKey[u].setAccessible(true);
										fieldForeingKey[u].set(child,
												idMaster);
									}
								}
								persistRecursivo(child);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			idMaster = -1L;
			Log.e("Erro persistRecursivo()", e.getMessage());
		}
		return idMaster;

	}

	private void persistRelationEntity(List<ContentValues> values,
			String joinTable, String whereAll) {

		if (!values.isEmpty()) {

			List<String> chaves = new ArrayList<String>();
			for (ContentValues contentValues : values) {
				String where = "";
				for (Entry<String, Object> e : contentValues.valueSet()) {
					if (chaves.size() < 2) {
						chaves.add(e.getKey());
					}

					where += where.length() == 0 ? e.getKey() + " = "
							+ e.getValue().toString() : " and " + e.getKey()
							+ " = " + e.getValue().toString();

				}

				if (database.rawQuery(
						"SELECT * FROM " + joinTable + " WHERE " + where, null)
						.getCount() == 0) {
					insert(contentValues, joinTable);
				}

			}

			Cursor registros = database.rawQuery("SELECT * FROM " + joinTable
					+ " WHERE " + whereAll, null);
			registros.moveToFirst();
			do {

				int count = 0;
				for (ContentValues contentValues : values) {
					if (contentValues.getAsLong(chaves.get(0)) == registros
							.getLong(registros.getColumnIndex(chaves.get(0)))
							&& contentValues.getAsLong(chaves.get(1)) == registros
									.getLong(registros.getColumnIndex(chaves
											.get(1)))) {
						count++;
					}
				}
				if (count == 0) {
					String whereDelete = chaves.get(0)
							+ " = "
							+ String.valueOf(registros.getLong(registros
									.getColumnIndex(chaves.get(0))))
							+ " AND "
							+ chaves.get(1)
							+ " = "
							+ String.valueOf(registros.getLong(registros
									.getColumnIndex(chaves.get(1))));

					database.execSQL("DELETE FROM " + joinTable + " WHERE "
							+ whereDelete.toString());
				}

			} while (registros.moveToNext());
		} else {
			database.execSQL("DELETE FROM " + joinTable + " WHERE " + whereAll);
		}
	}

	private Cursor query(String entity) {
		Cursor cursor = database.rawQuery("select * from " + entity, null);
		return cursor;
	}

	/**
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return cursor
	 */
	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return database.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy);
	}

	/**
	 * @param sql
	 * @param selectionArgs
	 * @return cursor
	 */
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		Cursor retorno = database.rawQuery(sql, selectionArgs);
		retorno.moveToFirst();
		return retorno;
	}

	/**
	 * Executa script sql.
	 * 
	 * @param sql
	 */
	public void execSQL(String sql) {
		database.execSQL(sql);
	}

	/**
	 * Importa e executa script sql. <br>
	 * <br>
	 * Ex: <br>
	 * UPSERT Combustivel (nome,preco) VALUES("Gasolina",3.11); <br>
	 * INSERT OR REPLACE INTO combustivel (nome) values("Gasolina");
	 * 
	 * @param scriptUri
	 * <BR>
	 *            ScriptUri.Assets - Diretório Assets do projeto. <BR>
	 *            ScriptUri.SdCard - Diretório do cartão SD.
	 * @param fileName
	 * <BR>
	 *            Nome Arquivo.
	 */
	public int importSqlScript(ScriptPath scriptUri, String fileName) {
		try {
			BufferedReader reader = null;
			if (scriptUri == ScriptPath.Assets) {
				reader = new BufferedReader(new InputStreamReader(getContext()
						.getAssets().open(fileName)));

			} else {
				if (Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					File dir = Environment.getExternalStorageDirectory();
					File file = new File(dir, fileName);
					if (!file.exists()) {
						return 0;
					}
					reader = new BufferedReader(new InputStreamReader(
							new FileInputStream(file)));
				} else {
					throw new JpdroidException(
							"Nenhum cartão de memória foi localizado!");
				}
			}
			return importSqlScript(reader);
		} catch (Exception e) {
			Log.e("Erro Importar arquivo sql.", e.getMessage());
			return -1;
		}
	}

	public int importSqlScript(File file) {
		try {
			if (!file.exists()) {
				return 0;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			return importSqlScript(reader);
		} catch (Exception e) {
			Log.e("Erro Importar arquivo sql.", e.getMessage());
			return -1;
		}
	}

	public int importSqlScript(BufferedReader reader) {
		try {
			String readLine = "";

			StringBuffer script = new StringBuffer();
			while ((readLine = reader.readLine()) != null) {
				script.append(readLine);
			}

			String[] lines = script.toString().split(";");

			transaction.begin();

			for (String line : lines) {
				upsert(line);
			}

			transaction.commit();
		} catch (Exception e) {
			transaction.end();
			Log.e("Erro Importar arquivo sql.", e.getMessage());
			return -1;
		} finally {
			transaction.end();
		}
		return 1;

	}

	/**
	 * UPSERT / UPDATE OR INSERT <br>
	 * Método responsável por inserir novos registros ou atualizar registros
	 * existentes. <br>
	 * <br>
	 * O comando deve respeitar a seguinte sintaxe, lembrando que a sintaxe é
	 * case sensitive. <br>
	 * Ex:UPSERT NomeEntidade (Coluna1,Coluna2) VALUES(Valor1,Valor2); <br>
	 * <br>
	 * Requisito: possuir ao menos uma coluna do tipo unique. <br>
	 * Os espaços das strings devem ser preenchidos com o caractere '#'. <br>
	 * Exemplo: <br>
	 * UPSERT Cidade (_id,nome,id_Estado) VALUES(0,"Dionísio Cerqueira",6); <br>
	 * UPSERT Cidade (_id,nome,id_Estado) VALUES(0,"Dionísio#Cerqueira",6);
	 * 
	 * @param sql
	 * @throws Exception
	 */
	public void upsert(String sql) throws Exception {

		if (sql.contains("UPSERT")) {

			String comando[] = null;
			String colunas[] = null;
			String valores[] = null;
			String replace = null;
			String where = " WHERE 0 = 0 ";
			TreeMap<String, String> chaveValor = new TreeMap<String, String>();

			String query = "", sqlExec = "";

			comando = sql.split(" ");
			replace = comando[2].replace('(', ' ').replace(')', ' ');
			colunas = replace.split(",");

			replace = comando[3].substring(7).replace(')', ' ');
			valores = replace.split(",");

			if (colunas.length != valores.length) {
				throw new JpdroidException(
						"O número de colunas não corresponde ao número de valores!");
			}
			for (int i = 0; i < colunas.length; i++) {
				chaveValor.put(colunas[i].trim(), valores[i].trim());
			}

			Class<?> classe = Class.forName(entidades.get(comando[1].trim()
					.toUpperCase()));
			Field[] fields = classe.getDeclaredFields();
			query = "SELECT * FROM " + comando[1];

			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);

				if (column != null && column.unique()) {
					String columnName = "";
					if ("".equals(column.name())) {
						columnName = field.getName();
					} else {
						columnName = column.name();
					}
					if (chaveValor.containsKey(columnName)) {
						where += " and " + columnName + " = "
								+ chaveValor.get(columnName);
					} else {
						throw new JpdroidException(
								"Coluna do tipo unique não encontrada na tabela "
										+ comando[1] + ".");
					}
				}
			}
			if (where.equals(" WHERE 0 = 0 ")) {
				for (String col : colunas) {
					where += " and " + col.trim() + " = "
							+ chaveValor.get(col.trim());
				}
			}
			if (where.equals(" WHERE 0 = 0 ")) {
				throw new JpdroidException(
						"Coluna do tipo unique não encontrada no script. Colunas marcadas como unique são obrigatorias nos comandos UPSERT.");
			}

			where = where.replaceAll("#", " ");
			if (database.rawQuery(query + where, null).getCount() == 0) {
				sqlExec = sql.replaceAll("UPSERT", "INSERT INTO");
				sqlExec = sqlExec.replaceAll("#", " ");
				database.execSQL(sqlExec);
			} else {
				sqlExec = "UPDATE " + comando[1];
				for (int i = 0; i < colunas.length; i++) {
					if (i > 0) {
						sqlExec += ",";
					} else {
						sqlExec += " SET ";
					}
					sqlExec += colunas[i] + " = "
							+ chaveValor.get(colunas[i].trim());
				}
				where = where.replaceAll("#", " ");
				sqlExec += where;
				sqlExec = sqlExec.replaceAll("#", " ");
				database.execSQL(sqlExec);
			}

		} else {
			database.execSQL(sql);
		}

	}

	/**
	 * Compacta o banco Sqlite, o tamanho do arquivo do sqlite diminuirá.
	 */
	public void vacuum() {
		database.execSQL("VACUUM");

	}

	/**
	 * Indica se é a primeira execução do programa.
	 * 
	 * @return boolean
	 */
	public boolean isCreate() {
		return dbHelper.isCreate();
	}

}
