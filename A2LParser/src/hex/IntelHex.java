/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * @author albert_kurucz
 */
public final class IntelHex {

    public List<Memory> memorySegments = new ArrayList<Memory>();
    Integer startAddress = null;
    Memory last = null;
    int extendedAddress = 0;
    boolean endOfFile = false;

    public static byte parseHexByte(String str) {
        return (byte) Integer.parseInt(str, 16);
    }

    public static byte parseHexByte(String str, int beginIndex) {
        return (byte) Integer.parseInt(str.substring(beginIndex, beginIndex + 2), 16);
    }

    // public byte readByte(uint offset)
    // {
    // }
    public byte[] readBytes(int address, int len) {
        for (Memory mem : memorySegments) {
            if (address >= mem.address && (address + len) <= (mem.address + mem.data.length)) {
                byte[] retval = new byte[len];
                for (int i = 0; i < len; i++) {
                    retval[i] = mem.data[i + address - mem.address];
                }
                return retval;
            }
        }
        return null;
    }

    public String readString(int address) {
        for (Memory mem : memorySegments) {
            if (address >= mem.address && address < mem.address + mem.data.length) {
                String retval = "";
                while (address < mem.address + mem.data.length && Character.isLetterOrDigit((char) mem.data[address - mem.address])) {
                    retval += (char) mem.data[address - mem.address];
                    address++;
                }
                return retval;
            }
        }
        return null;
    }

    public String readString(int address, int nByte) {
        for (Memory mem : memorySegments) {
            if (address >= mem.address && address < mem.address + mem.data.length) {
                String retval = "";
                while (address < mem.address + mem.data.length && retval.length() < nByte) {
                    retval += (char) mem.data[address - mem.address];
                    address++;
                }
                return retval;
            }
        }
        return null;
    }

    public void write(Formatter f) {
        if (startAddress != null) {
            byte sum = (byte) (9 + startAddress + (startAddress >> 8) + (startAddress >> 16) + (startAddress >> 24));
            sum = (byte) (0 - sum);
            f.format(":04000005");
            f.format("%08X", startAddress);
            f.format("%02X\n", sum);
        }
        int extAddress = 0;
        for (Memory segment : memorySegments) {
            if (extAddress != (segment.address >> 16)) {
                extAddress = segment.address >> 16;
                byte sum = (byte) (6 + extAddress + (extAddress >> 8));
                sum = (byte) (0 - sum);
                f.format(":02000004");
                f.format("%04X", extAddress);
                f.format("%02X\n", sum);
            }
            segment.write(f);
        }
        f.format(":00000001FF\n");
    }

    public IntelHex(String fileName) throws FileNotFoundException, IOException {
        this(new java.io.BufferedReader(new java.io.FileReader(fileName)));
    }

    public void processLine(String line) {
        if (endOfFile) {
            return;
        }
        // System.out.println(line);
        String recordType = line.substring(7, 9);
        switch (recordType) {
        case "00":
            // Data Records
            //
            // The Intel HEX file is made up of any number of data records that are terminated with a carriage return and a linefeed. Data records
            // appear as follows:
            // :10246200464C5549442050524F46494C4500464C33
            //
            // This record is decoded as follows:
            // :10246200464C5549442050524F46494C4500464C33
            // ||||||||||| CC->Checksum
            // |||||||||DD->Data
            // |||||||TT->Record Type
            // |||AAAA->Address
            // |LL->Record Length
            // :->Colon
            // where:
            // * 10 is the number of data bytes in the record.
            // * 2462 is the address where the data are to be located in memory.
            // * 00 is the record type 00 (a data record).
            // * 464C...464C is the data.
            // * 33 is the checksum of the record.

            Memory memoryOfLine = ProcessDataRecordLine(line, extendedAddress);
            if (last == null) {
                memorySegments.add(memoryOfLine);
                last = memoryOfLine;
            } else {
                if (last.address + last.data.length == memoryOfLine.address) {
                    last.appendMemory(memoryOfLine);
                } else {
                    memorySegments.add(memoryOfLine);
                    last = memoryOfLine;
                }
            }
            break;
        case "01":
            // End-of-File (EOF) Record
            //
            // An Intel HEX file must end with an end-of-file (EOF) record.
            // This record must have the value 01 in the record type field.
            //
            // An EOF record always appears as follows:
            //
            // :00000001FF
            //
            // where:
            //
            // * 00 is the number of data bytes in the record.
            // * 0000 is the address where the data are to be located in memory. The address in end-of-file records is meaningless and is ignored. An
            // address of 0000h is typical.
            // * 01 is the record type 01 (an end-of-file record).
            // * FF is the checksum of the record and is calculated as
            // 01h + NOT(00h + 00h + 00h + 01h).
            //
            // processing of this record is so easy, there is no need for a separate function
            if (!":00000001FF".equals(line)) {
                throw new IllegalArgumentException("Illegal End-Of-Line record received");
            }
            endOfFile = true;
            break;
        case "04":
            // Extended Linear Address Records (HEX386)
            //
            // Extended linear address records are also known as 32-bit address records and HEX386 records. These records contain the upper 16 bits
            // (bits 16-31) of the data address. The extended linear address record always has two data bytes and appears as follows:
            //
            // :02000004FFFFFC
            //
            // where:
            //
            // * 02 is the number of data bytes in the record.
            // * 0000 is the address field. For the extended linear address record, this field is always 0000.
            // * 04 is the record type 04 (an extended linear address record).
            // * FFFF is the upper 16 bits of the address.
            // * FC is the checksum of the record and is calculated as
            // 01h + NOT(02h + 00h + 00h + 04h + FFh + FFh).
            //
            // When an extended linear address record is read, the extended linear address stored in the data field is saved and is applied to
            // subsequent records read from the Intel HEX file. The linear address remains effective until changed by another extended address record.
            //
            // The absolute-memory address of a data record is obtained by adding the address field in the record to the shifted address data from the
            // extended linear address record. The following example illustrates this process..
            //
            // Address from the data record's address field 2462
            // Extended linear address record data field FFFF
            // --------
            // Absolute-memory address FFFF2462
            extendedAddress = ProcessExtendedLinearAddressRecord(line);
            break;

        case "05":
            // Start address of the program
            // :0400000508000000EF
            startAddress = ProcessStartAddressRecord(line);
            break;

        default:
            break;
        }
    }

    private boolean isValidCheckSum(String record) {
        int lineLength = record.length();
        byte[] hexRecord = new byte[lineLength / 2];

        int sum = 0;
        for (int i = 0; i < hexRecord.length; i++) {
            String num = record.substring(2 * i + 1, 2 * i + 3);
            hexRecord[i] = (byte) Integer.parseInt(num, 16);
            sum += hexRecord[i] & 0xff;
        }
        sum &= 0xff;

        return sum == 0;
    }

    public IntelHex(BufferedReader sr) throws IOException {
        String line;

        // Loop, which reads and processes lines
        // from a hex file until the end of
        // the file is reached
        // or
        // the End-of-File is received
        while (!endOfFile) {
            line = sr.readLine();
            if (line == null) {
                throw new IllegalArgumentException("End of file reached unexpectedly");
            }
            processLine(line);
        }
        if (startAddress != null) {
            System.out.println("Start address: 0x" + Integer.toString(startAddress, 16));
        }
        System.out.println("Number of segments: " + memorySegments.size());
        int i = 0;
        for (Memory mem : memorySegments) {
            System.out.print("Segment[" + i + "]:");
            System.out.print(" Address: 0x" + Integer.toString(mem.address, 16));
            System.out.println(" Length: " + mem.data.length);
            i++;
        }
    }

    public static int ProcessExtendedLinearAddressRecord(String line) {
        if (!line.startsWith(":02000004")) {
            throw new IllegalArgumentException("Illegal Extended Linear Address Record line received: " + line);
        }
        int address = Integer.parseInt(line.substring(9, 13), 16);
        if (0 != (byte) (6 + address + (address >> 8) + parseHexByte(line, 13))) {
            throw new IllegalArgumentException("HexFile Extended Linear Address line checksum error");
        }
        return address << 16;
    }

    public static int ProcessStartAddressRecord(String line) {
        if (!line.startsWith(":04000005")) {
            throw new IllegalArgumentException("Illegal Extended Linear Address Record line received: " + line);
        }
        int address = Integer.parseInt(line.substring(9, 17), 16);
        if (0 != (byte) (9 + address + (address >> 8) + (address >> 16) + (address >> 24) + parseHexByte(line, 17))) {
            throw new IllegalArgumentException("HexFile Start Address line checksum error");
        }
        return address;
    }

    public static Memory ProcessDataRecordLine(String line, int address) {
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
