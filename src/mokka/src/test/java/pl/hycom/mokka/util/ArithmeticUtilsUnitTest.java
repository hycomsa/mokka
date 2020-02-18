package pl.hycom.mokka.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArithmeticUtilsUnitTest {

    @Test
    public void divideAndCeilTest() {

        Assertions.assertEquals(4, ArithmeticUtils.divideAndCeil(15, 4));
        Assertions.assertEquals(4, ArithmeticUtils.divideAndCeil(16, 4));
        Assertions.assertEquals(5, ArithmeticUtils.divideAndCeil(17, 4));
        Assertions.assertEquals(5, ArithmeticUtils.divideAndCeil(18, 4));
    }
}
