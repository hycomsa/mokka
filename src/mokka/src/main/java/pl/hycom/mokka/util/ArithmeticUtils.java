package pl.hycom.mokka.util;

/**
 * Helper class for arithmetic related operations.
 *
 * @author Kamil Adamiec (kamil.adamiec@hycom.pl)
 */
public class ArithmeticUtils {

    private ArithmeticUtils() {
        // NOP
    }

    /**
     * Method which is workaround for sonar issue with boxing Math.ceil result,
     * to non-primitive type, for purpose of converting floating point result to integer type.
     * Method uses modulo operations to make sure, that result of integer division will be always rounded upwards.
     *
     * @param dividend Dividend
     * @param divisor Divisor
     * @return Rounded result
     */
    public static Long divideAndCeil(long dividend, long divisor) {
        long remainder = dividend % divisor;
        long result = dividend / divisor;
        if(remainder != 0) {
            result++;
        }
        return result;
    }
}
