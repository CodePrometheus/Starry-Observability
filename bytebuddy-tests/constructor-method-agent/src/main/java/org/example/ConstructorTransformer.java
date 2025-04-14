package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

/**
 * 构造方法增强
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/15 11:27 PM
 */
public class ConstructorTransformer implements AgentBuilder.Transformer {

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        return builder.constructor(ElementMatchers.any())
                .intercept(SuperMethodCall.INSTANCE.andThen(
                        MethodDelegation.to(new ConstructorInterceptor())));
    }
}
