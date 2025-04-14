package org.example;

import java.util.Arrays;

/**
 * 后续 进行 插桩增强的目标类 02
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/10 5:01 PM
 */
@AshCallLog
@AshOtherAnno
public class Something02 {

    @AshCallLog
    public static String returnHello(String... noUseArgs) {
        return "Hello";
    }

    @AshCallLog
    public String returnArgs(String... args) {
        return Arrays.toString(args);
    }

    @AshOtherAnno
    public String returnHi(String noUseArg) {
        return "Hi";
    }
}
