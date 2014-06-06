package br.com.rafael.jpdroid.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifica atributo de uma classe como coluna.
 * 
 * @author Rafael Centenaro
 *
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DtoField
{

}