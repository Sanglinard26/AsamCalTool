/*
 * Creation : 8 janv. 2020
 */
package a2l;

import java.io.File;
import java.io.FileNotFoundException;
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

    public static final void renameLabel(A2l a2l, Function function) {

    }

}
