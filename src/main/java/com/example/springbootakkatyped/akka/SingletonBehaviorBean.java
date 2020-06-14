package com.example.springbootakkatyped.akka;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Bean
@Lazy
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SingletonBehaviorBean {

    @AliasFor(value = "name", annotation = Bean.class)
    String[] value() default {};

    /**
     * The name of this bean, or if several names, a primary bean name plus aliases.
     * <p>If left unspecified, the name of the bean is the name of the annotated method.
     * If specified, the method name is ignored.
     * <p>The bean name and aliases may also be configured via the {@link #value}
     * attribute if no other attributes are declared.
     * @see #value
     */
    @AliasFor(value = "value", annotation = Bean.class)
    String[] name() default {};

}
