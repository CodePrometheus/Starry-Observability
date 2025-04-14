package org.example;

public class HelloByteCode {
    public static void main(String[] args) {
        try {
            System.out.println("try");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("finally");
        }
    }
}
