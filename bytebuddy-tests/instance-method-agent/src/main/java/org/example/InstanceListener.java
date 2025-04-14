package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

/**
 * 注册监听器, 在 类进行transform过程中, 会回调下面这些hook方法<br/>
 * A listener that is informed about events that occur during an instrumentation process.
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/10 8:42 PM
 */
public class InstanceListener implements AgentBuilder.Listener {
    /**
     * 当某个类将要被加载时, 就会回调该方法
     *
     * @param typeName    The binary name of the instrumented type.
     * @param classLoader The class loader which is loading this type or {@code null} if loaded by the boots loader.
     * @param module      The instrumented type's module or {@code null} if the current VM does not support modules.
     * @param loaded      {@code true} if the type is already loaded.
     */
    @Override
    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        System.out.println("【onDiscovery】typeName: " + typeName);
    }

    /**
     * 对某个类完成了transform之后会回调
     *
     * @param typeDescription The type that is being transformed.
     * @param classLoader     The class loader which is loading this type or {@code null} if loaded by the boots loader.
     * @param module          The transformed type's module or {@code null} if the current VM does not support modules.
     * @param loaded          {@code true} if the type is already loaded.
     * @param dynamicType     The dynamic type that was created.
     */
    @Override
    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
        System.out.println("【onTransformation】typeDescription: " + typeDescription);
    }

    /**
     * 当某个类将要被加载, 但配置了被byte buddy忽略(或本身没有配置被拦截), 则执行该方法
     *
     * @param typeDescription The type being ignored for transformation.
     * @param classLoader     The class loader which is loading this type or {@code null} if loaded by the boots loader.
     * @param module          The ignored type's module or {@code null} if the current VM does not support modules.
     * @param loaded          {@code true} if the type is already loaded.
     */
    @Override
    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
        System.out.println("【onIgnored】 typeDescription: " + typeDescription);
    }

    /**
     * 当 Byte Buddy 在 transform 过程中 发生异常, 则执行该方法
     *
     * @param typeName    The binary name of the instrumented type.
     * @param classLoader The class loader which is loading this type or {@code null} if loaded by the boots loader.
     * @param module      The instrumented type's module or {@code null} if the current VM does not support modules.
     * @param loaded      {@code true} if the type is already loaded.
     * @param throwable   The occurred error.
     */
    @Override
    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
        System.out.println("【onError】 typeName: " + typeName);
    }

    /**
     * 某个类处理结束后(transform/ignore/error都算), 回调该方法
     *
     * @param typeName    The binary name of the instrumented type.
     * @param classLoader The class loader which is loading this type or {@code null} if loaded by the boots loader.
     * @param module      The instrumented type's module or {@code null} if the current VM does not support modules.
     * @param loaded      {@code true} if the type is already loaded.
     */
    @Override
    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        System.out.println("【onComplet】 typeName: " + typeName);
    }
}
