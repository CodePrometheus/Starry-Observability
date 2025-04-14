package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

/**
 * premain入口, 在main之前执行
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/15 11:25 PM
 */
public class ConstructorPremain {
    public static void premain(String arg, Instrumentation instrumentation) {
        System.out.println("执行 premain");
        new AgentBuilder.Default()
                .type(ElementMatchers.named("org.example.Something"))
                .transform(new ConstructorTransformer())
                .installOn(instrumentation);
    }
}
