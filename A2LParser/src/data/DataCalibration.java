/*
 * Creation : 15 janv. 2020
 */
package data;

import java.util.ArrayList;
import java.util.List;

public abstract class DataCalibration {

    protected final List<Memory> memorySegments = new ArrayList<Memory>();

    public final byte[] readBytes(long address, int len) {
        for (Memory mem : memorySegments) {
            if (address >= mem.address && (address + len) <= (mem.address + mem.listByte.size())) {
                byte[] retval = new byte[len];
                for (int i = 0; i < len; i++) {
                    retval[i] = mem.listByte.get((int) (i + address - mem.address));
                }
                return retval;
            }
        }
        return new byte[0];
    }

    public final String readString(long address, int nByte) {
        long _address = address;
        for (Memory mem : memorySegments) {
            if (_address >= mem.address && _address < mem.address + mem.listByte.size()) {
                StringBuilder retval = new StringBuilder(nByte);
                while (_address < mem.address + mem.listByte.size() && retval.length() < nByte) {
                    retval.append((char) mem.listByte.get((int) (_address - mem.address)).byteValue());
                    _address++;
                }
                return retval.toString();
            }
        }
        return null;
    }

}
