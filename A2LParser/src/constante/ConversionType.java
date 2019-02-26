/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum ConversionType {

    IDENTICAL, FORM, LINEAR, RAT_FUNC, TAB_INTP, TAB_NOINTP, TAB_VERB;

    public static ConversionType getConversionType(String type) {
        switch (type) {
        case "IDENTICAL":
            return ConversionType.IDENTICAL;
        case "FORM":
            return ConversionType.FORM;
        case "LINEAR":
            return ConversionType.LINEAR;
        case "RAT_FUNC":
            return ConversionType.RAT_FUNC;
        case "TAB_INTP":
            return ConversionType.TAB_INTP;
        case "TAB_NOINTP":
            return ConversionType.TAB_NOINTP;
        case "TAB_VERB":
            return ConversionType.TAB_VERB;
        default:
            return null;
        }

    }

}
