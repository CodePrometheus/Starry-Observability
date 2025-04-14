package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.instrument.Instrumentation;

/**
 * premain 入口, 进行java agent 插桩
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/15 9:56 PM
 */
public class StaticPremain {

    /**
     * java agent 入口, premain 在main方法之前执行
     */
    public static void premain(String arg, Instrumentation instrumentation) {
        System.out.println("执行 premain");
        new AgentBuilder.Default()
                // 使用我们自定义的匹配器指定拦截的类
                .type(getMatcher())
                .transform(new StaticTransformer())
                .installOn(instrumentation);
    }

    public static ElementMatcher<? super TypeDescription> getMatcher() {
        // 相同效果, 类名匹配 return ElementMatchers.named("org.example.StaticUtils");
        return new ElementMatcher.Junction.AbstractBase<NamedElement>() {
            @Override
            public boolean matches(NamedElement target) {
                // 当类名匹配时, 拦截
                return "org.example.StaticUtils".equals(target.getActualName());
            }
        };
    }
}
