/*
 * Creation : 8 janv. 2020
 */
package a2l;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public final class A2lUtils {

    private static final String RAMCELL = "[RAMCELL]";
    private static final String LABEL = "[Label]";

    public static final void writeMeasurementLab(File file, Function function) {

        try (PrintWriter pw = new PrintWriter(file)) {

            final List<String> listMeasurement = new ArrayList<>();
            listMeasurement.addAll(function.getInMeasurement());
            listMeasurement.addAll(function.getLocMeasurement());
            listMeasurement.addAll(function.getOutMeasurement());

            Collections.sort(listMeasurement);

            pw.println(RAMCELL);
            for (String measurementName : listMeasurement) {
                pw.println(measurementName);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final void writeCharacteristicLab(File file, A2l a2l, Function function) {

        try (PrintWriter pw = new PrintWriter(file)) {
            final Vector<String> listCharacteristic = a2l.getAdjustableObjectNameByFunction(function.toString());
            listCharacteristic.addAll(function.getRefCharacteristic());

            Collections.sort(listCharacteristic);

            pw.println(LABEL);
            for (String characteristicName : listCharacteristic) {
                pw.println(characteristicName);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final void linkPrototypeAxis(Object a2lObject, File selectedFile) {

        A2l a2l = (A2l) a2lObject;

        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            String line;
            Vector<String> prototypeAxis = new Vector<String>();

            while ((line = br.readLine()) != null) {
                prototypeAxis.add(line.substring(0, line.length() - 1));
            }

            Vector<AdjustableObject> v = a2l.getAdjustableObjectFromList(prototypeAxis);
            System.out.println(v);

            File newFile = new File(selectedFile.getAbsolutePath().replace(".lab", "_Linked.lab"));

            try (PrintWriter pw = new PrintWriter(newFile)) {

                pw.println("[Label]");

                for (AdjustableObject axis : v) {
                    if (axis instanceof AxisPts) {
                        pw.println(axis.toString() + "b");
                        for (Characteristic s : ((AxisPts) axis).getCharacteristicsDependency()) {
                            pw.println(s + "b");
                        }
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final void checkMEIBloc(Object a2lObject, File selectedFile) {

        A2l a2l = (A2l) a2lObject;

        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            String line;
            Vector<String> measurements = new Vector<String>();
            Vector<String> characteristics = new Vector<String>();

            boolean bMeasurement = true;

            while ((line = br.readLine()) != null) {
                if ("[MEASUREMENT]".equals(line) || "[CHARACTERISTIC]".equals(line)) {

                    if ("[CHARACTERISTIC]".equals(line)) {
                        bMeasurement = !bMeasurement;
                    }

                    line = br.readLine();
                }

                if (bMeasurement) {
                    measurements.add(line);
                } else {
                    characteristics.add(line);
                }
            }

            Vector<String> m = a2l.getMeasurementNameFromList(measurements);
            measurements.removeAll(m);

            Vector<String> c = a2l.getAdjustableObjectNameFromList(characteristics);
            characteristics.removeAll(c);

            File newFile = new File(selectedFile.getAbsolutePath().replace(".txt", "_checked.txt"));

            try (PrintWriter pw = new PrintWriter(newFile)) {

                pw.println("[MEASUREMENT]");
                for (String measure : measurements) {
                    pw.println(measure);
                }

                pw.println("[CHARACTERISTIC]");
                for (String characteristic : characteristics) {
                    pw.println(characteristic);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
