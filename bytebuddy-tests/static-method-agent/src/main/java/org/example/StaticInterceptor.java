package org.example;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 静态方法拦截
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/15 10:03 PM
 */
public class StaticInterceptor {

    @RuntimeType
    public Object staticMethodIntercept(
            @Origin Class<?> clazz,
            @Origin Method targetMethod,
            @AllArguments Object[] targetMethodArgs,
            @SuperCall Callable<?> zuper) {
        System.out.println("「增强逻辑」targetMethod.getName() = " + targetMethod.getName());
        long callStartTime = System.currentTimeMillis();
        Object returnValue = null;
        try {
            returnValue = zuper.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("「增强逻辑」callTime: " + (System.currentTimeMillis() - callStartTime));
        }
        return returnValue;
    }
}
