package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;

/**
 * @see https://stackoverflow.com/a/25526869/230513
 */
public class DisplayLog {

    private static final String NAME = "C:\\User\\U354706\\Perso\\WorkInProgress\\64_00_42_(A520).a2l";

    static JTextArea area;

    private static class LogWorker extends SwingWorker<Document, String> {

        private final File file;
        private final Document doc;

        private LogWorker(File file, Document doc) {
            this.file = file;
            this.doc = doc;
        }

        @Override
        protected Document doInBackground() throws Exception {
            BufferedReader br = new BufferedReader(new FileReader(file));
            int len;
            char[] text = new char[8096];
            while ((len = br.read(text)) != -1) {
                publish(new String(text) + "\n");
            }
            return doc;
        }

        @Override
        protected void process(List<String> chunks) {
            for (String s : chunks) {
                try {
                    doc.insertString(doc.getLength(), s, null);
                } catch (BadLocationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void done() {
            area.setCaretPosition(0);
        }
    }

    private void display() {
        JFrame f = new JFrame("DisplayLog");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Document document = new DefaultStyledDocument();
        area = new JTextArea(document);
        final JProgressBar jpb = new JProgressBar();
        f.add(jpb, BorderLayout.NORTH);
        f.add(new JScrollPane(area));
        f.setSize(new Dimension(800, 600));
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        LogWorker lw = new LogWorker(new File(NAME), document);
        lw.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SwingWorker.StateValue s = (SwingWorker.StateValue) evt.getNewValue();
                jpb.setIndeterminate(s.equals(SwingWorker.StateValue.STARTED));

            }
        });
        lw.execute();
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new DisplayLog().display();

            }
        });

    }
}
