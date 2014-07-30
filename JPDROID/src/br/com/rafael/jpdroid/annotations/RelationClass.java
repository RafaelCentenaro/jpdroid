package br.com.rafael.jpdroid.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.rafael.jpdroid.enums.RelationType;

/**
 * Identifica atributo como classe relacionada.
 * 
 * @author Rafael Centenaro
 *
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RelationClass {
	/**
	 * Nome coluna utilizada no relacionamento.
	 * @return
	 */
	String joinColumn() default "";
	
	String joinTable() default "";
	/**
	 * Tipo de relacionamento.
	 * @return
	 */
	RelationType relationType();
	/**
	 * Identifica que este relacionamento não deve ser persistido.
	 */
	
	boolean Transient() default false;
}
