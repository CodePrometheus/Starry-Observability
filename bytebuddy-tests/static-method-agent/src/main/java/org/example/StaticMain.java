package org.example;

/**
 * main 入口, 观察 java agent修改字节码的效果
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/15 9:56 PM
 */
public class StaticMain {
    public static void main(String[] args) {
        System.out.println("执行 main");
        System.out.println(StaticUtils.hi("Ashiamd"));
        System.out.println(StaticUtils.hi("IABTD"));
    }
}
