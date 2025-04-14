package org.example;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.*;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * <p>
 * 使用 Byte Buddy 生成类字节码, 用例介绍:
 *   <ol>
 *     <li>{@link ByteBuddyCreateClassTest#test01()}: 生成Object(jdk自带类)的子类, 不指定任何特定参数</li>
 *     <li>{@link ByteBuddyCreateClassTest#test02()}: 生成非jdk自带类的子类, 不指定任何特定参数</li>
 *     <li>{@link ByteBuddyCreateClassTest#test03()}: 指定父类为ArrayList, 使用官方教程建议的命名策略NamingStrategy.SuffixingRandom</li>
 *     <li>{@link ByteBuddyCreateClassTest#test04()}: 父类非jdk自带类, 指定命名策略和具体类名</li>
 *     <li>{@link ByteBuddyCreateClassTest#test05()}: 尝试指定不合法的类名, 由于Byte Buddy本身带有字节码校验逻辑, 会提前报错</li>
 *     <li>{@link ByteBuddyCreateClassTest#test06()}: 指定不合法类名, 关闭Byte Buddy自带的字节码校验逻辑(该校验虽耗费性能, 但一般对项目影响不大, 也不建议关闭)</li>
 *     <li>{@link ByteBuddyCreateClassTest#test07()}: 将生成的字节码, 注入一个jar包中</li>
 *     <li>{@link ByteBuddyCreateClassTest#test08()}: 对实例方法插桩(stub), 修改原本的toString方法逻辑</li>
 *     <li>{@link ByteBuddyCreateClassTest#test09()}: 通过subclass继承类, 重写父类方法</li>
 *     <li>{@link ByteBuddyCreateClassTest#test10()}: rebase变基, 原方法保留变为private且被改名(增加$original${随机字符串}后缀), 原方法名内逻辑替换成我们指定的逻辑</li>
 *     <li>{@link ByteBuddyCreateClassTest#test11()}: redefine重定义, 重写指定的方法, 原方法逻辑不保留(被我们指定的逻辑覆盖掉)</li>
 *     <li>{@link ByteBuddyCreateClassTest#test12()}: redefine基础上, 增加新方法</li>
 *     <li>{@link ByteBuddyCreateClassTest#test13()}: 增加新成员变量, 以及生成对应的getter, setter方法</li>
 *     <li>{@link ByteBuddyCreateClassTest#test14()}: 将拦截的方法委托给相同方法签名的静态方法进行修改/增强</li>
 *     <li>{@link ByteBuddyCreateClassTest#test15()}: 将拦截的方法委托给相同方法签名的实例方法进行修改/增强</li>
 *     <li>{@link ByteBuddyCreateClassTest#test16()}: 将将拦截的方法委托给自定义方法进行修改/增强</li>
 *     <li>{@link ByteBuddyCreateClassTest#test17()}: 通过@Morph动态修改方法入参</li>
 *     <li>{@link ByteBuddyCreateClassTest#test18()}: 对构造方法插桩</li>
 *     <li>{@link ByteBuddyCreateClassTest#test19()}: 对静态方法插桩</li>
 *     <li>{@link ByteBuddyCreateClassTest#test20()}: 默认类加载策略`WRAPPER`, 不保存`.class`文件到本地, 重复加载类</li>
 *     <li>{@link ByteBuddyCreateClassTest#test21()}: 默认类加载策略`WRAPPER`,保存`.class`文件到本地, 之后加载类</li>
 *     <li>{@link ByteBuddyCreateClassTest#test22()}: 类加载策略`CHILD_FIRST`，保存`.class`文件到本地，之后重复加载类</li>
 *     <li>{@link ByteBuddyCreateClassTest#test23()}: redefine后，配合`CHILD_FIRST`加载类</li>
 *     <li>{@link ByteBuddyCreateClassTest#test24()}: 从指定 “jar包”, “文件目录”, “系统类加载器” 加载指定类</li>
 *     <li>{@link ByteBuddyCreateClassTest#test25()}: 清空指定类的所有方法的方法体(包含超类方法)</li>
 *     <li>{@link ByteBuddyCreateClassTest#test26()}: 清空指定类的当前类声明的所有方法的方法体(不包含超类方法)</li>
 *   </ol>
 * </p>
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/11/26 11:53 PM
 */
public class ByteBuddyCreateClassTest {

    /**
     * <p>(1) 不指定任何特别的参数, 只声明为Object的子类 </p>
     * <p>
     * <a href="http://bytebuddy.net/#/tutorial-cn">官方教程</a>已经说明了没有显示命名会发生什么: <br/>
     * <pre>
     * 如果没有显式的命名会发生什么？ Byte Buddy 遵循约定优于配置原则， 并且提供我们发现的便利的默认值。
     * 至于类的名称，Byte Buddy 的默认配置提供了一个NamingStrategy（命名策略）， 它可以根据动态类的超类名称随机生成一个名称。
     * 此外，定义的类名中的包和超类相同的话，直接父类的包私有方法对动态类就是可见的。 例如，如果你子类化一个名为example.Foo的类，
     * 生成的类的名称就像example.Foo$$ByteBuddy$$1376491271，其中数字序列是随机的。
     * 这条规则的一个例外情况是：子类化的类型来自Object所在的包java.lang。 Java 的安全模型不允许自定义的类在这个命名空间。
     * 因此，在默认的命名策略中，这种类型以net.bytebuddy.renamed前缀命名。
     * </pre>
     * </p>
     * <p>
     * 根据官方教程可以看出来, 生成的新类默认命名策略即:
     *  <ol>
     *    <li>父类是jdk自带类: {超类名}$ByteBuddy${随机字符串}</li>
     *    <li>父类非jdk自带类: net.bytebuddy.renamed.{超类名}$ByteBuddy${随机字符串}</li>
     *  </ol>
     * </p>
     */
    @Test
    public void test01() throws IOException {
        // 1. 创建Object的子类(Object是所有java类的父类)
        DynamicType.Unloaded<Object> objectSubClass = new ByteBuddy()
                // 表示当前新生成的类为 Object 的子类
                .subclass(Object.class).make();
        // 2. 将生成的字节码保存到 本地 (由于没有直接指定类名, 每次运行时生成不同的类, 类名不同)
        // 我本地第一次运行: net.bytebuddy.renamed.java.lang.Object$ByteBuddy$YbDNW0Kx
        // 我本地第二次运行: net.bytebuddy.renamed.java.lang.Object$ByteBuddy$FrN82cJg
        // objectSubClass.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (2) 指定父类为非jdk自带类, 不指定命名策略和其他参数
     */
    @Test
    public void test02() throws IOException {
        // 1. 创建 非jdk自带类 的子类
        DynamicType.Unloaded<NothingClass> noJdkSubClass = new ByteBuddy()
                // 表示当前新生成的类为 NothingClass 的子类
                .subclass(NothingClass.class)
                .make();
        // 2. 将生成的字节码保存到 本地 (由于没有直接指定类名, 每次运行时生成不同的类, 类名不同)
        // 我本地第一次运行: org.example.NothingClass$ByteBuddy$f7zBKYwS
        // 我本地第二次运行: org.example.NothingClass$ByteBuddy$FHZdoEVm
        // noJdkSubClass.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (3) 指定父类为ArrayList(jdk自带类), 使用官方教程建议的Byte Buddy自带的命名策略 (NamingStrategy.SuffixingRandom)
     */
    @Test
    public void test03() throws IOException {
        // 1. 创建 ArrayList(jdk自带类) 的子类
        DynamicType.Unloaded<ArrayList> arrayListSubClass = new ByteBuddy()
                // 使用官方教程建议的Byte Buddy自带的命名策略 (NamingStrategy.SuffixingRandom)
                .with(new NamingStrategy.SuffixingRandom("ashiamd"))
                // 表示当前新生成的类为 ArrayList 的子类
                .subclass(ArrayList.class)
                .make();
        // 2. 将生成的字节码保存到 本地 (由于没有直接指定类名, 每次运行时生成不同的类, 类名不同)
        // 我本地第一次运行: net.bytebuddy.renamed.java.util.ArrayList$ashiamd$UZCeJHeg
        // 我本地第二次运行: net.bytebuddy.renamed.java.util.ArrayList$ashiamd$HYNKU9cF
        // arrayListSubClass.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (4) 父类非jdk自带类, 指定命名策略和具体类名
     */
    @Test
    public void test04() throws IOException {
        // 1. 创建 NothingClass 的子类
        DynamicType.Unloaded<NothingClass> nothingClassSubClass = new ByteBuddy()
                // 使用官方教程建议的Byte Buddy自带的命名策略 (NamingStrategy.SuffixingRandom)
                .with(new NamingStrategy.SuffixingRandom("ashiamd"))
                // 表示当前新生成的类为 NothingClass 的子类
                .subclass(NothingClass.class)
                // 指定类名
                .name("com.example.AshiamdTest04")
                .make();
        // 2. 将生成的字节码保存到 本地, 每次运行结果一致
        // 第N次运行: com.example.AshiamdTest04
        // nothingClassSubClass.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (5) 尝试指定不合法的类名, 由于Byte Buddy本身带有字节码校验逻辑, 会提前报错
     */
    @Test
    public void test05() {
        try {
            // 1. 创建 NothingClass 的子类
            DynamicType.Unloaded<NothingClass> nothingClassSubClass = new ByteBuddy()
                    // 使用官方教程建议的Byte Buddy自带的命名策略 (NamingStrategy.SuffixingRandom)
                    .with(new NamingStrategy.SuffixingRandom("ashiamd"))
                    // 表示当前新生成的类为 NothingClass 的子类
                    .subclass(NothingClass.class)
                    // 指定类名 (不合法, 不能以数字开头)
                    .name("com.example.1111AshiamdTest05")
                    .make();
        } catch (Exception e) {
            // java.lang.IllegalStateException: Illegal type name: com.example.1111AshiamdTest05 for class com.example.1111AshiamdTest04
            Assert.assertTrue(e instanceof IllegalStateException);
        }
    }

    /**
     * (6) 指定不合法类名, 关闭Byte Buddy自带的字节码校验逻辑(该校验虽耗费性能, 但一般对项目影响不大, 也不建议关闭)
     */
    @Test
    public void test06() throws IOException {
        // 1. 创建 NothingClass 的子类
        DynamicType.Unloaded<NothingClass> nothingClassSubClass = new ByteBuddy()
                // 关闭Byte Buddy的默认字节码校验逻辑
                .with(TypeValidation.of(false))
                // 使用官方教程建议的Byte Buddy自带的命名策略 (NamingStrategy.SuffixingRandom)
                .with(new NamingStrategy.SuffixingRandom("ashiamd"))
                // 表示当前新生成的类为 NothingClass 的子类
                .subclass(NothingClass.class)
                // 指定类名 (不合法, 不能以数字开头)
                .name("com.example.321AshiamdTest06")
                .make();
        // 2. 将生成的字节码保存到 本地, 生成的字节码实际非法
        // 第N次运行: com.example.321AshiamdTest06
        // nothingClassSubClass.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (7) 将生成的字节码, 注入一个jar包中 <br/>
     * 这里本地将 simple_jar 模块打包成 simple_jar-1.0-SNAPSHOT-jar-with-dependencies.jar
     */
    @Test
    public void test07() throws IOException {
        // 1. 创建 NothingClass 的子类
        DynamicType.Unloaded<NothingClass> nothingClassSubClass = new ByteBuddy()
                // 关闭Byte Buddy的默认字节码校验逻辑
                .with(TypeValidation.of(false))
                // 使用官方教程建议的Byte Buddy自带的命名策略 (NamingStrategy.SuffixingRandom)
                .with(new NamingStrategy.SuffixingRandom("ashiamd"))
                // 表示当前新生成的类为 NothingClass 的子类
                .subclass(NothingClass.class)
                // 指定类名
                .name("com.example.AshiamdTest07")
                .make();
        // 2. 将生成的字节码 注入到 simple_jar-1.0-SNAPSHOT-jar-with-dependencies.jar 中
        // 获取当前工作目录路径 (也就是当前 bytebuddy_test 目录路径)
        String currentModulePath = System.getProperty("user.dir");
        // 获取 simple_jar 模块目录路径
        String simpleJarModulePath = currentModulePath.replace("bytebuddy_test", "simple_jar");
        // 需本地提前将simple_jar 通过 mvn package 打包
        // File jarFile = new File( simpleJarModulePath + "/target/simple_jar-1.0-SNAPSHOT.jar");
        // 本地打开jar可以看到新生成的class文件也在其中
        // nothingClassSubClass.inject(jarFile);
    }

    /**
     * (8) 对实例方法插桩(stub), 修改原本的toString方法逻辑
     */
    @Test
    public void test08() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        // 1. 声明一个未加载到ClassLoader中的 Byte Buddy 对象
        DynamicType.Unloaded<NothingClass> nothingClassUnloaded = new ByteBuddy()
                // 指定 超类 NothingClass
                .subclass(NothingClass.class)
                // 指定要拦截(插桩)的方法
                .method(ElementMatchers.named("toString"))
                // 指定拦截(插桩)后的逻辑, 这里设置直接返回指定值
                .intercept(FixedValue.value("just nothing."))
                .name("com.example.AshiamdTest08")
                .make();
        // 2. 将类通过 AppClassLoader 加载到 JVM 中
        ClassLoader currentClassLoader = getClass().getClassLoader();
        Assert.assertEquals("app", currentClassLoader.getName());
        Assert.assertEquals("jdk.internal.loader.ClassLoaders$AppClassLoader",
                currentClassLoader.getClass().getName());
        DynamicType.Loaded<NothingClass> loadedType = nothingClassUnloaded.load(currentClassLoader);
        // 3. 反射调用 toString方法, 验证方法内逻辑被我们修改
        Class<? extends NothingClass> loadedClazz = loadedType.getLoaded();
        Assert.assertEquals("net.bytebuddy.dynamic.loading.ByteArrayClassLoader",
                loadedClazz.getClassLoader().getClass().getName());
        NothingClass subNothingObj = loadedClazz.getDeclaredConstructor().newInstance();
        Assert.assertEquals("just nothing.", subNothingObj.toString());
        // 4. 将字节码写入本地
        loadedType.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (9) 通过subclass继承类, 重写父类方法
     */
    @Test
    public void test09() throws IOException {
        DynamicType.Unloaded<SomethingClass> subClass = new ByteBuddy().subclass(SomethingClass.class)
                .method(ElementMatchers.named("selectUserName")
                        // 注意实际字节码Local Variable 0 位置为this引用, 但是这里说的参数位置index只需要关注方法声明时的参数顺序, 无需关注隐性参数this引用
                        .and(ElementMatchers.takesArgument(0, Long.class))
                        // .and(ElementMatchers.returns(Objects.class)) 匹配不到
                        .and(ElementMatchers.returns(String.class))
                )
                .intercept(FixedValue.value("ashiamd"))
                .name("com.example.AshiamdTest09")
                .make();
        // subClass.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (10) rebase变基, 原方法保留变为private且被改名(增加$original${随机字符串}后缀), 原方法名内逻辑替换成我们指定的逻辑
     */
    @Test
    public void test10() throws IOException {
        DynamicType.Unloaded<SomethingClass> rebase = new ByteBuddy()
                .rebase(SomethingClass.class)
                .method(ElementMatchers.named("selectUserName")
                        // 注意实际字节码Local Variable 0 位置为this引用, 但是这里说的参数位置index只需要关注方法声明时的参数顺序, 无需关注隐性参数this引用
                        .and(ElementMatchers.takesArgument(0, Long.class))
                        // .and(ElementMatchers.returns(Objects.class)) 匹配不到
                        .and(ElementMatchers.returns(String.class))
                )
                .intercept(FixedValue.value("ashiamd"))
                .method(ElementMatchers.named("getAge"))
                .intercept(FixedValue.value(0))
                .name("com.example.AshiamdTest10")
                .make();
        // rebase.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (11) redefine重定义, 重写指定的方法, 原方法逻辑不保留(被我们指定的逻辑覆盖掉)
     */
    @Test
    public void test11() throws IOException {
        DynamicType.Unloaded<SomethingClass> redefine = new ByteBuddy()
                .redefine(SomethingClass.class)
                .method(ElementMatchers.named("print")
                        // 不匹配 .and(ElementMatchers.returns(NullType.class))
                        // 不匹配 .and(ElementMatchers.returnsGeneric(Void.class))
                        // 不匹配 .and(ElementMatchers.returns(TypeDescription.ForLoadedType.of(Void.class)))
                        // 不匹配 .and(ElementMatchers.returns(Void.class))
                        // 匹配 .and(ElementMatchers.returns(TypeDescription.VOID))
                        // 匹配 .and(ElementMatchers.returns(void.class))
                )
                .intercept(FixedValue.value(TypeDescription.ForLoadedType.of(Void.class)))
                .name("com.example.AshiamdTest11")
                .make();
        // redefine.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (12) redefine基础上, 增加新方法
     */
    @Test
    public void test12() throws IOException {
        DynamicType.Unloaded<NothingClass> redefine = new ByteBuddy().redefine(NothingClass.class)
                // 定义方法的 方法名, 方法返回值类型, 方法访问修饰符
                .defineMethod("returnBlankString", String.class, Modifier.PUBLIC | Modifier.STATIC)
                // 定义方法的形参
                .withParameters(String.class, Integer.class)
                // 定义方法体内逻辑
                .intercept(FixedValue.value(""))
                .name("com.example.AshiamdTest12")
                .make();
        // redefine.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (13) 增加新成员变量, 以及生成对应的getter, setter方法
     */
    @Test
    public void test13() throws IOException {
        DynamicType.Unloaded<NothingClass> ageBean = new ByteBuddy().subclass(NothingClass.class)
                // 定义新增的字段 name, type, 访问描述符
                .defineField("age", int.class, Modifier.PRIVATE)
                // 指定类实现指定接口(接口内定义我们需要的getter和setter方法)
                .implement(IAgeBean.class)
                // 指定实现接口的逻辑
                // ok .intercept(FieldAccessor.ofField("age"))
                .intercept(FieldAccessor.ofBeanProperty())
                .name("com.example.AshiamdTest13")
                .make();
        // ageBean.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (14) 将拦截的方法委托给相同方法签名的静态方法进行修改/增强
     */
    @Test
    public void test14() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<SomethingClass> subClassUnloaded = new ByteBuddy().subclass(SomethingClass.class)
                .method(ElementMatchers.named("selectUserName"))
                // 将 selectUserName 方法委托给 SomethingInterceptor01 中的 相同方法签名(方法描述符)的静态方法 进行修改/增强
                .intercept(MethodDelegation.to(SomethingInterceptor01.class))
                .name("com.example.AshiamdTest14")
                .make();
        // 前置 saveIn则在 subClassUnloaded.load(getClass().getClassLoader()) 报错 java.lang.IllegalStateException: Class already loaded: class com.example.AshiamdTest14
        // subClassUnloaded.saveIn(DemoTools.currentClassPathFile());
        // 加载类
        String returnStr = subClassUnloaded.load(getClass().getClassLoader())
                .getLoaded()
                // 实例化并调用 selectUserName 方法验证是否被修改/增强
                .getConstructor()
                .newInstance()
                .selectUserName(1L);
        Assert.assertEquals("SomethingInterceptor01.selectUserName, userId: 1", returnStr);
        // subClassUnloaded.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (15) 将拦截的方法委托给相同方法签名的实例方法进行修改/增强
     */
    @Test
    public void test15() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<SomethingClass> subClassUnloaded = new ByteBuddy().subclass(SomethingClass.class)
                .method(ElementMatchers.named("selectUserName"))
                // 将 selectUserName 方法委托给 SomethingInterceptor02 中的 相同方法签名(方法描述符)的实例方法 进行修改/增强
                .intercept(MethodDelegation.to(new SomethingInterceptor02()))
                .name("com.example.AshiamdTest15")
                .make();
        // 前置 saveIn则在 subClassUnloaded.load(getClass().getClassLoader()) 报错 java.lang.IllegalStateException: Class already loaded: class com.example.AshiamdTest14
        // subClassUnloaded.saveIn(DemoTools.currentClassPathFile());
        // 加载类
        String returnStr = subClassUnloaded.load(getClass().getClassLoader())
                .getLoaded()
                // 实例化并调用 selectUserName 方法验证是否被修改/增强
                .getConstructor()
                .newInstance()
                .selectUserName(2L);
        Assert.assertEquals("SomethingInterceptor02.selectUserName, userId: 2", returnStr);
        // subClassUnloaded.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (16) 将拦截的方法委托给自定义方法
     */
    @Test
    public void test16() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<SomethingClass> subClassUnloaded = new ByteBuddy().subclass(SomethingClass.class)
                .method(ElementMatchers.named("selectUserName"))
                // 将 selectUserName 方法委托给 SomethingInterceptor03 进行修改/增强
                .intercept(MethodDelegation.to(new SomethingInterceptor03()))
                .name("com.example.AshiamdTest16")
                .make();
        // 前置 saveIn则在 subClassUnloaded.load(getClass().getClassLoader()) 报错 java.lang.IllegalStateException: Class already loaded: class com.example.AshiamdTest14
        // subClassUnloaded.saveIn(DemoTools.currentClassPathFile());
        // 加载类
        String returnStr = subClassUnloaded.load(getClass().getClassLoader())
                .getLoaded()
                // 实例化并调用 selectUserName 方法验证是否被修改/增强
                .getConstructor()
                .newInstance()
                .selectUserName(3L);
        // returnStr = 3
        System.out.println("returnStr = " + returnStr);
        // subClassUnloaded.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (17) 通过@Morph动态修改方法入参
     */
    @Test
    public void test17() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<SomethingClass> subClassUnloaded = new ByteBuddy().subclass(SomethingClass.class)
                .method(ElementMatchers.named("selectUserName"))
                .intercept(MethodDelegation
                        .withDefaultConfiguration()
                        // 向Byte Buddy 注册 用于中转目标方法入参和返回值的 函数式接口
                        .withBinders(Morph.Binder.install(MyCallable.class))
                        .to(new SomethingInterceptor04()))
                .name("com.example.AshiamdTest17")
                .make();
        String returnStr = subClassUnloaded.load(getClass().getClassLoader())
                .getLoaded()
                // 实例化并调用 selectUserName 方法验证是否被修改/增强
                .getConstructor()
                .newInstance()
                .selectUserName(3L);
        // 符合预期，第一个参数被修改+1
        Assert.assertEquals("4", returnStr);
        // subClassUnloaded.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (18) 对构造方法插桩
     */
    @Test
    public void test18() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        DynamicType.Unloaded<SomethingClass> subClassUnloaded = new ByteBuddy().subclass(SomethingClass.class)
                // 对任何构造方法都进行插桩
                .constructor(ElementMatchers.any())
                // 表示在被拦截的构造方法原方法逻辑执行完后，再委托给拦截器
                .intercept(SuperMethodCall.INSTANCE.andThen(
                        MethodDelegation.to(new SomethingInterceptor05())))
                .name("com.example.AshiamdTest18")
                .make();
        subClassUnloaded.load(getClass().getClassLoader())
                .getLoaded()
                // 实例化并调用 selectUserName 方法验证是否被修改/增强
                .getConstructor()
                .newInstance();
        // subClassUnloaded.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (19) 对静态方法插桩
     */
    @Test
    public void test19() throws InvocationTargetException, IllegalAccessException, IOException, NoSuchMethodException {
        DynamicType.Unloaded<SomethingClass> sayWhatUnload = new ByteBuddy().rebase(SomethingClass.class)
                // 拦截 名为 "sayWhat" 的静态方法
                .method(ElementMatchers.named("sayWhat").and(ModifierReviewable.OfByteCodeElement::isStatic))
                // 拦截后的修改/增强逻辑
                .intercept(MethodDelegation.to(new SomethingInterceptor06()))
                .name("com.example.AshiamdTest19")
                .make();
        // 调用类静态方法, 验证是否执行了增强逻辑
        Class<? extends SomethingClass> loadedClazz = sayWhatUnload.load(getClass().getClassLoader())
                .getLoaded();
        Method sayWhatMethod = loadedClazz.getMethod("sayWhat", String.class);
        sayWhatMethod.invoke(null, "hello world");
        // sayWhatUnload.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (20) 默认类加载策略`WRAPPER`, 不保存`.class`文件到本地, 重复加载类
     */
    @Test
    public void test20() {
        DynamicType.Unloaded<SomethingClass> sayWhatUnload = new ByteBuddy().rebase(SomethingClass.class)
                .method(ElementMatchers.named("sayWhat").and(ModifierReviewable.OfByteCodeElement::isStatic))
                .intercept(MethodDelegation.to(new SomethingInterceptor06()))
                .name("com.example.AshiamdTest20")
                .make();
        Class<? extends SomethingClass> loaded01 = sayWhatUnload.load(getClass().getClassLoader()).getLoaded();
        Class<? extends SomethingClass> loaded02 = sayWhatUnload.load(getClass().getClassLoader()).getLoaded();
        Assert.assertNotEquals(loaded01, loaded02);
        // loaded01 = class com.example.AshiamdTest20
        System.out.println("loaded01 = " + loaded01);
        // loaded02 = class com.example.AshiamdTest20
        System.out.println("loaded02 = " + loaded02);
        // loaded01.hashCode() = 589273327
        System.out.println("loaded01.hashCode() = " + loaded01.hashCode());
        // loaded02.hashCode() = 609656250
        System.out.println("loaded02.hashCode() = " + loaded02.hashCode());
    }

    /**
     * (21) 默认类加载策略`WRAPPER`,保存`.class`文件到本地, 之后加载类
     */
    @Test
    public void test21() throws IOException {
        DynamicType.Unloaded<SomethingClass> sayWhatUnload = new ByteBuddy().rebase(SomethingClass.class)
                .method(ElementMatchers.named("sayWhat").and(ModifierReviewable.OfByteCodeElement::isStatic))
                .intercept(MethodDelegation.to(new SomethingInterceptor06()))
                .name("com.example.AshiamdTest21")
                .make();
        sayWhatUnload.saveIn(DemoTools.currentClassPathFile());
        Assert.assertThrows(IllegalStateException.class,
                // 会抛出 java.lang.IllegalStateException: Class already loaded: class com.example.AshiamdTest21
                () -> sayWhatUnload.load(getClass().getClassLoader()).getLoaded());
    }

    /**
     * (22) 类加载策略`CHILD_FIRST`，保存`.class`文件到本地，之后重复加载类
     */
    @Test
    public void test22() throws IOException {
        DynamicType.Unloaded<SomethingClass> sayWhatUnload = new ByteBuddy().rebase(SomethingClass.class)
                .method(ElementMatchers.named("sayWhat").and(ModifierReviewable.OfByteCodeElement::isStatic))
                .intercept(MethodDelegation.to(new SomethingInterceptor06()))
                .name("com.example.AshiamdTest22")
                .make();
        sayWhatUnload.saveIn(DemoTools.currentClassPathFile());
        Class<? extends SomethingClass> loaded01 = sayWhatUnload.load(getClass().getClassLoader(),
                ClassLoadingStrategy.Default.CHILD_FIRST).getLoaded();
        Class<? extends SomethingClass> loaded02 = sayWhatUnload.load(getClass().getClassLoader(),
                ClassLoadingStrategy.Default.CHILD_FIRST).getLoaded();
        Assert.assertNotEquals(loaded01, loaded02);
        // loaded01 = class com.example.AshiamdTest22
        System.out.println("loaded01 = " + loaded01);
        // loaded02 = class com.example.AshiamdTest22
        System.out.println("loaded02 = " + loaded02);
        // loaded01.hashCode() = 1293680734
        System.out.println("loaded01.hashCode() = " + loaded01.hashCode());
        // loaded02.hashCode() = 611520720
        System.out.println("loaded02.hashCode() = " + loaded02.hashCode());
    }

    /**
     * (23) redefine后，配合`CHILD_FIRST`加载类
     */
    @Test
    public void test23() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DynamicType.Unloaded<NothingClass> redefine = new ByteBuddy().redefine(NothingClass.class)
                .defineMethod("returnBlankString", String.class, Modifier.PUBLIC | Modifier.STATIC)
                .withParameters(String.class, Integer.class)
                .intercept(FixedValue.value(""))
                .make();

        // redefine.saveIn(DemoTools.currentClassPathFile());

        Class<? extends NothingClass> loaded01 = redefine.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.CHILD_FIRST).getLoaded();
        // loaded01 = class org.example.NothingClass
        System.out.println("loaded01 = " + loaded01);
        // loaded01.equals(NothingClass.class) = false
        System.out.println("loaded01.equals(NothingClass.class) = " + loaded01.equals(NothingClass.class));
        // loaded01.getClassLoader() = net.bytebuddy.dynamic.loading.ByteArrayClassLoader$ChildFirst@23348b5d
        System.out.println("loaded01.getClassLoader() = " + loaded01.getClassLoader());
        // NothingClass.class.getClassLoader() = jdk.internal.loader.ClassLoaders$AppClassLoader@4e0e2f2a
        System.out.println("NothingClass.class.getClassLoader() = " + NothingClass.class.getClassLoader());
        // loaded01.getDeclaredConstructor().newInstance() instanceof NothingClass = false
        System.out.println("loaded01.getDeclaredConstructor().newInstance() instanceof NothingClass = " +
                (loaded01.getDeclaredConstructor().newInstance() instanceof NothingClass));

        Class<? extends NothingClass> loaded02 = redefine.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.CHILD_FIRST).getLoaded();
        // loaded02 = class org.example.NothingClass
        System.out.println("loaded02 = " + loaded02);
        // loaded01.equals(loaded02) = false
        System.out.println("loaded01.equals(loaded02) = " + loaded01.equals(loaded02));
        // loaded01.hashCode() = 1725008249
        System.out.println("loaded01.hashCode() = " + loaded01.hashCode());
        // loaded02.hashCode() = 1620890840
        System.out.println("loaded02.hashCode() = " + loaded02.hashCode());
    }

    /**
     * (24) 从指定 “jar包”, “文件目录”, “系统类加载器” 加载指定类
     */
    @Test
    public void test24() throws IOException {
        // 1. 指定需要扫描的jar包路径
        ClassFileLocator jarPathLocator = ClassFileLocator.ForJarFile.of(new File("/Users/ashiamd/mydocs/dev-tools/apache-maven-3.9.3/repository/commons-io/commons-io/2.15.0/commons-io-2.15.0.jar"));
        // 2. 指定需要扫描的.class文件所在路径
        ClassFileLocator.ForFolder classPathLocator = new ClassFileLocator.ForFolder(new File("/Users/ashiamd/mydocs/docs/study/javadocument/javadocument/IDEA_project/ash_bytebuddy_study/bytebuddy_test/target/test-classes"));
        // 3. 从系统类加载器中扫描类 (不加则找不到jdk自身的类)
        ClassFileLocator classLoaderLocator = ClassFileLocator.ForClassLoader.ofSystemLoader();
        // 整合 多个 自定义的类扫描路径
        ClassFileLocator.Compound locatorCompound = new ClassFileLocator.Compound(jarPathLocator, classPathLocator, classLoaderLocator);
        // locatorCompound 去掉 classLoaderLocator 后, 后续net.bytebuddy.ByteBuddy.redefine(ByteBuddy.java:886)往下调用时,
        // 报错 net.bytebuddy.pool.TypePool$Resolution$NoSuchTypeException: Cannot resolve type description for java.lang.Object
        // ClassFileLocator.Compound locatorCompound = new ClassFileLocator.Compound(jarPathLocator, classPathLocator);
        // 类型池, 提供根据 全限制类名 从指定 类路径扫描范围内 获取 类描述对象 的方法
        TypePool typePool = TypePool.Default.of(locatorCompound);
        // 4. 从前面指定的扫描类范围中, 获取 “commons-io-2.15.0.jar” 内 FileUtils 类描述对象, resolve()不会触发类加载
        TypeDescription jarPathTypeDescription = typePool.describe("org.apache.commons.io.FileUtils").resolve();
        // 5. 获取 target下测试类路径的NothingClass类
        TypeDescription classPathTypeDescription = typePool.describe("org.example.NothingClass").resolve();

        // 6-1 redefine 指定 jar包内的 FileUtils 类, 并将生成的.class文件保存到本地
        new ByteBuddy().redefine(jarPathTypeDescription, locatorCompound)
                .method(ElementMatchers.named("current"))
                .intercept(FixedValue.nullValue())
                .make()
                .saveIn(DemoTools.currentClassPathFile());

        // 6-2 redefine 指定.class文件路径内的 NothingClass类, 并将生成的.class文件保存到本地
        new ByteBuddy().redefine(classPathTypeDescription, locatorCompound)
                .defineMethod("justVoid", void.class, Modifier.PUBLIC)
                .intercept(FixedValue.value(void.class))
                .make()
                .saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (25) 清空指定类的所有方法的方法体(包含超类方法)
     */
    @Test
    public void test25() throws IOException {
        DynamicType.Unloaded<SomethingClass> allMethodIncludeSuper = new ByteBuddy().redefine(SomethingClass.class)
                // 拦截所有方法(包括超类方法)
                .method(ElementMatchers.any())
                // 根据方法返回值类型, 返回对应类型的默认值
                .intercept(StubMethod.INSTANCE)
                .name("com.example.AshiamdTest25")
                .make();
        // allMethodIncludeSuper.saveIn(DemoTools.currentClassPathFile());
    }

    /**
     * (26) 清空指定类的当前类声明的所有方法的方法体(不包含超类方法)
     */
    @Test
    public void test26() throws IOException, ClassNotFoundException {
        DynamicType.Unloaded<SomethingClass> allMethod = new ByteBuddy().subclass(SomethingClass.class)
                // 拦截所有目标类声明的方法(不包括超类方法)
                .method(ElementMatchers.any().and(ElementMatchers.isDeclaredBy(SomethingClass.class)))
                // 根据方法返回值类型, 返回对应类型的默认值
                .intercept(StubMethod.INSTANCE)
                // 若这里使用rebase或redefine, 则需要去掉.name(“全限制类名”), 覆盖原类后才能使清空方法体的逻辑生效
                .name("com.example.AshiamdTest26")
                .make();
        // allMethod.saveIn(DemoTools.currentClassPathFile());
    }
}
