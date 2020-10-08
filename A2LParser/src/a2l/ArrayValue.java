/*
 * Creation : 3 oct. 2020
 */
package a2l;

import utils.NumeralString;

public class ArrayValue extends DataValue {

    private final short dimX;
    private final short dimY;
    private final Object[] values;

    public ArrayValue(short dimX, short dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.values = new Object[dimX * dimY];
    }

    @Override
    public Object getValue(int... coord) {
        int idx = coord[1] + dimX * coord[0];
        return this.values[idx];
    }

    @Override
    public void setValue(Object value, int... coord) {
        int idx = coord[1] + dimX * coord[0];
        this.values[idx] = getStorageObject(value);
    }

    @Override
    public short getDimX() {
        return dimX;
    }

    @Override
    public short getDimY() {
        return dimY;
    }

    public final float[] getXAxis() {
        float[] xAxis = new float[dimX - 1];

        for (int x = 1; x < dimX; x++) {
            if (NumeralString.isNumber(getValue(0, x).toString())) {
                xAxis[x - 1] = Float.parseFloat(getValue(0, x).toString());
            } else {
                xAxis[x - 1] = x;
            }
        }
        return xAxis;
    }

    public final float[] getYAxis() {
        float[] yAxis = new float[dimY - 1];

        for (int y = 1; y < dimY; y++) {
            if (NumeralString.isNumber(getValue(y, 0).toString())) {
                yAxis[y - 1] = Float.parseFloat(getValue(y, 0).toString());
            } else {
                yAxis[y - 1] = y;
            }
        }
        return yAxis;
    }

    public final float[][] getZvalues() {

        float[][] floatValues;

        if (values[0].equals("Y\\X")) {
            floatValues = new float[dimY - 1][dimX - 1];
            for (short y = 1; y < dimY; y++) {
                for (short x = 1; x < dimX; x++) {

                    if (NumeralString.isNumber(getValue(y, x).toString())) {
                        floatValues[y - 1][x - 1] = Float.parseFloat(getValue(y, x).toString());
                    } else {
                        floatValues[y - 1][x - 1] = Float.NaN;
                    }
                }
            }
        } else {
            floatValues = new float[dimY - 1][dimX];
            for (short y = 1; y < dimY; y++) {
                for (short x = 1; x < dimX; x++) {

                    if (NumeralString.isNumber(getValue(y, x).toString())) {
                        floatValues[y - 1][x - 1] = Float.parseFloat(getValue(y, x).toString());
                    } else {
                        floatValues[y - 1][x - 1] = Float.NaN;
                    }
                }
            }
        }

        return floatValues;
    }
}
