/*
 * Creation : 2 janv. 2019
 */
package a2l;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class A2l {

    private List<Characteristic> characteristics;
    private List<CompuMethod> compuMethods;
    private List<CompuTab> compuTabs;
    private List<CompuVTab> compuVTabs;
    private List<CompuVTabRange> compuVTabRanges;
    private List<Measurement> measurements;

    public A2l() {
        parse();
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    private final void parse() {
        final String BEGIN = "/begin";
        final String SPACE = " ";

        String A520 = "64_00_42 (A520).a2l";
        String B060C = "MG1CS032_B060C_VFm0C_tuned.a2l";
        String ASAM161 = "ASAP2_Demo_V161.a2l";
        String ASAPEXAMPLE = "ASAP2Example.a2l";
        String C46 = "ConfidentielPSA_c139646al00.a2l";
        String DW10B = "P6A84B00.a2l";

        // content = new StringBuilder();

        characteristics = new ArrayList<Characteristic>();
        compuMethods = new ArrayList<CompuMethod>();
        compuTabs = new ArrayList<CompuTab>();
        compuVTabs = new ArrayList<CompuVTab>();
        compuVTabRanges = new ArrayList<CompuVTabRange>();
        measurements = new ArrayList<Measurement>();

        try (BufferedReader buf = new BufferedReader(new FileReader("C:/Users/U354706/Desktop/Tmp/" + DW10B))) {

            long start = System.currentTimeMillis();

            String line;
            String[] splitSpace;
            String objectName;

            List<String> objectParameters = new ArrayList<String>();

            while ((line = buf.readLine()) != null) {

                if (line.contains(BEGIN)) {

                    objectParameters.clear();

                    splitSpace = line.trim().split(SPACE);

                    int nbSplit = splitSpace.length;

                    if (nbSplit == 2) {
                        objectName = null;
                    } else {
                        objectName = splitSpace[2];
                    }

                    switch (splitSpace[1]) {
                    case "CHARACTERISTIC":
                        if (characteristics.size() == 1) {
                            System.out.println("Parse Characteristics...");
                        }
                        characteristics.add(new Characteristic(walkInObject(buf, objectName)));
                        // System.out.println(characteristics.get(characteristics.size() - 1).getInfo());
                        break;
                    case "COMPU_METHOD":
                        if (compuMethods.size() == 1) {
                            System.out.println("Parse CompuMethods...");
                        }
                        compuMethods.add(new CompuMethod(walkInObject(buf, objectName)));
                        // System.out.println(compuMethods.get(compuMethods.size() - 1).getInfo());
                        break;
                    case "COMPU_TAB":
                        if (compuTabs.size() == 1) {
                            System.out.println("Parse CompuTabs...");
                        }
                        compuTabs.add(new CompuTab(walkInObject(buf, objectName)));
                        // System.out.println(compuTabs.get(compuTabs.size() - 1).getInfo());
                        break;
                    case "COMPU_VTAB":
                        if (compuVTabs.size() == 1) {
                            System.out.println("Parse CompuVTabs...");
                        }
                        compuVTabs.add(new CompuVTab(walkInObject(buf, objectName)));
                        // System.out.println(compuVTabs.get(compuVTabs.size() - 1).getInfo());
                        break;
                    case "COMPU_VTAB_RANGE":
                        if (compuVTabRanges.size() == 1) {
                            System.out.println("Parse CompuTabRanges...");
                        }
                        compuVTabRanges.add(new CompuVTabRange(walkInObject(buf, objectName)));
                        // System.out.println(compuVTabRanges.get(compuVTabRanges.size() - 1).getInfo());
                        break;
                    case "MEASUREMENT":
                        if (measurements.size() == 1) {
                            System.out.println("Parse Measurements...");
                        }
                        measurements.add(new Measurement(walkInObject(buf, objectName)));
                        // System.out.println(measurements.get(measurements.size() - 1).getInfo());
                        break;
                    default:
                        break;
                    }

                }
            }

            assignLinkedObject();

            System.out.println("\nFini en : " + (System.currentTimeMillis() - start) + "ms\n");
            System.out.println("Characteristic : " + characteristics.size());
            System.out.println("Measurement : " + measurements.size());
            System.out.println("CompuMethod : " + compuMethods.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final List<String> walkInObject(BufferedReader buf, String objectName) throws IOException {

        final String END = "/end";

        final List<String> parameters = new ArrayList<String>();

        if (objectName != null) {
            parameters.add(objectName);
        }

        String line;
        String[] splitSpace;

        while ((line = buf.readLine()) != null && !line.contains(END)) {

            line = line.trim();

            if (line.contains("ACM_AMF_ACTUATOR_CUTTED_TIME_APV")
                    || (objectName != null && objectName.contains("ACM_AMF_ACTUATOR_CUTTED_TIME_APV"))) {
                int z = 0;
            }

            if (!line.isEmpty()) {

                int idxCom = line.indexOf("/*"); // Si idxCom >-1 alors il y a un commentaire sur le parametre
                int idxQuote = line.indexOf('"');

                if (idxCom > -1) {
                    line = line.substring(0, idxCom).trim();
                }

                /*
                 * if (idxQuote == -1) { splitSpace = line.split(" "); } else { splitSpace = line.split("\""); }
                 * 
                 * if (line.charAt(0) != '"' && splitSpace.length > 1) {
                 * 
                 * for (String s : splitSpace) { s = s.trim(); if (!s.isEmpty()) { parameters.add(s); } }
                 * 
                 * } else { parameters.add(line.replace("\"", "")); }
                 */

                parameters.addAll(parseLine(line));
            }
        }

        return parameters;
    }

    private final List<String> parseLine(String line) {

        List<String> listWord = new ArrayList<String>();

        int lengthLine = line.length();
        int begin = 0;
        int end = 0;

        for (int i = 0; i < lengthLine; i++) {

            if (line.charAt(i) == '"') { // Cas d'un string

                if (i == 0) {
                    begin = i + 1;
                    end = lengthLine - 1;
                    listWord.add(line.substring(begin, end));
                    break;
                }

                begin = ++i;

                while (i < lengthLine && line.charAt(i) != '"') {
                    i++;
                }

                end = i;
                listWord.add(line.substring(begin, end));

                begin = end;
            }

            if (line.charAt(i) != ' ' && line.charAt(i) != '"') {

                begin = i;

                while (i < lengthLine && line.charAt(i) != ' ') {
                    i++;
                }
                end = i;
                listWord.add(line.substring(begin, end));

                begin = end;
            }

        }

        return listWord;
    }

    private final void assignLinkedObject() {

        System.out.println("Assign LinkedObject...");

        for (Characteristic characteristic : characteristics) {
            characteristic.assignComputMethod(compuMethods);
        }

        for (Measurement measurement : measurements) {
            measurement.assignComputMethod(compuMethods);
        }
    }

}
