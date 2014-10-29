package br.com.rafael.jpdroid.core;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.graphics.Bitmap;
import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.ForeignKey;
import br.com.rafael.jpdroid.annotations.DefaultOrder;
import br.com.rafael.jpdroid.annotations.PrimaryKey;
import br.com.rafael.jpdroid.annotations.RelationClass;
import br.com.rafael.jpdroid.enums.RelationType;

/**
 * Classe responsável por obter colunas e valores de um objeto.
 * 
 * @author Rafael Centenaro
 */
public class JpdroidObjectMap {

	/**
	 * Retorna as chaves estrangeiras identificadas pela anotação ForeignKey
	 * 
	 * @param item
	 * @param referenceEntity
	 * @return Field[]
	 */
	public static Field[] getFieldsByForeignKey(Object item, String referenceEntity) {
		List<Field> fields = new ArrayList<Field>();

		Field[] declaredFields = item.getClass().getDeclaredFields();
		for (Field field : declaredFields) {

			ForeignKey annotation = field.getAnnotation(ForeignKey.class);

			if (annotation != null && annotation.joinEntity().getSimpleName().equals(referenceEntity)) {
				fields.add(field);
			}

		}
		return fields.toArray(new Field[fields.size()]);
	}

	/**
	 * Retorna os campos identificados pela anotação RelationClass.
	 * 
	 * @param entity
	 * @return Field[]
	 */
	public static Field[] getFieldsByRelationClass(Object entity, RelationType relationType) {
		List<Field> fields = new ArrayList<Field>();

		Field[] declaredFields = entity.getClass().getDeclaredFields();
		for (Field field : declaredFields) {

			RelationClass annotation = field.getAnnotation(RelationClass.class);
			if (annotation != null && !annotation.Transient()) {
				if (relationType == RelationType.OneToMany) {
					if ((annotation.relationType() == relationType || annotation.relationType() == RelationType.OneToOne)) {
						fields.add(field);
					}
				} else {
					if ((annotation.relationType() == relationType || annotation.relationType() == RelationType.ManyToMany)) {
						fields.add(field);
					}
				}
			}

		}
		return fields.toArray(new Field[fields.size()]);
	}

	/**
	 * Retorna o campo identificado pela anotação passada por parâmetro.
	 * 
	 * @param entity
	 * @param annotationClass
	 * @return Field
	 */
	public static Field getFieldByAnnotation(Object entity, Class<? extends Annotation> annotationClass) {
		Field[] fields = entity.getClass().getDeclaredFields();
		for (Field field : fields) {

			Annotation annotation = field.getAnnotation(annotationClass);

			if (annotation != null) {
				return field;
			}

		}
		return null;
	}
	
	/**
	 * Retorna os campos configurados pela anotação Order.
	 * @param entity
	 * @return
	 */
	public static String getDefaultOrderBy(Class<?> entity){
		
		String orderBy = "";
		
		Field[] fieldOrderBy = entity.getDeclaredFields();
		for (Field field : fieldOrderBy) {
			DefaultOrder order = field.getAnnotation(DefaultOrder.class);
			if (order != null) {
				if (orderBy.length() == 0) {
					orderBy = field.getName() + " " + order.order();
				} else {
					orderBy += ", "+field.getName() + " " + order.order();
				}
			}
		}
		return orderBy;
	}

	/**
	 * Retorna um array de campos identificados pela anotação passada por parâmetro.
	 * 
	 * @param entity
	 * @param annotationClass
	 * @return Field[]
	 */
	public static Field[] getFieldsByAnnotation(Object entity, Class<? extends Annotation>... annotationClass) {
		List<Field> fields = new ArrayList<Field>();
		for (Class<? extends Annotation> class1 : annotationClass) {
			Field[] fieldChild = getFieldsByAnnotation(entity, class1);
			for (Field field : fieldChild) {
				fields.add(field);
			}
		}
		return fields.toArray(new Field[fields.size()]);
	}

	public static Field[] getFieldsByAnnotation(Object entity, Class<? extends Annotation> annotationClass) {
		List<Field> fields = new ArrayList<Field>();

		Field[] declaredFields = entity.getClass().getDeclaredFields();
		for (Field field : declaredFields) {

			Annotation annotation = field.getAnnotation(annotationClass);

			if (annotation != null) {
				fields.add(field);
			}

		}
		return fields.toArray(new Field[fields.size()]);
	}

	/**
	 * Retorna um array de String contendo os campos do objeto.
	 * 
	 * @param entity
	 * @return String[]
	 */
	@SuppressWarnings("unchecked")
	public static String[] getColumns(Class<?> entity) {
		return getColumns(entity, Column.class);
	}

	public static String[] getColumns(Class<?> entity, Class<? extends Annotation>... annotationClass) {
		List<String> colunas = new ArrayList<String>();
		Field[] fields = entity.getDeclaredFields();
		String columnName = null;
		for (Class<? extends Annotation> class1 : annotationClass) {

			for (Field field : fields) {
				Annotation annotationColumn = field.getAnnotation(class1);
				if (annotationColumn != null) {
					if (annotationColumn instanceof Column) {
						if ("".equals(((Column) annotationColumn).name())) {
							columnName = field.getName();
						} else {
							columnName = ((Column) annotationColumn).name();
						}
					} else {
						columnName = field.getName();
					}
					colunas.add(columnName);
				}
			}
		}
		return colunas.toArray(new String[colunas.size()]);
	}

	/**
	 * Retorna o ContentValues referente ao objeto.
	 * 
	 * @param object - Instância da entidade.
	 * @return - ContentValues
	 */
	@SuppressLint("SimpleDateFormat")
  public static ContentValues getContentvalues(Object object) {
		Class<?> entity = object.getClass();

		ContentValues values = new ContentValues();
		String columnName = null;

		Field[] fields = entity.getDeclaredFields();
		for (Field field : fields) {

			PrimaryKey annotationId = field.getAnnotation(PrimaryKey.class);
			Column annotationColumn = field.getAnnotation(Column.class);
			ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

	
			if (annotationId != null) {
				continue;
			}
			if (annotationColumn != null) {
				if ("".equals(annotationColumn.name())) {
					columnName = field.getName();
				} else {
					columnName = annotationColumn.name();
				}

				try {
					field.setAccessible(true);
					if (foreignKey != null && String.valueOf(field.get(object)).equalsIgnoreCase("0") ) {
						
						continue;
					}
					if (("Byte[]".equalsIgnoreCase(field.getType().getSimpleName()))
					    || ("Bitmap".equalsIgnoreCase(field.getType().getSimpleName()))) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						Bitmap foto = null;

						Object valor = field.get(object);
						if (valor != null) {
							foto = (Bitmap) valor;
						}

						if (foto != null) {
							foto.compress(Bitmap.CompressFormat.PNG, 100, baos);
							byte[] photo = baos.toByteArray();

							values.put(columnName, photo);
						}
					} 
					else if( ("java.util.Date".equals(field.getType().getName())) || ("java.sql.Date".equals(field.getName()))
					    || ("Calendar".equals(field.getType().getSimpleName()))){
						values.put(columnName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(field.get(object)));
					}
					else {
						values.put(columnName, String.valueOf(field.get(object)));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return values;
	}

	public static List<ForeignKey> getForeingKeys(Class<?> entity) {
		List<ForeignKey> fk = new ArrayList<ForeignKey>();

		Field[] declaredFields = entity.getDeclaredFields();
		for (Field field : declaredFields) {

			ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

			if (foreignKey != null) {
				fk.add(foreignKey);
			}

		}
		return fk;
	}

	public static Field getFieldPk(Object entity) {
		Field[] declaredFields = entity.getClass().getDeclaredFields();
		for (Field field : declaredFields) {

			PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);

			if (primaryKey != null) {
				field.setAccessible(true);
				return field;
			}

		}
		return null;

	}
}
