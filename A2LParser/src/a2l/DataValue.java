/*
 * Creation : 3 oct. 2020
 */
package a2l;

public interface DataValue {

    short getDimX();

    short getDimY();

    Object getValue(int axeX, int axeY);

}
