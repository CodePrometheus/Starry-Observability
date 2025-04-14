package org.example;

/**
 * 用于修改/增强 {@link SomethingClass#selectUserName(Long)} 方法
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/2 9:21 PM
 */
public class SomethingInterceptor02 {

    /**
     * 修改/增强 {@link SomethingClass#selectUserName(Long)} 方法 <br/>
     * 这里和 {@link SomethingInterceptor01#selectUserName(Long)} 主要不同点在于没有 static修饰, 是实例方法
     */
    public String selectUserName(Long userId) {
        // 原方法逻辑 return String.valueOf(userId);
        return "SomethingInterceptor02.selectUserName, userId: " + userId;
    }
}
