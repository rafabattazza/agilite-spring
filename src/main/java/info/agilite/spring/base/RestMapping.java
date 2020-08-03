package info.agilite.spring.base;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Anotação auxiliar para facilitar a criação de RestControllers que retornam JSon, 
 * substitui o @RequestMapping + @RestController
 * @author Rafael
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RequestMapping(produces=MediaType.APPLICATION_JSON_VALUE)
@RestController
public @interface RestMapping {
 
 @AliasFor(annotation = RequestMapping.class, attribute = "value")
 String[] value();
 
}