/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum ConversionType {

    IDENTICAL, FORM, LINEAR, RAT_FUNC, TAB_INTP, TAB_NOINTP, TAB_VERB, NO_COMPU_METHOD, UNKNOWN;

    public static ConversionType getConversionType(String type) {
        switch (type) {
        case "IDENTICAL":
            return IDENTICAL;
        case "FORM":
            return FORM;
        case "LINEAR":
            return LINEAR;
        case "RAT_FUNC":
            return RAT_FUNC;
        case "TAB_INTP":
            return TAB_INTP;
        case "TAB_NOINTP":
            return TAB_NOINTP;
        case "TAB_VERB":
            return TAB_VERB;
        case "NO_COMPU_METHOD":
            return NO_COMPU_METHOD;
        default:
            return UNKNOWN;
        }

    }

}
