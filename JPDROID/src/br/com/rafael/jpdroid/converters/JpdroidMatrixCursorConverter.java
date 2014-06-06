package br.com.rafael.jpdroid.converters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.database.MatrixCursor;
import br.com.rafael.jpdroid.annotations.Column;
import br.com.rafael.jpdroid.annotations.Ignorable;
import static br.com.rafael.jpdroid.core.JpdroidObjectMap.*;

/**
 * Classe responsável pela conversão objetos para um MatrixCursor.
 * 
 * @author Rafael Centenaro
 */
public class JpdroidMatrixCursorConverter {

	public static MatrixCursor toMatrixCursor(Object entity) {
		return toMatrixCursor(entity, true);
	}

	/**
	 * Retorna um MatrixCursor gerado com base na entidade passada por parâmetro.
	 * 
	 * @param entity
	 * @return MatrixCursor <BR>
	 *         Ex:SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, R.layout.activity_simple_list,
	 *         matrixCursor, columns, to, 0);
	 */
	@SuppressWarnings("unchecked")
	public static MatrixCursor toMatrixCursor(Object entity, boolean onlyColumn) {
		MatrixCursor matrixCursor = null;
		try {
			String[] columns;

			List<Object> row = new ArrayList<Object>();

			if (entity instanceof List) {
				if (onlyColumn) {
					columns = getColumns(((List<?>) entity).get(0).getClass());
				} else {
					columns = getColumns(((List<?>) entity).get(0).getClass(),Column.class,Ignorable.class);
				}
				matrixCursor = new MatrixCursor(columns);

				for (Object item : ((List<?>) entity)) {
					row = new ArrayList<Object>();
					Field[] fields = null;
					if (onlyColumn) {
						fields = getFieldsByAnnotation(item, Column.class);
					} else {
						fields = getFieldsByAnnotation(item, Column.class, Ignorable.class);
					}
					for (Field field : fields) {
						field.setAccessible(true);
						row.add(field.get(item));

					}
					matrixCursor.addRow(row);
				}
			} else {
				columns = getColumns(entity.getClass());
				matrixCursor = new MatrixCursor(columns);

				Field[] fields = null;
				if (onlyColumn) {
					fields = getFieldsByAnnotation(entity, Column.class);
				} else {
					fields = getFieldsByAnnotation(entity, Column.class, Ignorable.class);
				}
				for (Field field : fields) {
					field.setAccessible(true);
					row.add(field.get(entity));

				}
				matrixCursor.addRow(row);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return matrixCursor;
	}
}
