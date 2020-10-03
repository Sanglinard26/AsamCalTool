/*
 * Creation : 3 oct. 2020
 */
package a2l;

public class SingleNumValue implements DataValue {

    private final double value;

    public SingleNumValue(double value) {
        this.value = value;
    }

    @Override
    public Object getValue(int axeX, int axeY) {
        return value;
    }

    @Override
    public short getDimX() {
        return 1;
    }

    @Override
    public short getDimY() {
        return 1;
    }

}
