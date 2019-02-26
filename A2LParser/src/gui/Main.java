/*
 * Creation : 28 févr. 2018
 */
package gui;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(final String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Ihm();

            }
        });

    }

}
