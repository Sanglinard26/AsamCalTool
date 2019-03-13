/*
 * Creation : 20 févr. 2019
 */
package constante;

public enum IndexOrder {

    INDEX_INCR, INDEX_DECR;

    public static IndexOrder getIndexOrder(String type) {
        switch (type) {
        case "INDEX_INCR":
            return IndexOrder.INDEX_INCR;
        case "INDEX_DECR":
            return IndexOrder.INDEX_DECR;
        default:
            return null;
        }

    }

}
