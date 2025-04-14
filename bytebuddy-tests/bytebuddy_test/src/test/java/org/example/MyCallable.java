package org.example;

/**
 * 用于后续接收目标方法的参数, 以及中转返回值的函数式接口 <br/>
 * 入参必须是 Object[], 返回值必须是 Object
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/3 8:38 AM
 */
@FunctionalInterface
public interface MyCallable {
    // java.lang.IllegalArgumentException: public abstract java.lang.String org.example.MyCallable.apply(java.lang.Object[]) does not return an Object-type
    // String apply(Object[] args);

    // java: incompatible types: java.lang.Object[] cannot be converted to java.lang.Long[]
    // Object apply(Long[] args);

    Object apply(Object[] args);
}
