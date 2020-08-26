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

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class DisplayLog {

    private static final String NAME = "C:\\User\\U354706\\Perso\\WorkInProgress\\MG1CS032_D081C_VC2bis.a2l";

    private static class LogWorker extends SwingWorker<TableModel, String> {

        private final File file;
        private final DefaultTableModel model;

        private LogWorker(File file, DefaultTableModel model) {
            this.file = file;
            this.model = model;
            model.setColumnIdentifiers(new Object[] { file.getAbsolutePath() });
        }

        @Override
        protected TableModel doInBackground() throws Exception {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                publish(s);
            }
            return model;
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            for (String s : chunks) {
                model.addRow(new Object[] { s });
            }
        }
    }

    private void display() {
        JFrame f = new JFrame("DisplayLog");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        final JProgressBar jpb = new JProgressBar();
        f.add(jpb, BorderLayout.NORTH);
        f.add(new JScrollPane(table));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        LogWorker lw = new LogWorker(new File(NAME), model);
        lw.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                SwingWorker.StateValue s = (SwingWorker.StateValue) e.getNewValue();
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
