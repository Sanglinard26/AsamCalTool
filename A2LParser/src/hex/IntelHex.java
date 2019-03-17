/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author albert_kurucz
 */
public final class IntelHex {

    public final List<Memory> memorySegments = new ArrayList<Memory>();
    Long startAddress = null;
    Memory last = null;
    long extendedAddress = 0;
    boolean endOfFile = false;

    public static byte parseHexByte(String str) {
        return (byte) Integer.parseInt(str, 16);
    }

    public static byte parseHexByte(String str, int beginIndex) {
        return (byte) Integer.parseInt(str.substring(beginIndex, beginIndex + 2), 16);
    }

    public byte[] readBytes(long address, int len) {
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

    public String readString(long address, int nByte) {
        for (Memory mem : memorySegments) {
            if (address >= mem.address && address < mem.address + mem.listByte.size()) {
                String retval = "";
                while (address < mem.address + mem.listByte.size() && retval.length() < nByte) {
                    retval += (char) mem.listByte.get((int) (address - mem.address)).byteValue();
                    address++;
                }
                return retval;
            }
        }
        return null;
    }

    public IntelHex(String fileName) throws FileNotFoundException, IOException {
        this(new java.io.BufferedReader(new java.io.FileReader(fileName)));

    }

    public void processLine(String line) {
        if (endOfFile) {
            return;
        }
        String recordType = line.substring(7, 9);
        switch (recordType) {
        case "00":
            Memory memoryOfLine = ProcessDataRecordLine(line, extendedAddress);
            if (last == null) {
                memorySegments.add(memoryOfLine);
                last = memoryOfLine;
            } else {
                if (last.address + last.listByte.size() == memoryOfLine.address) {
                    last.appendMemory(memoryOfLine);
                } else {
                    memorySegments.add(memoryOfLine);
                    last = memoryOfLine;
                }
            }
            break;
        case "01":
            if (!":00000001FF".equals(line)) {
                throw new IllegalArgumentException("Illegal End-Of-Line record received");
            }
            endOfFile = true;
            break;
        case "04":
            extendedAddress = ProcessExtendedLinearAddressRecord(line);
            break;

        case "05":
            startAddress = ProcessStartAddressRecord(line);
            break;

        default:
            break;
        }
    }

    public IntelHex(BufferedReader sr) throws IOException {
        String line;

        while (!endOfFile) {
            line = sr.readLine();
            if (line == null) {
                break;
            }
            processLine(line);
        }

        sr.close();
    }

    public static long ProcessExtendedLinearAddressRecord(String line) {
        if (!line.startsWith(":02000004")) {
            throw new IllegalArgumentException("Illegal Extended Linear Address Record line received: " + line);
        }
        long address = Long.parseLong(line.substring(9, 13), 16);
        if (0 != (byte) (6 + address + (address >> 8) + parseHexByte(line, 13))) {
            throw new IllegalArgumentException("HexFile Extended Linear Address line checksum error");
        }
        return address << 16;
    }

    public static long ProcessStartAddressRecord(String line) {
        if (!line.startsWith(":04000005")) {
            throw new IllegalArgumentException("Illegal Extended Linear Address Record line received: " + line);
        }
        long address = Long.parseLong(line.substring(9, 17), 16);
        if (0 != (byte) (9 + address + (address >> 8) + (address >> 16) + (address >> 24) + parseHexByte(line, 17))) {
            throw new IllegalArgumentException("HexFile Start Address line checksum error");
        }
        return address;
    }

    public static Memory ProcessDataRecordLine(String line, long address) {
        Memory memory = ProcessDataRecordLine(line);
        memory.address = memory.address + address;
        return memory;
    }

    public static Memory ProcessDataRecordLine(String line) {
        // System.out.println(line);
        if (line.length() > 75) {
            throw new IllegalArgumentException("Longer than 75 characters line received: " + line);
        }
        if (!line.startsWith(":")) {
            throw new IllegalArgumentException("HexFile line does not start with colon: " + line);
        }
        byte length = parseHexByte(line, 1);
        byte sum = length;

        int lineAddress = Integer.parseInt(line.substring(3, 7), 16);
        sum += (byte) lineAddress;
        sum += (byte) (lineAddress >> 8);

        byte[] hexLineDataBytes = new byte[length];

        int i;
        for (i = 0; i < length; i++) {
            hexLineDataBytes[i] = parseHexByte(line, 9 + 2 * i);
            sum += hexLineDataBytes[i];
        }

        // checksum
        sum += parseHexByte(line, 9 + 2 * i);

        if (sum != 0) {
            throw new IllegalArgumentException("HexFile Data Record line checksum error");
        }

        return new Memory(lineAddress, hexLineDataBytes);
    }

}
