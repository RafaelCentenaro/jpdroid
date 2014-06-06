package br.com.rafael.jpdroid.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifica classe como entidade no banco de dados.
 * 
 * @author Rafael Centenaro
 *
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity
{
}