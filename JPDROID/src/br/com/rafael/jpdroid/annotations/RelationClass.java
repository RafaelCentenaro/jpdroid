package br.com.rafael.jpdroid.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.rafael.jpdroid.enums.RelationType;

/**
 * Identifica atributo de uma classe como classe relacionada.
 * 
 * @author Rafael Centenaro
 *
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RelationClass {
	/**
	 * Nome do atributo na classe.
	 * @return
	 */
	String fieldName();
	/**
	 * Nome coluna utilizada no relacionamento.
	 * @return
	 */
	String joinColumn();
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
