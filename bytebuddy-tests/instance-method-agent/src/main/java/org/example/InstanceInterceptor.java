package org.example;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * 实例方法 拦截器, 这里配合 {@link AshCallLog} 注解使用,
 * 输出被调用的方法的方法名, 参数, 返回值, 执行时间
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/10 8:30 PM
 */
public class InstanceInterceptor {

    @RuntimeType
    public Object instanceMethodIntercept(
            @Origin Method targetMethod,
            @AllArguments Object[] targetMethodArgs,
            @SuperCall Callable<?> zuper) {
        System.out.println("「增强逻辑」targetMethod.getName() = " + targetMethod.getName());
        System.out.println("「增强逻辑」Arrays.toString(targetMethodArgs) = " + Arrays.toString(targetMethodArgs));
        long callStartTime = System.currentTimeMillis();
        Object returnValue = null;
        try {
            returnValue = zuper.call();
            System.out.println("「增强逻辑」returnValue = " + returnValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("「增强逻辑」callTime: " + (System.currentTimeMillis() - callStartTime));
        }
        return returnValue;
    }
}
