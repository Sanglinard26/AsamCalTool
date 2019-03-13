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
public class Memory {

    public int address;
    public List<Byte> listByte;

    public Memory(int address, byte[] data) {
        this.address = address;
        listByte = new ArrayList<Byte>();
        for (byte b : data) {
            listByte.add(b);
        }
    }

    private boolean canAppendMemory(Memory append) {
        return append.address == (address + listByte.size());
    }

    public void appendMemory(Memory append) {
        if (!canAppendMemory(append)) {
            throw new IllegalArgumentException("Cannot append memory");
        }
        listByte.addAll(append.listByte);
    }

}
