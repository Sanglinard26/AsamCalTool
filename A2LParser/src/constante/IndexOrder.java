/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum IndexOrder {

    INDEX_INCR, INDEX_DECR, UNKNOWN;

    public static IndexOrder getIndexOrder(String type) {
        switch (type) {
        case "INDEX_INCR":
            return INDEX_INCR;
        case "INDEX_DECR":
            return INDEX_DECR;
        default:
            return UNKNOWN;
        }

    }

}
