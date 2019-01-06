/*
 * Creation : 2 janv. 2019
 */
package a2l;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        Pattern regexQuote = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        // Pattern regexQuote = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"");

        // content = new StringBuilder();

        characteristics = new ArrayList<Characteristic>();
        compuMethods = new ArrayList<CompuMethod>();
        compuTabs = new ArrayList<CompuTab>();
        compuVTabs = new ArrayList<CompuVTab>();
        compuVTabRanges = new ArrayList<CompuVTabRange>();
        measurements = new ArrayList<Measurement>();

        try (BufferedReader buf = new BufferedReader(new FileReader("C:/Users/U354706/Desktop/Tmp/" + C46))) {

            long start = System.currentTimeMillis();

            String line;

            List<String> objectParameters = new ArrayList<String>();

            while ((line = buf.readLine()) != null) {

                line = line.trim();

                if (line.startsWith(BEGIN)) {

                    objectParameters.clear();

                    if (line.contains("CHARACTERISTIC") && !line.contains("DEF_CHARACTERISTIC") && !line.contains("REF_CHARACTERISTIC")) {
                        do {
                            objectParameters.addAll(parseLineWithRegex(regexQuote, line));
                        } while ((line = buf.readLine()) != null && !line.contains("/END") && !line.contains("CHARACTERISTIC"));

                        characteristics.add(new Characteristic(objectParameters));
                        // System.out.println(characteristics.get(characteristics.size() - 1).getInfo());
                    }

                    if (line.contains("COMPU_METHOD")) {
                        do {
                            objectParameters.addAll(parseLineWithRegex(regexQuote, line));
                        } while ((line = buf.readLine()) != null && !line.contains("/END") && !line.contains("COMPU_METHOD"));

                        compuMethods.add(new CompuMethod(objectParameters));
                        // System.out.println(compuMethods.get(compuMethods.size() - 1).getInfo());
                    }

                    if (line.contains("COMPU_TAB")) {
                        do {
                            objectParameters.addAll(parseLineWithRegex(regexQuote, line));
                        } while ((line = buf.readLine()) != null && !line.contains("/END") && !line.contains("COMPU_TAB"));

                        compuTabs.add(new CompuTab(objectParameters));
                        // System.out.println(compuTabs.get(compuTabs.size() - 1).getInfo());
                    }

                    if (line.contains("COMPU_VTAB") && !line.contains("COMPU_VTAB_RANGE")) {
                        do {
                            objectParameters.addAll(parseLineWithRegex(regexQuote, line));
                        } while ((line = buf.readLine()) != null && !line.contains("/END") && !line.contains("COMPU_VTAB"));

                        compuVTabs.add(new CompuVTab(objectParameters));
                        // System.out.println(compuVTabs.get(compuVTabs.size() - 1).getInfo());
                    }

                    if (line.contains("COMPU_VTAB_RANGE")) {
                        do {
                            objectParameters.addAll(parseLineWithRegex(regexQuote, line));
                        } while ((line = buf.readLine()) != null && !line.contains("/END") && !line.contains("COMPU_VTAB_RANGE"));

                        compuVTabRanges.add(new CompuVTabRange(objectParameters));
                        // System.out.println(compuVTabRanges.get(compuVTabRanges.size() - 1).getInfo());
                    }

                    if (line.contains("MEASUREMENT") && !line.contains("OUT_MEASUREMENT") && !line.contains("LOC_MEASUREMENT")
                            && !line.contains("IN_MEASUREMENT") && !line.contains("REF_MEASUREMENT")) {
                        do {
                            if (line.contains("Ext_bBrkReq")) {
                                int i = 0;
                            }
                            objectParameters.addAll(parseLineWithRegex(regexQuote, line));
                        } while ((line = buf.readLine()) != null && !line.contains("/END") && !line.contains("MEASUREMENT"));

                        measurements.add(new Measurement(objectParameters));
                        // System.out.println(measurements.get(measurements.size() - 1).getInfo());
                    }

                }
            }

            // assignLinkedObject();

            System.out.println("\nFini en : " + (System.currentTimeMillis() - start) + "ms\n");
            System.out.println("Characteristic : " + characteristics.size());
            System.out.println("Measurement : " + measurements.size());
            System.out.println("CompuMethod : " + compuMethods.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final List<String> parseLineWithRegex(Pattern regexQuote, String line) {

        final List<String> listWord = new ArrayList<String>();

        line = line.trim().replaceAll("/\\*.*?\\*/", "");// single line comments

        if (line.matches("\".*\"")) {
            // this string starts and end with a double quote
            listWord.add(line.substring(1, line.length() - 1));
            return listWord;
        }

        final Matcher regexMatcher = regexQuote.matcher(line);

        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                listWord.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                System.out.println("group2");
                listWord.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                listWord.add(regexMatcher.group());
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
