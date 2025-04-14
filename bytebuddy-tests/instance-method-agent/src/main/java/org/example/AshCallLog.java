package org.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识 需要记录方法调用信息 的方法, 后续 插桩逻辑会log输出 被调用的方法名, 方法参数, 方法执行耗时等信息
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/10 5:05 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface AshCallLog {
}
