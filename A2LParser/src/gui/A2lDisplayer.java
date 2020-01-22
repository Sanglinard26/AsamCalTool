/*
 * Creation : 22 janv. 2020
 */
package gui;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

public final class A2lDisplayer extends JFrame {

    private static final String NAME = "C:\\User\\U354706\\Perso\\WorkInProgress\\64_00_42_(A520).a2l";

    private static final long serialVersionUID = 1L;

    static SeekableByteChannel sbc = null;
    static MappedByteBuffer mappedByteBuffer = null;

    private static class DisplayWorker extends SwingWorker<String, String> {

        private final File file;

        private DisplayWorker(File file) {
            this.file = file;

        }

        @Override
        protected String doInBackground() {

            try {
                sbc = Files.newByteChannel(file.toPath(), StandardOpenOption.READ);
                mappedByteBuffer = ((FileChannel) sbc).map(FileChannel.MapMode.READ_ONLY, 0, sbc.size());

                byte[] b = new byte[(int) sbc.size()];
                mappedByteBuffer.get(b);

                return new String(b, 0, 10000, Charset.defaultCharset());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void done() {
            JTextPane jTextPane = new JTextPane();
            try {
                jTextPane.setText(get());
            } catch (InterruptedException | ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DisplayWorker displayWorker = new DisplayWorker(new File(NAME));
                displayWorker.execute();

            }
        });

    }

}
