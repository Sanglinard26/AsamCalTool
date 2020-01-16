
package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author albert_kurucz
 */
public final class MotorolaS19 extends DataCalibration {

    private Memory last = null;
    private long extendedAddress = 0;
    private boolean endOfFile = false;

    private final static byte parseHexByte(String str, int beginIndex) {
        return (byte) Integer.parseInt(str.substring(beginIndex, beginIndex + 2), 16);
    }

    public MotorolaS19(String fileName) throws FileNotFoundException, IOException {
        this(new BufferedReader(new FileReader(fileName)));

    }

    private final void processLine(String line) {
        if (endOfFile) {
            return;
        }

        char recordType = line.charAt(1);

        switch (recordType) {
        case '0': // Entêt de bloc - 2 octets
            // startAddress = processStartAddressRecord(line);
        case '1': // Séquence de données - 2 octets

            break;
        case '2': // Séquence de données - 3 octets
            break;
        case '3': // Séquence de données - 4 octets
            Memory memoryOfLine = processDataRecordLine(line, extendedAddress);
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
        case '5': // Nombre d'enregistrement - 2 octets

            break;
        case '7': // Fin de bloc - 4 octets
            // if (!":00000001FF".equals(line)) {
            // throw new IllegalArgumentException("Illegal End-Of-Line record received");
            // }
            endOfFile = true;
            break;
        case '8': // Fin de bloc - 3 octets

            break;
        case '9': // Fin de bloc - 2 octets

            break;
        default:
            break;
        }
    }

    public MotorolaS19(BufferedReader sr) throws IOException {
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

    private static final Memory processDataRecordLine(String line, long address) {
        Memory memory = processDataRecordLine(line);
        memory.address = memory.address + address;
        return memory;
    }

    private static final Memory processDataRecordLine(String line) {
        if (line.charAt(0) != 'S') {
            throw new IllegalArgumentException("s19File line does not start with colon: " + line);
        }

        byte length = (byte) (parseHexByte(line, 2) - 5);

        long lineAddress = Long.parseLong(line.substring(4, 12), 16);

        byte[] hexLineDataBytes = new byte[length];

        byte i;
        for (i = 0; i < length; i++) {
            hexLineDataBytes[i] = parseHexByte(line, 12 + 2 * i);
        }

        if (!isValidChecksum(line)) {
            throw new IllegalArgumentException("s19File Data Record line checksum error : " + line);
        }

        return new Memory(lineAddress, hexLineDataBytes);
    }

    private static boolean isValidChecksum(String line) {
        int sum = 0;

        byte checksum = parseHexByte(line, line.length() - 2);

        for (int i = 2; i < line.length() - 2; i += 2) {
            sum += Integer.parseInt(line.substring(i, i + 2), 16);
        }

        byte calcChecksum = (byte) ~(sum & 0xFF);

        return calcChecksum == checksum;
    }

}
