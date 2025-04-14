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
public class SomethingInterceptor03 {

    /**
     * 修改/增强 {@link SomethingClass#selectUserName(Long)} 方法 <br/>
     * 和 {@link SomethingInterceptor01#selectUserName(Long)} 以及 {@link SomethingInterceptor02#selectUserName(Long)} 大不相同，
     * 不需要和原目标方法保持相同的方法签名 <br/>
     * 为了克服需要一致方法签名的限制，Byte Buddy 允许给方法和方法参数添加@RuntimeType注解， 它指示 Byte Buddy 终止严格类型检查以支持运行时类型转换 <br/>
     */
    @RuntimeType
    public Object otherMethodName(
            // 表示被拦截的目标对象, 只有拦截实例方法时可用
            @This Object targetObj,
            // 表示被拦截的目标方法, 只有拦截实例方法或静态方法时可用
            @Origin Method targetMethod,
            // 目标方法的参数
            @AllArguments Object[] targetMethodArgs,
            // 表示被拦截的目标对象, 只有拦截实例方法时可用 (可用来调用目标类的super方法)
            @Super Object targetSuperObj,
            // 若确定超类(父类), 也可以用具体超类(父类)接收
            // @Super SomethingClass targetSuperObj,
            // 用于调用目标方法
            @SuperCall Callable<?> zuper
    ) {
        // 原方法逻辑 return String.valueOf(userId);
        // targetObj = com.example.AshiamdTest16@79e4c792
        System.out.println("targetObj = " + targetObj);
        // targetMethod.getName() = selectUserName
        System.out.println("targetMethod.getName() = " + targetMethod.getName());
        // Arrays.toString(targetMethodArgs) = [3]
        System.out.println("Arrays.toString(targetMethodArgs) = " + Arrays.toString(targetMethodArgs));
        // targetSuperObj = com.example.AshiamdTest16@79e4c792
        System.out.println("targetSuperObj = " + targetSuperObj);
        Object result = null;
        try {
            // 调用目标方法
            result = zuper.call();
            // 直接通过反射的方式调用原方法, 会导致无限递归进入当前增强的逻辑
            // result = targetMethod.invoke(targetObj,targetMethodArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
