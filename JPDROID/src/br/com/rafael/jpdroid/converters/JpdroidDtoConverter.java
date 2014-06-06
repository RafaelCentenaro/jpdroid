package br.com.rafael.jpdroid.converters;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import br.com.rafael.jpdroid.annotations.Dto;
import br.com.rafael.jpdroid.annotations.DtoField;

/**
 * JpdroidDtoConverter - Esta classe permite a conversão de um objeto do tipo entity para um DTO e vice-versa. <br/>
 * Ex: <br/>
 * Pessoa pessoaOrigem = jpdroid.getObjects(Pessoa.class,true).get(0); <br/>
 * PessoaDTO pessoaDTO = JpdroidDtoConverter.Convert(pessoaOrigem, PessoaDTO.class); <br/>
 * Pessoa pessoa = JpdroidDtoConverter.Convert(pessoaDTO, Pessoa.class);
 * 
 * @author Rafael
 */
public class JpdroidDtoConverter {

	public static <T, E> List<T> convert(List<E> orig, Class<T> dest) {

		List<T> retorno = new ArrayList<T>();

		for (E item : orig) {

			retorno.add(convert(item, dest));

		}

		return retorno;

	}

	public static <T> T convert(Object orig, Class<T> dest) {

		T classConvert = null;

		try {

			if (orig.getClass().getSuperclass().getAnnotation(Dto.class) != null) {

				classConvert = convert(orig.getClass().getSuperclass(), dest);

			} else {

				classConvert = dest.newInstance();

			}

			Class<?> dto = null;

			Class<?> origem = orig.getClass();

			if (orig.getClass().getAnnotation(Dto.class) != null) {

				dto = orig.getClass();

			} else {

				dto = dest;

			}

			Field[] fields = dto.getDeclaredFields();

			for (Field field : fields) {

				DtoField dtoField = field.getAnnotation(DtoField.class);

				if (dtoField != null) {

					Field fieldDestino = null;

					Field fieldOrigem = null;

					if (dto.equals(dest)) {

						fieldDestino = field;

					} else {

						fieldDestino = dest.getDeclaredField(field.getName());

					}

					fieldDestino.setAccessible(true);

					fieldOrigem = origem.getDeclaredField(field.getName());

					fieldOrigem.setAccessible(true);

					Class<? extends Object> ob = fieldOrigem.getType();

					if (ob.isAssignableFrom(List.class)) {

						ParameterizedType fieldGenericType = (ParameterizedType) fieldDestino.getGenericType();

						Class<?> fieldType = (Class<?>) fieldGenericType.getActualTypeArguments()[0];

						Object value = fieldOrigem.get(orig);
						if (value != null) {

							List<Object> valores = new ArrayList<Object>();

							for (Object item : ((List<?>) value)) {

								valores.add(convert(item, fieldType));

							}

							fieldDestino.set(classConvert, valores);
						}

					} else {

						Object value = fieldOrigem.get(orig);
						if (value != null) {
							Dto destino = value.getClass().getAnnotation(Dto.class);

							if (destino != null) {

								fieldDestino.set(classConvert, convert(value, value.getClass()));

							} else {

								fieldDestino.set(classConvert, fieldOrigem.get(orig));

							}
						}

					}

				}

			}

			convertAncestor(orig, dto, classConvert);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return classConvert;

	}

	private static void convertAncestor(Object orig, Class<?> dto, Object dest) throws Exception {

		Class<?> dtoAnc = dto.getSuperclass();

		if (dtoAnc.equals(Object.class)) {

			return;

		}

		Class<?> classOrig = null;

		Class<?> classDest = null;

		if (!orig.getClass().getSuperclass().equals(Object.class)) {

			classOrig = orig.getClass().getSuperclass();

		} else {

			classOrig = orig.getClass();

		}

		if (!dest.getClass().getSuperclass().equals(Object.class)) {

			classDest = dest.getClass().getSuperclass();

		} else {

			classDest = dest.getClass();

		}

		Field[] fields = dtoAnc.getDeclaredFields();

		for (Field field : fields) {

			DtoField dtoField = field.getAnnotation(DtoField.class);

			if (dtoField != null) {

				Field fieldDestino = null;

				Field fieldOrigem = null;

				if (dto.equals(dest)) {

					fieldDestino = field;

				} else {

					fieldDestino = classDest.getDeclaredField(field.getName());

				}

				fieldDestino.setAccessible(true);

				fieldOrigem = classOrig.getDeclaredField(field.getName());

				fieldOrigem.setAccessible(true);

				Class<? extends Object> ob = fieldOrigem.getType();

				if (ob.isAssignableFrom(List.class)) {

					ParameterizedType fieldGenericType = (ParameterizedType) fieldDestino.getGenericType();

					Class<?> fieldType = (Class<?>) fieldGenericType.getActualTypeArguments()[0];

					Object value = fieldOrigem.get(orig);
					if (value != null) {
						List<Object> valores = new ArrayList<Object>();

						for (Object item : ((List<?>) value)) {

							valores.add(convert(item, fieldType));

						}

						fieldDestino.set(dest, valores);
					}

				} else {

					Object value = fieldOrigem.get(orig);
					if (value != null) {
						Dto destino = value.getClass().getAnnotation(Dto.class);

						if (destino != null) {

							fieldDestino.set(dest, convert(value, value.getClass()));

						} else {

							fieldDestino.set(dest, fieldOrigem.get(orig));

						}
					}

				}

			}

		}

	}

}