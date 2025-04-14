package org.example;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * 用于修改/增强 {@link SomethingClass#sayWhat(String)} 静态方法
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/3 7:30 PM
 */
public class SomethingInterceptor06 {

    @RuntimeType
    public void sayWhatEnhance(
            // 静态方法对应的类class对象
            @Origin Class<?> clazz,
            // 静态方法不可访问 @This Object targetObj,
            @Origin Method targetMethod,
            @AllArguments Object[] targetMethodArgs,
            // 静态方法不可访问 @Super Object targetSuperObj,
            @SuperCall Callable<?> zuper) {
        // 原方法逻辑 System.out.println("what to Say, say: " + whatToSay);
        System.out.println("clazz = " + clazz);
        System.out.println("targetMethod.getName() = " + targetMethod.getName());
        System.out.println("Arrays.toString(targetMethodArgs) = " + Arrays.toString(targetMethodArgs));
        try {
            System.out.println("before sayWhat");
            // 调用目标方法
            zuper.call();
            System.out.println("after sayWhat");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

