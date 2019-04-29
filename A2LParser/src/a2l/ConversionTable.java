/*
 * Creation : 31 mars 2019
 */
package a2l;

import constante.ConversionType;

public abstract class ConversionTable implements A2lObject, Comparable<ConversionTable> {

	protected String name;
	protected String longIdentifier;
	protected ConversionType conversionType;

	@Override
	public final int compareTo(ConversionTable o) {
		return this.name.compareToIgnoreCase(o.toString());
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
