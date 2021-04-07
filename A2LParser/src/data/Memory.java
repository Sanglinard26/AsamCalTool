/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

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
        listByte = new ArrayList<Byte>(data.length);
        for (int i = 0; i < data.length; i++) {
            listByte.add(data[i]);
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
