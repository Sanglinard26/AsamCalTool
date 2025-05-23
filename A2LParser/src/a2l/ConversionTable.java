/*
 * Creation : 31 mars 2019
 */
package a2l;

import java.util.Map;

import constante.ConversionType;

public abstract class ConversionTable implements A2lObject, Comparable<ConversionTable> {

    protected String name;
    protected char[] longIdentifier;
    protected ConversionType conversionType;

    @Override
    public final int compareTo(ConversionTable o) {
        return this.name.compareToIgnoreCase(o.toString());
    }

    abstract Map<?, ?> getMap();

    @Override
    public String toString() {
        return this.name;
    }

}
