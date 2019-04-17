/*
 * Creation : 2 janv. 2019
 */
package a2lobject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public final HashMap<String, AdjustableObject> getAdjustableObjects() {
        return adjustableObjects;
    }

    public final List<AdjustableObject> getListAdjustableObjects() {
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

            final List<String> objectParameters = new ArrayList<String>();
            final Map<String, String> mergeDefCharacteristic = new HashMap<String, String>();

            while ((line = buf.readLine()) != null) {

                if (line.length() == 0) {
                    continue;
                }

                if (line.indexOf(BEGIN) > -1) {

                    line = line.trim();

                    // String keyword = RegexHolder.MULTI_SPACE.split(line, 0)[1];
                    String keyword = getKeyword(line);

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
                        if (function.getDefCharacteristic() != null) {
                            mergeDefCharacteristic.putAll(function.getDefCharacteristic());
                        }

                        functions.put(function.toString(), function);
                        break;
                    default:
                        break;
                    }
                }
            }

            objectParameters.clear();

            assignLinkedObject(mergeDefCharacteristic);

            mergeDefCharacteristic.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final List<String> fillParameters(BufferedReader buf, String line, List<String> objectParameters, String keyword) throws IOException {

        final Pattern regexQuote = RegexHolder.QUOTE;
        final String spaceKeyword = " " + keyword;
        final String tabKeyword = "\t" + keyword;
        final String end = "/end";

        objectParameters.clear();

        do {
            line = line.trim();
            if (line.length() > 0) {
                objectParameters.addAll(parseLineWithRegex(regexQuote, line));
            }
            // } while ((line = buf.readLine()) != null && !(line.trim().endsWith(spaceKeyword) || line.trim().endsWith(tabKeyword)));
        } while ((line = buf.readLine()) != null
                && !((line.indexOf(spaceKeyword) > -1 || line.indexOf(tabKeyword) > -1) && (line.indexOf(end) > -1)));

        return objectParameters;

    }

    private static final String getKeyword(String line) {

        byte idx = 6;
        int lineSize = line.length();

        do {
            idx += 1;
        } while (idx < lineSize && !Character.isJavaIdentifierStart(line.charAt(idx)));

        byte idx2 = idx;

        do {
            idx2 += 1;
        } while (idx2 < lineSize && Character.isJavaIdentifierStart(line.charAt(idx2)));

        return line.substring(idx, idx2);
    }

    private final List<String> parseLineWithRegex(Pattern regexQuote, String line) {

        final List<String> listWord = new ArrayList<String>();

        final String lineWoutComment;

        if (line.indexOf("/*") > -1 || line.indexOf("*/") > -1 || line.indexOf("//") > -1) {
            lineWoutComment = RegexHolder.LINE_COMMENT.matcher(line).replaceAll("");
        } else {
            lineWoutComment = line;
        }

        if (lineWoutComment.indexOf("\"") > -1 && RegexHolder.isString(lineWoutComment)) {
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

    private final void assignLinkedObject(Map<String, String> defCharacteristic) {

        for (Entry<String, AdjustableObject> entry : adjustableObjects.entrySet()) {
            AdjustableObject adjustableObject = entry.getValue();
            adjustableObject.assignComputMethod(compuMethods);
            adjustableObject.assignRecordLayout(recordLayouts);
            if (adjustableObject instanceof Characteristic) {
                ((Characteristic) adjustableObject).assignAxisPts(adjustableObjects);
            }
            adjustableObject.setFunction(defCharacteristic.get(adjustableObject.toString()));
        }

        for (Entry<String, CompuMethod> entry : compuMethods.entrySet()) {
            CompuMethod compuMethod = entry.getValue();
            if (compuMethod.hasCompuTabRef()) {
                compuMethod.assignConversionTable(conversionTables);
            }
        }
    }

    public final ModPar getModPar() {
        return modPar;
    }

    public final ModCommon getModCommon() {
        return modCommon;
    }

    public List<AdjustableObject> getAdjustableObjectByFunction(String function) {

        final List<AdjustableObject> listByFunction = new ArrayList<AdjustableObject>();

        String functionRef;

        for (Entry<String, AdjustableObject> entry : adjustableObjects.entrySet()) {
            functionRef = entry.getValue().getFunction();
            if (functionRef != null && functionRef.equals(function)) {
                listByFunction.add(entry.getValue());
            }
        }

        Collections.sort(listByFunction);

        return listByFunction;
    }

}
