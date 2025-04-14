package org.example;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * 用于修改/增强 {@link SomethingClass#selectUserName(Long)} 方法
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/2 9:21 PM
 */
public class SomethingInterceptor04 {

    /**
     * 修改/增强 {@link SomethingClass#selectUserName(Long)} 方法 <br/>
     * <a href="http://bytebuddy.net/#/tutorial-cn" target="_blank">Byte Buddy官方教程文档</a>
     * <p>
     *  {@code @Morph}: 这个注解的工作方式与{@code @SuperCall}注解非常相似。然而，使用这个注解允许指定用于调用超类方法参数。
     *  注意， 仅当你需要调用具有与原始调用不同参数的超类方法时，才应该使用此注解，因为使用@Morph注解需要对所有参数装箱和拆箱。
     *  如果过你想调用一个特定的超类方法， 请考虑使用@Super注解来创建类型安全的代理。在这个注解被使用之前，需要显式地安装和注册，类似于@Pipe注解。
     * </p>
     */
    @RuntimeType
    public Object otherMethodName(
            // 目标方法的参数
            @AllArguments Object[] targetMethodArgs,
            // @SuperCall Callable<?> zuper
            // 用于调用目标方法 (这里使用@Morph, 而不是@SuperCall, 才能修改入参)
            @Morph MyCallable zuper
    ) {
        // 原方法逻辑 return String.valueOf(userId);
        Object result = null;
        try {
            // 修改参数
            if(null != targetMethodArgs && targetMethodArgs.length > 0) {
                targetMethodArgs[0] = (long) targetMethodArgs[0] + 1;
            }
            // @SuperCall 不接受参数 result = zuper.call();
            // 调用目标方法
            result = zuper.apply(targetMethodArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
