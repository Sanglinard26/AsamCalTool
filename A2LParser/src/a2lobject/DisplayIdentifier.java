/*
 * Creation : 22 févr. 2019
 */
package a2lobject;

public final class DisplayIdentifier {

    private String ident;

    public DisplayIdentifier(String ident) {
        this.ident = ident;
    }

    public final String getInfo() {
        return this.ident;
    }

    @Override
    public String toString() {
        return getInfo();
    }

}
