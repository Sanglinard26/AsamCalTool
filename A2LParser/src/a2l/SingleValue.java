/*
 * Creation : 3 oct. 2020
 */
package a2l;

public class SingleValue extends DataValue {

    private Object value;

    public SingleValue(Object value) {
        this.value = getStorageObject(value);
    }

    @Override
    public Object getValue(int... coord) {
        return this.value;
    }

    @Override
    public void setValue(Object value, int... coord) {
        this.value = getStorageObject(value);
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
