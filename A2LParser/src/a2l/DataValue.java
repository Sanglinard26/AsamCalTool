/*
 * Creation : 3 oct. 2020
 */
package a2l;

public abstract class DataValue {

    public abstract short getDimX();

    public abstract short getDimY();

    public abstract Object getValue(int... coord);

    public abstract void setValue(Object value, int... coord);

    protected final Object getStorageObject(Object o) {

        if (o instanceof Double) {

            double d = (double) o;
            int i = (int) d;

            if (d - i != 0) {
                return (double) o;
            } else if (i <= Byte.MAX_VALUE && i >= Byte.MIN_VALUE) {
                return (byte) i;
            } else if (i <= Short.MAX_VALUE && i >= Short.MIN_VALUE) {
                return (short) i;
            } else {
                return i;
            }
        }

        return o;
    }

}
