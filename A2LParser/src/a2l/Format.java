/*
 * Creation : 10 oct. 2020
 */
package a2l;

public final class Format {

    private byte overallLength;
    private byte decimalesPlace;

    public Format(String txtFormat) {

        int idxDot = txtFormat.indexOf('.');
        if (idxDot > -1) {
            if (txtFormat.charAt(idxDot - 1) != '%') {
                if (idxDot > 1) {
                    this.overallLength = Byte.parseByte(txtFormat.substring(1, idxDot));
                } else {
                    this.overallLength = Byte.parseByte(txtFormat.substring(0, idxDot));
                }

            } else {
                this.overallLength = -1;
            }

            this.decimalesPlace = Byte.parseByte(txtFormat.substring(idxDot + 1));
        } else {
            this.overallLength = 16;
            this.decimalesPlace = 16;
        }
    }

    public final byte getOverallLength() {
        return overallLength;
    }

    public final byte getDecimalesPlace() {
        return decimalesPlace;
    }

    @Override
    public String toString() {
        return this.overallLength != -1 ? "%" + this.overallLength + "." + this.decimalesPlace : "%." + this.decimalesPlace;
    }

}
