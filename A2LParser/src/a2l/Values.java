/*
 * Creation : 26 janv. 2018
 */
package a2l;

import utils.NumeralString;

public class Values {

    private final short dimX;
    private final short dimY;
    private final Object[] values;

    public Values(short dimX, short dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.values = new Object[dimX * dimY];
    }

    public final short getDimX() {
        return dimX;
    }

    public final short getDimY() {
        return dimY;
    }

    public final String getValue(int axeX, int axeY) {
        int idx = axeY + dimX * axeX;
        return this.values[idx].toString();
    }

    public final void setValue(int axeX, int axeY, Object value) {
        int idx = axeY + dimX * axeX;
        this.values[idx] = value;
        // this.values[idx] = getStorageObject(value);
        // System.out.println(value + " => " + value.getClass() + "/" + this.values[idx].getClass());
    }

    private Object getStorageObject(Object o) {

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

    public final float[] getXAxis() {
        float[] xAxis = new float[dimX - 1];

        for (int x = 1; x < dimX; x++) {
            if (NumeralString.isNumber(getValue(0, x))) {
                xAxis[x - 1] = Float.parseFloat(getValue(0, x));
            } else {
                xAxis[x - 1] = x;
            }
        }
        return xAxis;
    }

    public final float[] getYAxis() {
        float[] yAxis = new float[dimY - 1];

        for (int y = 1; y < dimY; y++) {
            if (NumeralString.isNumber(getValue(y, 0))) {
                yAxis[y - 1] = Float.parseFloat(getValue(y, 0));
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

                    if (NumeralString.isNumber(getValue(y, x))) {
                        floatValues[y - 1][x - 1] = Float.parseFloat(getValue(y, x));
                    } else {
                        floatValues[y - 1][x - 1] = Float.NaN;
                    }
                }
            }
        } else {
            floatValues = new float[dimY - 1][dimX];
            for (short y = 1; y < dimY; y++) {
                for (short x = 1; x < dimX; x++) {

                    if (NumeralString.isNumber(getValue(y, x))) {
                        floatValues[y - 1][x - 1] = Float.parseFloat(getValue(y, x));
                    } else {
                        floatValues[y - 1][x - 1] = Float.NaN;
                    }
                }
            }
        }

        return floatValues;
    }

}
