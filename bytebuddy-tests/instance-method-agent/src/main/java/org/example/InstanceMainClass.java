package org.example;

/**
 * 入口类, 观察 java agent修改字节码的效果
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/10 4:57 PM
 */
public class InstanceMainClass {
    public static void main(String[] args) {
        System.out.println("执行 main");
        Something01 something01 = new Something01();
        System.out.println("main, something01.returnSeven(1, 2) = " + something01.returnSeven(1, 2));
        System.out.println("main, something01.returnZero(1, 2) = " + something01.returnZero(1, 2));

        Something02 something02 = new Something02();
        System.out.println("main, something02.returnHello(\"hi\", \"hello\") = " + something02.returnHello("hi", "hello"));
        System.out.println("main, something02.returnHi(\"hahaha\") = " + something02.returnHi("hahaha"));
        System.out.println("main, something02.returnArgs(\"arg1\", \"arg2\") = " + something02.returnArgs("arg1", "arg2"));
    }
}
