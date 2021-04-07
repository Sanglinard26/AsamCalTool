/*
 * Creation : 22 janv. 2020
 */
package gui;

import java.awt.Adjustable;
import java.awt.Dimension;
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

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

public final class A2lDisplayer extends JFrame {

    private static final long serialVersionUID = 1L;

    static SeekableByteChannel sbc = null;
    static MappedByteBuffer mappedByteBuffer = null;
    static byte[] b;
    static int fileLength;
    static JTextArea textArea;
    static JScrollPane scrollPane;
    static PlainDocument doc;

    public A2lDisplayer(File a2lFile) {
        DisplayWorker displayWorker = new DisplayWorker(a2lFile);
        displayWorker.execute();
    }

    private static class DisplayWorker extends SwingWorker<PlainDocument, String> {

        private final File file;

        private DisplayWorker(File file) {
            this.file = file;

        }

        @Override
        protected PlainDocument doInBackground() {

            try {

                sbc = Files.newByteChannel(file.toPath(), StandardOpenOption.READ);
                fileLength = (int) sbc.size();
                mappedByteBuffer = ((FileChannel) sbc).map(FileChannel.MapMode.READ_ONLY, 0, sbc.size());

                b = new byte[(int) sbc.size()];
                mappedByteBuffer.get(b);

                mappedByteBuffer.clear();
                sbc.close();

                doc = new PlainDocument();

                doc.insertString(0, new String(b, 0, fileLength / 2, Charset.defaultCharset()), null);

                return doc;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void done() {
            JFrame frame = new JFrame("A2L");
            textArea = new JTextArea(doc);
            textArea.setRows(30);
            textArea.setColumns(50);
            scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.getVerticalScrollBar().addAdjustmentListener(new MyAdjustmentListener());

            frame.setSize(new Dimension(1000, 500));
            frame.add(scrollPane);

            frame.setVisible(true);

            int nleft = doc.getLength();
            Segment text = new Segment();
            int offs = 0;
            text.setPartialReturn(true);
            while (nleft > 0) {
                try {
                    doc.getText(offs, nleft, text);

                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                // do something with text
                nleft -= text.count;
                offs += text.count;
            }

        }

    }

    static class MyAdjustmentListener implements AdjustmentListener {

        boolean secondPart = false;

        public void adjustmentValueChanged(AdjustmentEvent evt) {
            Adjustable source = evt.getAdjustable();
            JScrollBar bar = (JScrollBar) source;

            int value = evt.getValue();

            if (!secondPart && value > bar.getMaximum() / 2) {
                int offset = Math.min(fileLength / 2, fileLength - 1);
                int length = fileLength - offset;
                textArea.replaceRange(new String(b, offset, length, Charset.defaultCharset()), 0, doc.getLength());
                secondPart = true;
            }

        }
    }

}
