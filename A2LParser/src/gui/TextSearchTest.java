/*
 * Creation : 27 janv. 2020
 */
package gui;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

public class TextSearchTest {

    private static SpinnerNumberModel numberModel;

    private static class Search implements Callable<HashMap<Integer, Integer>> {

        private Document document;
        HashMap<Integer, Integer> dataOffsets;
        String searchString;

        public Search(Document document, String searchString) {
            this.document = document;
            this.searchString = searchString;
        }

        @Override
        public HashMap<Integer, Integer> call() throws Exception {
            search();

            return dataOffsets;
        }

        @SuppressWarnings("boxing")
        private void search() {

            List<Integer> lineOffsets = new ArrayList<Integer>();
            dataOffsets = new HashMap<Integer, Integer>();
            Element element = document.getDefaultRootElement();
            int elementCount = element.getElementCount();

            for (int i = 0; i < elementCount; i++) {
                lineOffsets.add(element.getElement(i).getStartOffset());
            }
            lineOffsets.add(element.getElement(element.getElementCount() - 1).getEndOffset());

            int count = 0;
            int lsOffset;
            int leOffset;
            int keyCnt = 1;

            while (count < (lineOffsets.size() - 1)) {

                lsOffset = lineOffsets.get(count);
                leOffset = lineOffsets.get(count + 1);
                count++;
                Segment seg = new Segment();

                try {
                    document.getText(lsOffset, leOffset - lsOffset, seg);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }

                String line = seg.toString();
                int mark = 0;

                while ((mark = line.indexOf(searchString, mark)) > -1) {
                    dataOffsets.put(keyCnt++, lsOffset + mark);
                    mark += searchString.length();
                }
            }

        }

    }

    private static class TextSearchPanel extends JPanel implements ActionListener {

        private static final long serialVersionUID = 1L;

        JTextField textField;
        JTextArea textArea;
        Highlighter highlighter;

        public TextSearchPanel(File file) {
            super(new GridBagLayout());

            try {

                SeekableByteChannel sbc = Files.newByteChannel(file.toPath(), StandardOpenOption.READ);
                int fileLength = (int) sbc.size();
                MappedByteBuffer mappedByteBuffer = ((FileChannel) sbc).map(FileChannel.MapMode.READ_ONLY, 0, sbc.size());

                byte[] b = new byte[(int) sbc.size()];
                mappedByteBuffer.get(b);

                mappedByteBuffer.clear();
                sbc.close();

                PlainDocument doc = new PlainDocument();

                doc.insertString(0, new String(b, 0, fileLength / 4, Charset.defaultCharset()), null);
                textField = new JTextField(20);
                textArea = new JTextArea(40, 100);

                textField.addActionListener(this);
                textField.setText("data");
                textArea.setEditable(true);
                textArea.setDocument(doc);
                JScrollPane scrollPane = new JScrollPane(textArea);

                GridBagConstraints c = new GridBagConstraints();
                c.gridwidth = GridBagConstraints.REMAINDER;

                c.fill = GridBagConstraints.HORIZONTAL;
                add(textField, c);

                numberModel = new SpinnerNumberModel();
                JSpinner spinner = new JSpinner(numberModel);
                add(spinner, c);

                spinner.addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        if (highlighter != null && highlighter.getHighlights().length > 0) {
                            int idx = numberModel.getNumber().intValue();
                            textArea.setCaretPosition(highlighter.getHighlights()[idx].getStartOffset());
                        } else {
                            numberModel.setValue(0);
                        }

                    }
                });

                c.fill = GridBagConstraints.BOTH;
                c.weightx = 1.0;
                c.weighty = 1.0;
                add(scrollPane, c);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadLocationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

        @SuppressWarnings("boxing")
        @Override
        public void actionPerformed(ActionEvent event) {

            Cursor startCursor = textArea.getCursor();
            Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
            highlighter = textArea.getHighlighter();
            String searchText = textField.getText();
            Search search = new Search(textArea.getDocument(), searchText);

            textArea.setEditable(false);
            textArea.setCursor(waitCursor);
            highlighter.removeAllHighlights();

            ExecutorService service = Executors.newSingleThreadExecutor();
            Future<HashMap<Integer, Integer>> offsets = service.submit(search);

            try {
                for (Integer start : offsets.get().values()) {
                    highlighter.addHighlight(start, start + searchText.length(), DefaultHighlighter.DefaultPainter);
                }
            } catch (Exception e) {
            }

            textArea.setEditable(true);
            textArea.setCursor(startCursor);

            if (highlighter.getHighlights().length > 0) {
                textArea.setCaretPosition(highlighter.getHighlights()[0].getStartOffset());

                numberModel.setMinimum(0);
                numberModel.setMaximum(highlighter.getHighlights().length - 1);
                numberModel.setStepSize(1);

                JOptionPane.showMessageDialog(this, "Search done!\nResult are highlighted.");
            } else {
                JOptionPane.showMessageDialog(this, "No result...");
            }

        }
    }

    public static void createAndShowGUI(File file) {

        JFrame frame = new JFrame("TextSearchTest");
        frame.add(new TextSearchPanel(file));
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String args[]) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {

                long start = System.currentTimeMillis();

                createAndShowGUI(new File("C:\\User\\U354706\\Perso\\WorkInProgress\\MG1CS032_D081C_VC2bis.a2l"));

                System.out.println("Durée : " + (System.currentTimeMillis() - start) + "ms");
            }
        });
    }
}
