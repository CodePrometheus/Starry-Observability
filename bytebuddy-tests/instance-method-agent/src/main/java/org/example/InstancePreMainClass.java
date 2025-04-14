package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * premain 入口类, 用于java agent进行插桩
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/10 5:21 PM
 */
public class InstancePreMainClass {
    /**
     * java agent 入口, premain在main方法之前执行
     */
    public static void premain(String arg, Instrumentation instrumentation) {
        System.out.println("执行 premain");
        // 使用 Byte Buddy 包装的 agent常见处理逻辑(指定要拦截的对象, 以及拦截后的处理逻辑, 任何字节码操作工具都基本这个流程)
        AgentBuilder agentBuilder = new AgentBuilder.Default()
                // 忽略(不拦截)的类, 这里忽略 java自带类和byte buddy的类
                .ignore(ElementMatchers.nameStartsWith("java.")
                        .or(ElementMatchers.nameStartsWith("javax."))
                        .or(ElementMatchers.nameStartsWith("jdk."))
                        .or(ElementMatchers.nameStartsWith("sun."))
                        // 忽略byte buddy的类
                        .or(ElementMatchers.nameStartsWith("net.bytebuddy.")))
                // 拦截的类
                .type(isAnnotatedWith(nameStartsWith("org.example.Ash").and(nameEndsWith("Log"))))
                // 拦截的方法, 以及指定修改/增强的逻辑
                .transform(new InstanceTransformer())
                // 注册 回调方法监听器
                .with(new InstanceListener());
        // 注册到 Instrumentation
        agentBuilder.installOn(instrumentation);
    }
}
