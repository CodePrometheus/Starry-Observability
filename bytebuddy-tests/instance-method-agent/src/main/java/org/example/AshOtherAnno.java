package org.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模拟其他 注解, 无实际作用
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/10 5:06 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface AshOtherAnno {
}
