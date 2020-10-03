/*
 * Creation : 3 oct. 2020
 */
package a2l;

public class ArrayNumValue implements DataValue {

    private final short dimX;
    private final short dimY;
    private final double[] values;

    public ArrayNumValue(short dimX, short dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.values = new double[dimX * dimY];
    }

    @Override
    public Object getValue(int axeX, int axeY) {
        int idx = axeY + dimX * axeX;
        return this.values[idx];
    }

    @Override
    public short getDimX() {
        return dimX;
    }

    @Override
    public short getDimY() {
        return dimY;
    }

    public final void setValue(int axeX, int axeY, double value) {
        int idx = axeY + dimX * axeX;
        this.values[idx] = value;
    }

    public final float[] getXAxis() {
        float[] xAxis = new float[dimX - 1];

        for (int x = 1; x < dimX; x++) {
            xAxis[x - 1] = (float) getValue(0, x);

        }
        return xAxis;
    }

    public final float[] getYAxis() {
        float[] yAxis = new float[dimY - 1];

        for (int y = 1; y < dimY; y++) {
            yAxis[y - 1] = (float) getValue(y, 0);

        }
        return yAxis;
    }

    public final float[][] getZvalues() {

        float[][] floatValues;

        if (dimY > 2) {
            floatValues = new float[dimY - 1][dimX - 1];
            for (short y = 1; y < dimY; y++) {
                for (short x = 1; x < dimX; x++) {
                    floatValues[y - 1][x - 1] = (float) getValue(y, x);
                }
            }
        } else {
            floatValues = new float[dimY - 1][dimX];
            for (short y = 1; y < dimY; y++) {
                for (short x = 1; x < dimX; x++) {
                    floatValues[y - 1][x - 1] = (float) getValue(y, x);
                }
            }
        }

        return floatValues;
    }

}
