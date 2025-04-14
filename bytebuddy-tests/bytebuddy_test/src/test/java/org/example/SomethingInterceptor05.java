package org.example;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * 用于增强 {@link SomethingClass#SomethingClass()} 构造方法
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/3 11:30 AM
 */
public class SomethingInterceptor05 {

    @RuntimeType
    public void constructEnhance(
            //  表示被拦截的目标对象, 在构造方法中同样是可用的(也是实例方法)
            @This Object targetObj) {
        // constructEnhance() , com.example.AshiamdTest18@10163d6
        System.out.println("constructEnhance() , " + targetObj);
    }
}
