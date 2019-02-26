/*
 * Creation : 2 janv. 2019
 */
package a2lobject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.RegexHolder;

public final class A2l {

    private List<AxisPts> axisPts;
    private List<Characteristic> characteristics;
    private List<CompuMethod> compuMethods;
    private List<CompuTab> compuTabs;
    private List<CompuVTab> compuVTabs;
    private List<CompuVTabRange> compuVTabRanges;
    private List<Measurement> measurements;
    private List<RecordLayout> recordLayouts;

    public A2l(File a2lFile) {
        parse(a2lFile);
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    private final void parse(File a2lFile) {
        final String BEGIN = "/begin";
        final String END = "/end ";

        // Pattern regexQuote = RegexHolder.QUOTE;

        axisPts = new ArrayList<AxisPts>();
        characteristics = new ArrayList<Characteristic>();
        compuMethods = new ArrayList<CompuMethod>();
        compuTabs = new ArrayList<CompuTab>();
        compuVTabs = new ArrayList<CompuVTab>();
        compuVTabRanges = new ArrayList<CompuVTabRange>();
        measurements = new ArrayList<Measurement>();
        recordLayouts = new ArrayList<RecordLayout>();

        int numLine = 0;

        try (BufferedReader buf = new BufferedReader(new FileReader(a2lFile))) {

            long start = System.currentTimeMillis();
            long fileSize = a2lFile.length();

            System.out.println("File size : " + fileSize + " byte\n");

            String line;

            List<String> objectParameters = new ArrayList<String>();

            while ((line = buf.readLine()) != null) {

                numLine++;

                if (line.isEmpty()) {
                    continue;
                }

                line = line.trim();

                if (line.startsWith(BEGIN)) {

                    String keyword = RegexHolder.MULTI_SPACE.split(line, 0)[1];

                    switch (keyword) {
                    case "AXIS_PTS":
                        fillParameters(buf, line, objectParameters, keyword);
                        axisPts.add(new AxisPts(objectParameters));
                        break;
                    case "CHARACTERISTIC":
                        fillParameters(buf, line, objectParameters, keyword);
                        characteristics.add(new Characteristic(objectParameters));
                        break;
                    case "COMPU_METHOD":
                        fillParameters(buf, line, objectParameters, keyword);
                        compuMethods.add(new CompuMethod(objectParameters));
                        break;
                    case "COMPU_TAB":
                        fillParameters(buf, line, objectParameters, keyword);
                        compuTabs.add(new CompuTab(objectParameters));
                        break;
                    case "COMPU_VTAB":
                        fillParameters(buf, line, objectParameters, keyword);
                        compuVTabs.add(new CompuVTab(objectParameters));
                        break;
                    case "COMPU_VTAB_RANGE":
                        fillParameters(buf, line, objectParameters, keyword);
                        compuVTabRanges.add(new CompuVTabRange(objectParameters));
                        break;
                    case "MEASUREMENT":
                        fillParameters(buf, line, objectParameters, keyword);
                        measurements.add(new Measurement(objectParameters));
                        break;
                    case "RECORD_LAYOUT":
                        fillParameters(buf, line, objectParameters, keyword);
                        recordLayouts.add(new RecordLayout(objectParameters));
                        break;

                    default:
                        break;
                    }
                }

                // System.out.println("Parsed line : " + numLine);
            }

            assignLinkedObject();

            System.out.println("\nFini en : " + (System.currentTimeMillis() - start) + "ms\n");
            System.out.println("AxisPts : " + axisPts.size());
            System.out.println("Characteristic : " + characteristics.size());
            System.out.println("Measurement : " + measurements.size());
            System.out.println("CompuMethod : " + compuMethods.size());
            System.out.println("RecordLayout : " + recordLayouts.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final List<String> fillParameters(BufferedReader buf, String line, List<String> objectParameters, String keyword) {

        final String END = "/end ";
        final Pattern regexQuote = RegexHolder.QUOTE;

        objectParameters.clear();

        try {
            do {
                objectParameters.addAll(parseLineWithRegex(regexQuote, line));
            } while ((line = buf.readLine()) != null && !line.trim().equals(END + keyword));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objectParameters;

    }

    private final List<String> parseLineWithRegex(Pattern regexQuote, String line) {

        final List<String> listWord = new ArrayList<String>();

        line = RegexHolder.LINE_COMMENT.matcher(line.trim()).replaceAll("");

        if (RegexHolder.isString(line)) {
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
            characteristic.assignRecordLayout(recordLayouts);
        }

        for (Measurement measurement : measurements) {
            measurement.assignComputMethod(compuMethods);
        }
    }

}
