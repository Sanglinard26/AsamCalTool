/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hex;

import java.util.ArrayList;
import java.util.List;

/**
 * @author albert_kurucz
 */
public final class Memory {

    public long address;
    public final List<Byte> listByte;

    public Memory(long address, byte[] data) {
        this.address = address;
        listByte = new ArrayList<Byte>();
        for (byte b : data) {
            listByte.add(b);
        }
    }

    private final boolean canAppendMemory(Memory append) {
        return append.address == (address + listByte.size());
    }

    public final void appendMemory(Memory append) {
        if (!canAppendMemory(append)) {
            throw new IllegalArgumentException("Cannot append memory");
        }
        listByte.addAll(append.listByte);
    }

}
