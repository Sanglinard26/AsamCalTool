/*
 * Creation : 8 mars 2019
 */
package hex;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class MyHexParser {

    public MyHexParser(Path hexPath) {
        parse(hexPath);
    }

    public final void parse(Path hexPath) {

        SeekableByteChannel sbc = null;
        MappedByteBuffer mappedByteBuffer = null;

        // open hex file
        try {
            long start = System.currentTimeMillis();

            sbc = Files.newByteChannel(hexPath, StandardOpenOption.READ);

            mappedByteBuffer = ((FileChannel) sbc).map(FileChannel.MapMode.READ_ONLY, 0, sbc.size());

            sbc.close();

            int pos = 0;
            while (mappedByteBuffer.position() < mappedByteBuffer.capacity()) {
                ByteBuffer bb;
                byte b;
                bb = getContent(mappedByteBuffer, mappedByteBuffer.position(), 1);
                pos = mappedByteBuffer.position();
                b = bb.get();
                if (b == 58) {

                    while (b != 13) {
                        bb = getContent(mappedByteBuffer, pos, 2);
                        pos = mappedByteBuffer.position();
                        char c = parseByteToAscii(bb);
                        b = (byte) c;
                        System.out.print(c);
                    }

                }
            }

            ByteBuffer bb = getContent(mappedByteBuffer, 0, 1);

            System.out.println(bb.get());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (sbc != null) {
                try {
                    sbc.close();
                } catch (IOException e) {
                }
            }
            if (mappedByteBuffer != null) {
                mappedByteBuffer.clear();

            }
        }
    }

    public final static ByteBuffer getContent(MappedByteBuffer mappedByteBuffer, int pos, int length) {

        ByteBuffer bb = ByteBuffer.allocateDirect(length);

        int end = pos + length;

        for (int i = pos; i < end; i++) {
            bb.put(mappedByteBuffer.get(i));
        }

        bb.rewind();
        mappedByteBuffer.position(end);

        return bb;

    }

    public final static char parseByteToAscii(ByteBuffer bb) {
        byte[] b = new byte[2];

        b[0] = (byte) (bb.get(0) << 4);
        b[1] = bb.get(1);

        return (char) (b[0] | b[1]);
    }

}
