package org.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * 针对 实例 方法进行 修改/增强的 类转换器 <br/>
 * 某个类被 {@link AgentBuilder#type(ElementMatcher)}匹配, 将要被类加载时, 进入transform方法
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/10 6:06 PM
 */
public class InstanceTransformer implements AgentBuilder.Transformer {
    /**
     * transform 转化逻辑
     *
     * @param builder          之前Byte Buddy 增强修改类时的中间产物Builder(比如 {@link ByteBuddy#subclass(Class)}的返回值就是) The dynamic builder to transform.
     * @param typeDescription  将被加载的类对应的类信息 The description of the type currently being instrumented.
     * @param classLoader      The class loader of the instrumented class. Might be {@code null} to represent the bootstrap class loader.
     * @param module           The class's module or {@code null} if the current VM does not support modules.
     * @param protectionDomain The protection domain of the transformed type.
     * @return : A transformed version of the supplied builder.
     */
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                            TypeDescription typeDescription,
                                            ClassLoader classLoader,
                                            JavaModule module,
                                            ProtectionDomain protectionDomain) {
        return builder.method(not(isStatic()).and(isAnnotatedWith(nameStartsWith("org.example.Ash").and(nameEndsWith("Log")))))
                .intercept(MethodDelegation.to(new InstanceInterceptor()));
    }
}
