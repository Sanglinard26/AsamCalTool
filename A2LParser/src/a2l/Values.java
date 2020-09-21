/*
 * Creation : 26 janv. 2018
 */
package a2l;

import utils.NumeralString;

public final class Values {

    private final short dimX;
    private final short dimY;
    private final String[] values;
    private static int idx;

    public Values(short dimX, short dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.values = new String[dimX * dimY];
    }

    public final int getDimX() {
        return dimX;
    }

    public final int getDimY() {
        return dimY;
    }

    public final String getValue(int axeX, int axeY) {
        idx = axeY + dimX * axeX;
        return this.values[idx];
    }

    public final void setValue(int axeX, int axeY, String value) {
        idx = axeY + dimX * axeX;
        this.values[idx] = value;
    }

    public final float[] getXAxis() {
        float[] xAxis;

        if (values[0].equals("Y\\X")) {
            xAxis = new float[dimX - 1];
            for (int x = 1; x < dimX; x++) {
                if (NumeralString.isNumber(getValue(0, x))) {
                    xAxis[x - 1] = Float.parseFloat(getValue(0, x));
                } else {
                    xAxis[x - 1] = x;
                }
            }
        } else {
            xAxis = new float[dimX];
            for (int x = 0; x < dimX; x++) {
                if (NumeralString.isNumber(getValue(0, x))) {
                    xAxis[x] = Float.parseFloat(getValue(0, x));
                } else {
                    xAxis[x] = x;
                }
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
            floatValues = new float[1][dimX];

            for (short x = 0; x < dimX; x++) {

                if (NumeralString.isNumber(getValue(1, x))) {
                    floatValues[0][x] = Float.parseFloat(getValue(1, x));
                } else {
                    floatValues[0][x] = Float.NaN;
                }

            }
        }

        return floatValues;
    }

}
