/*
 * Creation : 30 juil. 2020
 */
package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class DisplayLog_Bis {

    private static final String NAME = "C:\\User\\U354706\\Perso\\WorkInProgress\\MG1CS032_D081C_VC2bis.a2l";

    private static class FileReaderWorker extends SwingWorker<List<String>, String> {

        private final File file;
        private final PlainDocument doc;

        private FileReaderWorker(File file, PlainDocument doc) {
            this.file = file;
            this.doc = doc;
        }

        public File getFile() {
            return file;
        }

        @Override
        protected List<String> doInBackground() throws Exception {
            List<String> contents = new ArrayList<>(256);
            try (BufferedReader br = new BufferedReader(new FileReader(getFile()))) {
                String text = null;
                while ((text = br.readLine()) != null) {
                    // You will want to deal with adding back in the new line characters
                    // here if that is important to you...
                    contents.add(text);
                    publish(text);
                }
            }
            return contents;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        protected void process(List<String> chunks) {
            for (String text : chunks) {
                try {
                    doc.insertString(doc.getLength(), text, null);
                } catch (BadLocationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void display() {
        JFrame f = new JFrame("DisplayLog");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PlainDocument doc = new PlainDocument();
        JTextArea ta = new JTextArea();
        final JProgressBar jpb = new JProgressBar();
        f.add(jpb, BorderLayout.NORTH);
        f.add(new JScrollPane(ta));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        FileReaderWorker lw = new FileReaderWorker(new File(NAME), doc);
        lw.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                SwingWorker.StateValue s = (SwingWorker.StateValue) e.getNewValue();
                jpb.setIndeterminate(s.equals(SwingWorker.StateValue.STARTED));
            }
        });
        lw.execute();

        while (!lw.isDone()) {

        }

        System.out.println("finished");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DisplayLog_Bis().display();
            }
        });
    }
}
