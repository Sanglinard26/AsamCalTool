/*
 * Creation : 24 avr. 2019
 */
package a2l;

public final class A2lObjectParsingException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public A2lObjectParsingException(String message, int beginLine, int endLine) {
        super(String.format("%s between line %d and %d", message, beginLine, endLine));
    }

}
