package br.com.rafael.jpdroid.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifica atributo de uma classe como chave estrangeira.
 * 
 * @author Rafael Centenaro
 *
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)

public @interface ForeignKey {
	/**
	 * Entidade relacionada.
	 * @return
	 */
	String  joinEntity();
	/**
	 * Coluna chave primária da entidade relacionada.
	 * @return
	 */
	String  joinPrimaryKey();
	/**
	 * Indica se deve excluir este registro quando a entidade relacionada for deletada. 
	 * @return
	 */
	boolean  deleteCascade() default false;
}
