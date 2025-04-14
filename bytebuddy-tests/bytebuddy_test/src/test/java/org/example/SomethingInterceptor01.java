package org.example;

/**
 * 用于修改/增强 {@link SomethingClass#selectUserName(Long)} 方法
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/2 9:21 PM
 */
public class SomethingInterceptor01 {

    /**
     * 修改/增强 {@link SomethingClass#selectUserName(Long)} 方法 <br/>
     * 注意这里除了增加static访问修饰符，其他方法描述符信息和原方法(被修改/增强的目标方法)一致
     */
    public static String selectUserName(Long userId) {
        // 原方法逻辑 return String.valueOf(userId);
        return "SomethingInterceptor01.selectUserName, userId: " + userId;
    }
}
