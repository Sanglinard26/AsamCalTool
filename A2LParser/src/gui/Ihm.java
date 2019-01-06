/*
 * Creation : 2 janv. 2019
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import a2l.A2l;
import a2l.Characteristic;

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    public Ihm() {
        super("A2LParser");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Container container = getContentPane();

        container.setLayout(new BorderLayout());

        final A2l a2l = new A2l();

        // System.out.println(a2l.getContent().toString());

        final JList<Characteristic> list = new JList<Characteristic>(
                a2l.getCharacteristics().toArray(new Characteristic[a2l.getCharacteristics().size()]));
        final JTextPane textPane = new JTextPane();

        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    textPane.setText(list.getSelectedValue().getInfo());
                }

            }
        });

        container.add(new JScrollPane(list), BorderLayout.WEST);
        container.add(textPane, BorderLayout.CENTER);

        pack();

        setVisible(true);
    }

}
