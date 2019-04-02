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
    private HashMap<String, AdjustableObject> adjustableObjects;
    private HashMap<String, CompuMethod> compuMethods;
    private HashMap<String, ConversionTable> conversionTables;
    private List<Measurement> measurements;
    private HashMap<String, RecordLayout> recordLayouts;
    private HashMap<String, Function> functions;

    public A2l(File a2lFile) {
        parse(a2lFile);
    }

    public HashMap<String, AdjustableObject> getAdjustableObjects() {
        return adjustableObjects;
    }

    public List<AdjustableObject> getListAdjustableObjects() {
        return new ArrayList<>(adjustableObjects.values());
    }

    private final void parse(File a2lFile) {
        final String BEGIN = "/begin";

        adjustableObjects = new HashMap<String, AdjustableObject>();
        compuMethods = new HashMap<String, CompuMethod>();
        conversionTables = new HashMap<String, ConversionTable>();
        measurements = new ArrayList<Measurement>();
        recordLayouts = new HashMap<String, RecordLayout>();
        functions = new HashMap<String, Function>();

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
                        adjustableObjects.put(axisPt.toString(), axisPt);
                        break;
                    case "CHARACTERISTIC":
                        fillParameters(buf, line, objectParameters, keyword);
                        Characteristic characteristic = new Characteristic(objectParameters);
                        adjustableObjects.put(characteristic.toString(), characteristic);
                        break;
                    case "COMPU_METHOD":
                        fillParameters(buf, line, objectParameters, keyword);
                        CompuMethod compuMethod = new CompuMethod(objectParameters);
                        compuMethods.put(compuMethod.toString(), compuMethod);
                        break;
                    case "COMPU_TAB":
                        fillParameters(buf, line, objectParameters, keyword);
                        CompuTab compuTab = new CompuTab(objectParameters);
                        conversionTables.put(compuTab.toString(), compuTab);
                        break;
                    case "COMPU_VTAB":
                        fillParameters(buf, line, objectParameters, keyword);
                        CompuVTab compuVTab = new CompuVTab(objectParameters);
                        conversionTables.put(compuVTab.toString(), compuVTab);
                        break;
                    case "COMPU_VTAB_RANGE":
                        fillParameters(buf, line, objectParameters, keyword);
                        CompuVTabRange compuVTabRange = new CompuVTabRange(objectParameters);
                        conversionTables.put(compuVTabRange.toString(), compuVTabRange);
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
                    case "FUNCTION":
                        fillParameters(buf, line, objectParameters, keyword);
                        Function function = new Function(objectParameters);
                        functions.put(function.toString(), function);
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

    private final List<String> fillParameters(BufferedReader buf, String line, List<String> objectParameters, String keyword) throws IOException {

        final String END = "/end ";
        final Pattern regexQuote = RegexHolder.QUOTE;

        objectParameters.clear();

        do {
            objectParameters.addAll(parseLineWithRegex(regexQuote, line));
        } while ((line = buf.readLine()) != null && !line.trim().equals(END + keyword));

        return objectParameters;

    }

    private final List<String> parseLineWithRegex(Pattern regexQuote, String lineToParse) {

        final List<String> listWord = new ArrayList<String>();

        final String lineWoutComment = RegexHolder.LINE_COMMENT.matcher(lineToParse.trim()).replaceAll("");

        if (RegexHolder.isString(lineWoutComment)) {
            // this string starts and end with a double quote
            listWord.add(lineWoutComment.substring(1, lineWoutComment.length() - 1));
            return listWord;
        }

        final Matcher regexMatcher = regexQuote.matcher(lineWoutComment);

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

        for (String adjustableObject : adjustableObjects.keySet()) {
            adjustableObjects.get(adjustableObject).assignComputMethod(compuMethods);
            adjustableObjects.get(adjustableObject).assignRecordLayout(recordLayouts);
            if (adjustableObjects.get(adjustableObject) instanceof Characteristic) {
                ((Characteristic) adjustableObjects.get(adjustableObject)).assignAxisPts(adjustableObjects);
            }
        }

        for (String compuMethod : compuMethods.keySet()) {
            CompuMethod compuMethod2 = compuMethods.get(compuMethod);
            Object compuTabRef = compuMethod2.getOptionalsParameters().get(SecondaryKeywords.COMPU_TAB_REF);
            if (compuTabRef != null) {
                compuMethod2.assignConversionTable(conversionTables);
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
