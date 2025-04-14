package org.example;

/**
 * 具有一些方法的类
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/11/28 4:55 PM
 */
public class SomethingClass {

    public SomethingClass() {
        System.out.println("SomethingClass()");
    }
    public String selectUserName(Long userId) {
        return String.valueOf(userId);
    }

    public void print() {
        System.out.println("print something");
    }

    public int getAge() {
        return Integer.MAX_VALUE;
    }

    public static void sayWhat(String whatToSay) {
        System.out.println("what to Say, say: " + whatToSay);
    }
}
