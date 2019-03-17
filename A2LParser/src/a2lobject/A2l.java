/*
 * Creation : 2 janv. 2019
 */
package a2lobject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import constante.SecondaryKeywords;
import utils.RegexHolder;

public final class A2l {

    private ModPar modPar;
    private ModCommon modCommon;
    private HashMap<String, AxisPts> axisPts;
    private List<Characteristic> characteristics;
    private HashMap<String, CompuMethod> compuMethods;
    private List<CompuTab> compuTabs;
    private HashMap<String, CompuVTab> compuVTabs;
    private List<CompuVTabRange> compuVTabRanges;
    private List<Measurement> measurements;
    private HashMap<String, RecordLayout> recordLayouts;

    public A2l(File a2lFile) {
        parse(a2lFile);
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    private final void parse(File a2lFile) {
        final String BEGIN = "/begin";

        axisPts = new HashMap<String, AxisPts>();
        characteristics = new ArrayList<Characteristic>();
        compuMethods = new HashMap<String, CompuMethod>();
        compuTabs = new ArrayList<CompuTab>();
        compuVTabs = new HashMap<String, CompuVTab>();
        compuVTabRanges = new ArrayList<CompuVTabRange>();
        measurements = new ArrayList<Measurement>();
        recordLayouts = new HashMap<String, RecordLayout>();

        try (BufferedReader buf = new BufferedReader(new FileReader(a2lFile))) {

            String line;

            List<String> objectParameters = new ArrayList<String>();

            while ((line = buf.readLine()) != null) {

                if (line.isEmpty()) {
                    continue;
                }

                line = line.trim();

                if (line.startsWith(BEGIN)) {

                    String keyword = RegexHolder.MULTI_SPACE.split(line, 0)[1];

                    switch (keyword) {
                    case "MOD_PAR":
                        fillParameters(buf, line, objectParameters, keyword);
                        modPar = new ModPar(objectParameters);
                        break;
                    case "MOD_COMMON":
                        fillParameters(buf, line, objectParameters, keyword);
                        modCommon = new ModCommon(objectParameters);
                        break;
                    case "AXIS_PTS":
                        fillParameters(buf, line, objectParameters, keyword);
                        AxisPts axisPt = new AxisPts(objectParameters);
                        axisPts.put(axisPt.toString(), axisPt);
                        break;
                    case "CHARACTERISTIC":
                        fillParameters(buf, line, objectParameters, keyword);
                        characteristics.add(new Characteristic(objectParameters));
                        break;
                    case "COMPU_METHOD":
                        fillParameters(buf, line, objectParameters, keyword);
                        CompuMethod compuMethod = new CompuMethod(objectParameters);
                        compuMethods.put(compuMethod.toString(), compuMethod);
                        break;
                    case "COMPU_TAB":
                        fillParameters(buf, line, objectParameters, keyword);
                        compuTabs.add(new CompuTab(objectParameters));
                        break;
                    case "COMPU_VTAB":
                        fillParameters(buf, line, objectParameters, keyword);
                        CompuVTab compuVTab = new CompuVTab(objectParameters);
                        compuVTabs.put(compuVTab.toString(), compuVTab);
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
                        RecordLayout recordLayout = new RecordLayout(objectParameters);
                        recordLayouts.put(recordLayout.toString(), recordLayout);
                        break;

                    default:
                        break;
                    }
                }
            }

            assignLinkedObject();

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

    private final List<String> parseLineWithRegex(Pattern regexQuote, String lineToParse) {

        final List<String> listWord = new ArrayList<String>();

        lineToParse = RegexHolder.LINE_COMMENT.matcher(lineToParse.trim()).replaceAll("");

        if (RegexHolder.isString(lineToParse)) {
            // this string starts and end with a double quote
            listWord.add(lineToParse.substring(1, lineToParse.length() - 1));
            return listWord;
        }

        final Matcher regexMatcher = regexQuote.matcher(lineToParse);

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

        for (String axisPt : axisPts.keySet()) {
            axisPts.get(axisPt).assignComputMethod(compuMethods);
            axisPts.get(axisPt).assignRecordLayout(recordLayouts);
        }

        for (Characteristic characteristic : characteristics) {
            characteristic.assignComputMethod(compuMethods);
            characteristic.assignRecordLayout(recordLayouts);
            characteristic.assignAxisPts(axisPts);
        }

        for (String compuMethod : compuMethods.keySet()) {
            CompuMethod compuMethod2 = compuMethods.get(compuMethod);
            Object compuTabRef = compuMethod2.getOptionalsParameters().get(SecondaryKeywords.COMPU_TAB_REF);
            if (compuTabRef != null) {
                compuMethod2.assignCompuVTab(compuVTabs);
            }
        }
    }

    public ModPar getModPar() {
        return modPar;
    }

    public ModCommon getModCommon() {
        return modCommon;
    }

}
