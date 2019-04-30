/*
 * Creation : 2 janv. 2019
 */
package a2l;

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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.RegexHolder;

public final class A2l {

    private String name;
    private ModPar modPar;
    private ModCommon modCommon;
    private HashMap<String, AdjustableObject> adjustableObjects;
    private HashMap<String, CompuMethod> compuMethods;
    private HashMap<String, ConversionTable> conversionTables;
    private List<Measurement> measurements;
    private HashMap<String, RecordLayout> recordLayouts;
    private HashMap<String, Function> functions;
    private HashMap<String, Unit> units;

    private static int numLine;
    private static int beginLine;
    private static int endLine;

    public A2l(File a2lFile) {
        parse(a2lFile);
    }

    @Override
    public String toString() {
        return getName();
    }

    public final String getName() {
        return this.name;
    }

    public final HashMap<String, AdjustableObject> getAdjustableObjects() {
        return adjustableObjects;
    }

    public final Vector<AdjustableObject> getListAdjustableObjects() {
        Vector<AdjustableObject> v = new Vector<>(adjustableObjects.values());
        Collections.sort(v);
        return v;
    }

    public final Vector<Function> getListFunction() {
        Vector<Function> v = new Vector<>(functions.values());
        Collections.sort(v);
        return v;
    }

    public final Vector<CompuMethod> getListCompuMethod() {
        Vector<CompuMethod> v = new Vector<>(compuMethods.values());
        Collections.sort(v);
        return v;
    }

    public final Vector<ConversionTable> getListConversionTable() {
        Vector<ConversionTable> v = new Vector<>(conversionTables.values());
        Collections.sort(v);
        return v;
    }

    public final Vector<RecordLayout> getListRecordLayout() {
        Vector<RecordLayout> v = new Vector<>(recordLayouts.values());
        Collections.sort(v);
        return v;
    }

    public final Vector<Unit> getListUnit() {
        Vector<Unit> v = new Vector<>(units.values());
        Collections.sort(v);
        return v;
    }

    private final void parse(File a2lFile) {
        final String BEGIN = "/begin";

        this.name = a2lFile.getName().substring(0, a2lFile.getName().length() - 4);

        adjustableObjects = new HashMap<String, AdjustableObject>();
        compuMethods = new HashMap<String, CompuMethod>();
        conversionTables = new HashMap<String, ConversionTable>();
        measurements = new ArrayList<Measurement>();
        recordLayouts = new HashMap<String, RecordLayout>();
        functions = new HashMap<String, Function>();
        units = new HashMap<String, Unit>();

        try (BufferedReader buf = new BufferedReader(new FileReader(a2lFile))) {

            String line;

            final List<String> objectParameters = new ArrayList<String>();
            final Map<String, String> mergeDefCharacteristic = new HashMap<String, String>();

            numLine = 0;

            while ((line = buf.readLine()) != null) {

                numLine++;

                if (line.length() == 0) {
                    continue;
                }

                if (line.indexOf(BEGIN) > -1) {

                    line = line.trim();

                    String keyword = getKeyword(line);

                    try {
                        switch (keyword) {
                        case "MOD_PAR":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            modPar = new ModPar(objectParameters, beginLine, endLine);
                            break;
                        case "MOD_COMMON":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            modCommon = new ModCommon(objectParameters, beginLine, endLine);
                            break;
                        case "AXIS_PTS":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            AxisPts axisPt = new AxisPts(objectParameters, beginLine, endLine);
                            adjustableObjects.put(axisPt.toString(), axisPt);
                            break;
                        case "CHARACTERISTIC":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            Characteristic characteristic = new Characteristic(objectParameters, beginLine, endLine);
                            adjustableObjects.put(characteristic.toString(), characteristic);
                            break;
                        case "COMPU_METHOD":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            CompuMethod compuMethod = new CompuMethod(objectParameters, beginLine, endLine);
                            compuMethods.put(compuMethod.toString(), compuMethod);
                            break;
                        case "COMPU_TAB":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            CompuTab compuTab = new CompuTab(objectParameters, beginLine, endLine);
                            conversionTables.put(compuTab.toString(), compuTab);
                            break;
                        case "COMPU_VTAB":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            CompuVTab compuVTab = new CompuVTab(objectParameters, beginLine, endLine);
                            conversionTables.put(compuVTab.toString(), compuVTab);
                            break;
                        case "COMPU_VTAB_RANGE":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            CompuVTabRange compuVTabRange = new CompuVTabRange(objectParameters, beginLine, endLine);
                            conversionTables.put(compuVTabRange.toString(), compuVTabRange);
                            break;
                        case "MEASUREMENT":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            measurements.add(new Measurement(objectParameters, beginLine, endLine));
                            break;
                        case "RECORD_LAYOUT":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            RecordLayout recordLayout = new RecordLayout(objectParameters, beginLine, endLine);
                            recordLayouts.put(recordLayout.toString(), recordLayout);
                            break;
                        case "FUNCTION":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            Function function = new Function(objectParameters, beginLine, endLine);
                            if (function.getDefCharacteristic() != null) {
                                mergeDefCharacteristic.putAll(function.getDefCharacteristic());
                            }
                            functions.put(function.toString(), function);
                            break;
                        case "UNIT":
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            Unit unit = new Unit(objectParameters, beginLine, endLine);
                            units.put(unit.toString(), unit);
                            break;
                        default:
                            break;
                        }
                    } catch (A2lObjectParsingException e) {
                        System.out.println(e.getMessage());
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
            numLine++;
        } while ((line = buf.readLine()) != null
                && !((line.indexOf(spaceKeyword) > -1 || line.indexOf(tabKeyword) > -1) && (line.indexOf(end) > -1)));

        return objectParameters;

    }

    private static final String getKeyword(String line) {

        byte idx = 6; // length of "/begin"
        final int lineSize = line.length();

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

    public Vector<AdjustableObject> getAdjustableObjectByFunction(String function) {

        final Vector<AdjustableObject> listByFunction = new Vector<AdjustableObject>();

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
