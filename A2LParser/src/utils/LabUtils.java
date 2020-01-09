/*
 * Creation : 8 janv. 2020
 */
package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import a2l.Function;

public final class LabUtils {

    public static final void writeMeasurementLab(File file, Function function) {

        try (PrintWriter pw = new PrintWriter(file)) {

            final List<String> listMeasurement = new ArrayList<>();
            listMeasurement.addAll(function.getInMeasurement());
            listMeasurement.addAll(function.getLocMeasurement());
            listMeasurement.addAll(function.getOutMeasurement());

            Collections.sort(listMeasurement);

            pw.println("[RAMCELL]");
            for (String measurementName : listMeasurement) {
                pw.println(measurementName);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final void writeCharacteristicLab(File file, Function function) {

        try (PrintWriter pw = new PrintWriter(file)) {
            final List<String> listCharacteristic = new ArrayList<>(function.getDefCharacteristic().keySet());

            Collections.sort(listCharacteristic);

            pw.println("[Label]");
            for (String characteristicName : listCharacteristic) {
                pw.println(characteristicName);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
