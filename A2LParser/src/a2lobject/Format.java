/*
 * Creation : 24 févr. 2019
 */
package a2lobject;

public final class Format {

    private final String txtFormat;
    private int overallLength;
    private int decimalesPlace;

    public Format(String txtFormat) {
        this.txtFormat = txtFormat;

        int idxDot = this.txtFormat.indexOf('.');
        if (idxDot > -1) {
            this.overallLength = Integer.parseInt(this.txtFormat.substring(1, idxDot));
            this.decimalesPlace = Integer.parseInt(this.txtFormat.substring(idxDot + 1));
        }
    }

    public final int getOverallLength() {
        return overallLength;
    }

    public final int getDecimalesPlace() {
        return decimalesPlace;
    }

    @Override
    public String toString() {
        return this.txtFormat;
    }

}
