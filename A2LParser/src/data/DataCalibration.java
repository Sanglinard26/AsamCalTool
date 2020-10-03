/*
 * Creation : 15 janv. 2020
 */
package data;

import java.util.ArrayList;
import java.util.List;

public abstract class DataCalibration {

    protected final List<Memory> memorySegments = new ArrayList<Memory>();

    public final byte[] readBytes(long address, int len) {
        byte[] retval;
        final int memorySegmentsSize = memorySegments.size();
        Memory mem = null;

        for (int i = 0; i < memorySegmentsSize; i++) {
            mem = memorySegments.get(i);
            if (address >= mem.address && (address + len) <= (mem.address + mem.listByte.size())) {
                retval = new byte[len];
                for (int j = 0; j < len; j++) {
                    retval[j] = mem.listByte.get((int) (j + address - mem.address));
                }
                return retval;
            }
        }
        return new byte[0];
    }

    public final String readString(long address, int nByte) {
        long _address = address;
        final int memorySegmentsSize = memorySegments.size();
        Memory mem = null;

        for (int i = 0; i < memorySegmentsSize; i++) {
            mem = memorySegments.get(i);
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
