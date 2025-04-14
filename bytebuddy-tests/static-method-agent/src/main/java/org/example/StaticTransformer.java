package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

/**
 * 针对静态方法进行增强<br/>
 * 某个类被 {@link AgentBuilder#type(ElementMatcher)}匹配, 将要被类加载时, 进入transform方法
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/15 10:04 PM
 */
public class StaticTransformer implements AgentBuilder.Transformer {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        return builder.method(ElementMatchers.isStatic())
                .intercept(MethodDelegation.to(new StaticInterceptor()));
    }
}
