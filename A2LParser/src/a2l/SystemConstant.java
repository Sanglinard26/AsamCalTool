/*
 * Creation : 8 avr. 2019
 */
package a2l;

import java.util.List;

public final class SystemConstant implements A2lObject, Comparable<SystemConstant> {

    private String name;
    private char[] value;

    public SystemConstant(List<String> parameters) {

        build(parameters, 0, 0);

    }

    public final String getName() {
        return this.name;
    }

    public final String getValue() {
        return new String(this.value);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(SystemConstant o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws A2lObjectParsingException {
        this.name = parameters.get(0);
        this.value = parameters.get(1).toCharArray();
    }

    @Override
    public String getProperties() {
        StringBuilder sb = new StringBuilder("<html><b><u>PROPERTIES :</u></b>");

        sb.append("<ul><li><b>Name: </b>" + name + "\n");
        sb.append("<li><b>Value: </b>" + getValue() + "\n");
        sb.append("</u></html>");

        return sb.toString();
    }

}
