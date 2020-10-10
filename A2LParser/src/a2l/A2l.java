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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;

import constante.PrimaryKeywords;
import utils.ParserUtils;

public final class A2l {

    private File path;
    private String name;
    private ModPar modPar;
    private ModCommon modCommon;
    private HashMap<Integer, AdjustableObject> adjustableObjects;
    private HashMap<Integer, CompuMethod> compuMethods;
    private HashMap<Integer, ConversionTable> conversionTables;
    private Vector<Measurement> measurements;
    private HashMap<Integer, RecordLayout> recordLayouts;
    private Vector<Function> functions;

    private Vector<Unit> units;

    private static int numLine;
    private static int beginLine;
    private static int endLine;

    private EventListenerList listeners;

    public A2l() {

        adjustableObjects = new HashMap<Integer, AdjustableObject>();
        compuMethods = new HashMap<Integer, CompuMethod>();
        conversionTables = new HashMap<Integer, ConversionTable>();
        measurements = new Vector<Measurement>();
        recordLayouts = new HashMap<Integer, RecordLayout>();
        units = new Vector<Unit>();

        functions = new Vector<Function>();

        listeners = new EventListenerList();

    }

    @Override
    public String toString() {
        return getName();
    }

    public final File getPath() {
        return path;
    }

    public final String getName() {
        return this.name;
    }

    public final HashMap<Integer, AdjustableObject> getAdjustableObjects() {
        return adjustableObjects;
    }

    public final Vector<AdjustableObject> getListAdjustableObjects() {
        Vector<AdjustableObject> v = new Vector<>(adjustableObjects.values());
        Collections.sort(v);
        return v;
    }

    public final Vector<Function> getListFunction() {
        Collections.sort(functions);
        return functions;
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
        Collections.sort(units);
        return units;
    }

    public final Vector<SystemConstant> getListSystemConstant() {
        Vector<SystemConstant> v = new Vector<>(modPar.getSystemConstant());
        Collections.sort(v);
        return v;
    }

    public final Vector<Measurement> getListMeasurement() {
        Collections.sort(measurements);
        return measurements;
    }

    public final void addA2lStateListener(A2lStateListener a2lStateListener) {
        this.listeners.add(A2lStateListener.class, a2lStateListener);
    }

    public final void removeA2lStateListener(A2lStateListener a2lStateListener) {
        this.listeners.remove(A2lStateListener.class, a2lStateListener);
    }

    public final boolean parse(File a2lFile) {
        final String BEGIN = "/begin";

        this.path = a2lFile;
        this.name = a2lFile.getName().substring(0, a2lFile.getName().length() - 4);

        fireStateChanged("Loading file : " + a2lFile.getAbsolutePath());

        long startParsing = System.currentTimeMillis();

        try (BufferedReader buf = new BufferedReader(new FileReader(a2lFile))) {

            String line;

            final List<String> objectParameters = new ArrayList<String>();
            final Map<Integer, String> mergeDefCharacteristic = new HashMap<Integer, String>();

            numLine = 0;

            fireStateChanged("Parsing in progress");

            AxisPts axisPt = null;
            Characteristic characteristic = null;
            CompuMethod compuMethod = null;
            CompuTab compuTab = null;
            CompuVTab compuVTab = null;
            CompuVTabRange compuVTabRange = null;
            RecordLayout recordLayout = null;
            Function function = null;

            while ((line = buf.readLine()) != null) {

                numLine++;

                if (line.length() == 0) {
                    continue;
                }

                if (line.indexOf(BEGIN) > -1) {

                    line = line.trim();

                    PrimaryKeywords keyword = PrimaryKeywords.getPrimaryKeywords(sumKeywordChar(line));

                    try {
                        switch (keyword) {
                        case MOD_PAR:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            modPar = new ModPar(objectParameters, beginLine, endLine);
                            break;
                        case MOD_COMMON:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            modCommon = new ModCommon(objectParameters, beginLine, endLine);
                            break;
                        case AXIS_PTS:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            axisPt = new AxisPts(objectParameters, beginLine, endLine);
                            adjustableObjects.put(axisPt.getID(), axisPt);
                            break;
                        case CHARACTERISTIC:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            characteristic = new Characteristic(objectParameters, beginLine, endLine);
                            adjustableObjects.put(characteristic.getID(), characteristic);
                            break;
                        case COMPU_METHOD:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            compuMethod = new CompuMethod(objectParameters, beginLine, endLine);
                            compuMethods.put(compuMethod.toString().hashCode(), compuMethod);
                            break;
                        case COMPU_TAB:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            compuTab = new CompuTab(objectParameters, beginLine, endLine);
                            conversionTables.put(compuTab.toString().hashCode(), compuTab);
                            break;
                        case COMPU_VTAB:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            compuVTab = new CompuVTab(objectParameters, beginLine, endLine);
                            conversionTables.put(compuVTab.toString().hashCode(), compuVTab);
                            break;
                        case COMPU_VTAB_RANGE:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            compuVTabRange = new CompuVTabRange(objectParameters, beginLine, endLine);
                            conversionTables.put(compuVTabRange.toString().hashCode(), compuVTabRange);
                            break;
                        case MEASUREMENT:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            measurements.add(new Measurement(objectParameters, beginLine, endLine));
                            break;
                        case RECORD_LAYOUT:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            recordLayout = new RecordLayout(objectParameters, beginLine, endLine);
                            recordLayouts.put(recordLayout.toString().hashCode(), recordLayout);
                            break;
                        case FUNCTION:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            function = new Function(objectParameters, beginLine, endLine);
                            if (function.getDefCharacteristic() != null) {
                                mergeDefCharacteristic.putAll(function.getDefCharacteristic());
                            }
                            functions.add(function);
                            break;
                        case UNIT:
                            beginLine = numLine;
                            fillParameters(buf, line, objectParameters, keyword);
                            endLine = numLine;
                            units.add(new Unit(objectParameters, beginLine, endLine));
                            break;
                        default:
                            break;
                        }
                    } catch (A2lObjectParsingException e) {
                        fireStateChanged(e.getMessage());
                    }

                }
            }

            objectParameters.clear();

            fireStateChanged("Linking A2l object");
            assignLinkedObject(mergeDefCharacteristic);

            fireStateChanged("Parsing finished in " + (System.currentTimeMillis() - startParsing) + "ms");

            mergeDefCharacteristic.clear();

            return true;

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    private void fireStateChanged(String state) {
        for (A2lStateListener listener : listeners.getListeners(A2lStateListener.class)) {
            listener.stateChange(state);
        }
    }

    private final List<String> fillParameters(BufferedReader buf, String line, List<String> objectParameters, PrimaryKeywords keyword)
            throws IOException {

        final Pattern regexQuote = ParserUtils.QUOTE;
        final String spaceKeyword = " " + keyword;
        final String tabKeyword = "\t" + keyword;
        final String end = "/end";

        objectParameters.clear();

        do {
            line = line.trim();
            if (line.length() > 0) {
                objectParameters.addAll(parseLineWithRegex(regexQuote, line, buf));
            }
            numLine++;
        } while ((line = buf.readLine()) != null
                && !((line.indexOf(spaceKeyword) > -1 || line.indexOf(tabKeyword) > -1) && (line.indexOf(end) > -1)));

        return objectParameters;

    }

    private static final int sumKeywordChar(String line) {

        byte idx = 6; // length of "/begin"
        final int lineSize = line.length();
        int sum = 0;

        do {
            idx += 1;
        } while (idx < lineSize && !Character.isJavaIdentifierStart(line.charAt(idx)));

        byte idx2 = idx;

        do {
            sum += (line.charAt(idx2) * (idx2 - idx));
            idx2 += 1;
        } while (idx2 < lineSize && Character.isJavaIdentifierStart(line.charAt(idx2)));

        return sum;
    }

    private final List<String> parseLineWithRegex(Pattern regexQuote, String line, BufferedReader buf) throws IOException {

        final List<String> listWord = new ArrayList<String>();

        final String lineWoutComment;
        String lineTmp;
        StringBuilder sb = new StringBuilder();

        if (line.indexOf("/*") > -1 || line.indexOf("*/") > -1 || line.indexOf("//") > -1) {
            lineWoutComment = ParserUtils.LINE_COMMENT.matcher(line).replaceAll("").trim();
            if (lineWoutComment.length() == 0) {
                return listWord;
            }
        } else {
            lineWoutComment = line;
        }

        if (lineWoutComment.charAt(0) == '"' && lineWoutComment.charAt(lineWoutComment.length() - 1) == '"'
                && ParserUtils.isUniqueString(lineWoutComment)) {
            listWord.add(lineWoutComment.substring(1, lineWoutComment.length() - 1));
            return listWord;
        } else if (lineWoutComment.charAt(0) == '"' && !ParserUtils.isEvenQuote(lineWoutComment)) {

            sb.append(lineWoutComment.substring(1));
            do {

                lineTmp = buf.readLine();

                if (lineTmp == null) {
                    break;
                } else if (lineTmp.trim().startsWith("/end")) {
                    break;
                }
                sb.append(" " + lineTmp.substring(0, lineTmp.length() - 1));
            } while (lineTmp.charAt(lineTmp.length() - 1) != '"');

            listWord.add(sb.toString());

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

    private final void assignLinkedObject(Map<Integer, String> defCharacteristic) {

        for (AdjustableObject adjustableObject : adjustableObjects.values()) {
            adjustableObject.assignComputMethod(compuMethods);
            adjustableObject.assignRecordLayout(recordLayouts);
            if (adjustableObject instanceof Characteristic) {
                ((Characteristic) adjustableObject).assignAxisPts(adjustableObjects);
            }
            adjustableObject.setFunction(defCharacteristic.get(adjustableObject.getID()));
        }

        for (CompuMethod compuMethod : compuMethods.values()) {
            compuMethod.assignConversionTable(conversionTables);
        }

        final int measurementSize = measurements.size();
        for (int i = 0; i < measurementSize; i++) {
            measurements.get(i).assignComputMethod(compuMethods);
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

        for (AdjustableObject adjustableObject : adjustableObjects.values()) {
            functionRef = adjustableObject.getFunction();
            if (functionRef != null && functionRef.equals(function)) {
                listByFunction.add(adjustableObject);
            }
        }

        Collections.sort(listByFunction);

        return listByFunction;
    }

    public Vector<String> getAdjustableObjectNameByFunction(String function) {

        final Vector<String> listNameByFunction = new Vector<String>();

        String functionRef;

        for (AdjustableObject adjustableObject : adjustableObjects.values()) {
            functionRef = adjustableObject.getFunction();
            if (functionRef != null && functionRef.equals(function)) {
                listNameByFunction.add(adjustableObject.name);
            }
        }

        Collections.sort(listNameByFunction);

        return listNameByFunction;
    }

    public Vector<AdjustableObject> getAdjustableObjectFromList(Vector<String> listNameAdjObj) {
        final Vector<AdjustableObject> listByName = new Vector<AdjustableObject>();

        for (String nameAdjObj : listNameAdjObj) {
            listByName.add(adjustableObjects.get(nameAdjObj.hashCode()));
        }

        Collections.sort(listByName);

        return listByName;
    }

    public static StringBuilder compareA2L(final File firstFile, final File secondFile) throws InterruptedException {

        final StringBuilder sb = new StringBuilder();

        long start = System.currentTimeMillis();

        final A2l first = new A2l();

        Thread threadA2l1 = new Thread(new Runnable() {

            @Override
            public void run() {
                first.parse(firstFile);
            }
        });

        threadA2l1.start();

        final A2l second = new A2l();

        Thread threadA2l2 = new Thread(new Runnable() {

            @Override
            public void run() {
                second.parse(secondFile);
            }
        });

        threadA2l2.start();

        threadA2l1.join();
        threadA2l2.join();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                Set<Integer> missingObjects = new HashSet<>(first.getAdjustableObjects().keySet());
                Set<Integer> newObjects = new HashSet<>(second.getAdjustableObjects().keySet());
                Set<Integer> compObjects = new HashSet<>(second.getAdjustableObjects().keySet());

                newObjects.removeAll(first.getAdjustableObjects().keySet());
                missingObjects.removeAll(second.getAdjustableObjects().keySet());
                compObjects.retainAll(first.getAdjustableObjects().keySet());

                HashMap<Integer, AdjustableObject> firstAdjObject = first.getAdjustableObjects();
                HashMap<Integer, AdjustableObject> secondAdjObject = second.getAdjustableObjects();

                AdjustableObject object1;
                AdjustableObject object2;

                sb.append("*** COMPARE REPORT ***\n");

                sb.append("\nMissing objects : " + "\n");

                for (Integer objectName : missingObjects) {
                    object1 = firstAdjObject.get(objectName);
                    sb.append(object1 + ", ");
                }

                sb.append("\n\nNew objects : " + "\n");

                for (Integer objectName : newObjects) {
                    object1 = secondAdjObject.get(objectName);
                    sb.append(object1 + ", ");
                }

                HashMap<String, String> diff;

                for (Integer objectId : compObjects) {
                    object1 = firstAdjObject.get(objectId);
                    object2 = secondAdjObject.get(objectId);

                    diff = AdjustableObject.compar(object1, object2);

                    if (diff.size() > 0) {
                        sb.append("\n\n" + object1.name + "\n");
                        sb.append(diff);
                    }

                }

                sb.append("\n\n*** END ***");

            }
        });

        thread.start();

        while (thread.isAlive()) {
        }

        System.out.println("Finished in = " + (System.currentTimeMillis() - start) + "ms");

        return sb;
    }

}
