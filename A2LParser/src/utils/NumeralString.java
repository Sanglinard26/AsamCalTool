/*
 * Creation : 21 nov. 2018
 */
package utils;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public final class NumeralString {

    private static final Pattern NUMBER_SI = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
    private static final Pattern ZERO_REPLACEMENT = Pattern.compile("0*$");
    private static final Pattern OTHER_REPLACEMENT = Pattern.compile("\\.$");

    public static final boolean isNumber(String s) {

        return NUMBER_SI.matcher(s).matches();
    }

    public static final String cutNumber(String number) {
        return (number.indexOf("e") > -1 || number.indexOf(".") < 0) ? number
                : OTHER_REPLACEMENT.matcher(ZERO_REPLACEMENT.matcher(number).replaceAll("")).replaceAll("");
    }

    public static final String formatStringNumber(double number) {

        final DecimalFormat format = new DecimalFormat();

        if (Math.floor(number) == number) {

            format.setMaximumFractionDigits(0);

        } else {

            String text = Double.toString(number);
            int integerPlaces = text.indexOf('.');
            int decimalPlaces = text.length() - integerPlaces - 1;

            if (decimalPlaces > 3) {

                return String.format("%1.3g", number).toLowerCase();
            }

            format.setMaximumFractionDigits(3);
        }

        return format.format(number);
    }

}
