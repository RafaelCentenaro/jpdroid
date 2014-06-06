package br.com.rafael.jpdroid.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifica atributo de uma classe como chave-primária.
 * 
 * @author Rafael Centenaro
 *
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey
{
	boolean autoGenerate() default true;
}
