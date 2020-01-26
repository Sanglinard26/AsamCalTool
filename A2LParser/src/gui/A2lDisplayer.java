/*
 * Creation : 22 janv. 2020
 */
package gui;

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.print.Doc;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public final class A2lDisplayer extends JFrame {

    //private static final String NAME = "C:\\User\\U354706\\Perso\\WorkInProgress\\64_00_42_(A520).a2l";
    private static final String NAME = "D:\\tramp\\Documents\\Tmp\\A2L\\WorkInProgress\\64_00_42_(A520).a2l";
    static int END_OF_TEXT = 10000;

    private static final long serialVersionUID = 1L;

    static SeekableByteChannel sbc = null;
    static MappedByteBuffer mappedByteBuffer = null;
    static byte[] b;
    static int fileLength;
    static JTextArea textArea;
    static JScrollPane scrollPane;
    static PlainDocument doc;

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
                
                doc.insertString(0, new String(b, 0, fileLength/2, Charset.defaultCharset()), null);
            	
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
            textArea.setRows(50);
			scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.getVerticalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
			scrollPane.getHorizontalScrollBar().addAdjustmentListener(new MyAdjustmentListener());

			frame.add(scrollPane);
			System.out.println(scrollPane.getVerticalScrollBar().getMaximum());
			frame.pack();
			frame.setVisible(true);
        }

    }

    static class MyAdjustmentListener implements AdjustmentListener {
        public void adjustmentValueChanged(AdjustmentEvent evt) {
            Adjustable source = evt.getAdjustable();
            JScrollBar bar = (JScrollBar) source;
            System.out.println("Bar min : " + bar.getMinimum());
            System.out.println("Bar max : " + bar.getMaximum());
            System.out.println("Bar value : " + bar.getValue());

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
