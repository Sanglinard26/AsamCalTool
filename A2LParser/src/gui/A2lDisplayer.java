/*
 * Creation : 22 janv. 2020
 */
package gui;

import java.awt.Adjustable;
import java.awt.EventQueue;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
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
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

public final class A2lDisplayer extends JFrame {

    private static final String NAME = "C:\\User\\U354706\\Perso\\WorkInProgress\\64_00_42_(A520).a2l";
    static int END_OF_TEXT = 10000;

    private static final long serialVersionUID = 1L;

    static SeekableByteChannel sbc = null;
    static MappedByteBuffer mappedByteBuffer = null;
    static byte[] b;
    static int fileLength;
    static JTextArea textArea;
    static JScrollPane scrollPane;

    private static class DisplayWorker extends SwingWorker<String, String> {

        private final File file;

        private DisplayWorker(File file) {
            this.file = file;

        }

        @Override
        protected String doInBackground() {

            try {
                sbc = Files.newByteChannel(file.toPath(), StandardOpenOption.READ);
                fileLength = (int) sbc.size();
                mappedByteBuffer = ((FileChannel) sbc).map(FileChannel.MapMode.READ_ONLY, 0, sbc.size());

                b = new byte[(int) sbc.size()];
                mappedByteBuffer.get(b);

                mappedByteBuffer.clear();
                sbc.close();

                return new String(b, 0, 10000, Charset.defaultCharset());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void done() {
            try {
                textArea = new JTextArea(get(), 50, 100);
                scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                // scrollPane.setPreferredSize(new Dimension(1000, 500));
                scrollPane.getVerticalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
                scrollPane.getHorizontalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
                JOptionPane.showMessageDialog(null, scrollPane);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    static class MyAdjustmentListener implements AdjustmentListener {
        public void adjustmentValueChanged(AdjustmentEvent evt) {
            Adjustable source = evt.getAdjustable();
            JScrollBar bar = (JScrollBar) source;
            System.out.println("Bar min : " + bar.getMinimum());
            System.out.println("Bar max : " + bar.getMaximum());
            System.out.println("Bar value : " + bar.getValue());
            if (evt.getValueIsAdjusting()) {
            }
            int orient = source.getOrientation();
            if (orient == Adjustable.HORIZONTAL) {

            } else {

            }
            int type = evt.getAdjustmentType();

            switch (type) {
            case AdjustmentEvent.UNIT_INCREMENT:

                break;
            case AdjustmentEvent.UNIT_DECREMENT:

                break;
            case AdjustmentEvent.BLOCK_INCREMENT:

                break;
            case AdjustmentEvent.BLOCK_DECREMENT:

                break;
            case AdjustmentEvent.TRACK:

                break;
            }
            int value = evt.getValue();

            if (value > 0) {
                int offset = Math.min(0 + value * 10, fileLength - 1);
                int length = Math.min(END_OF_TEXT + value * 10, fileLength - 1);
                if (offset + length > fileLength - 1) {
                    length = fileLength - 1 - offset;
                }
                // textArea.setText(new String(b, offset, length, Charset.defaultCharset()));

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
