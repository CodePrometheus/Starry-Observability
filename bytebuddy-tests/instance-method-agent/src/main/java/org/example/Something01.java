package org.example;

/**
 * 后续 进行 插桩增强的目标类 01
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/10 5:01 PM
 */
@AshCallLog
@AshOtherAnno
public class Something01 {
    public int returnZero(int number01, int number02) {
        return 0;
    }

    @AshCallLog
    public int returnSeven(int... numbers) {
        return 7;
    }
}
