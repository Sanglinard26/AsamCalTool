/*
 * Creation : 12 mars 2019
 */
package a2lobject;

import java.util.List;

public final class FixAxisParList {

    private float[] axisPtsValue;

    public FixAxisParList(List<String> parameters) {

    	axisPtsValue = new float[parameters.size()];
    	
        for (int n = 0; n < parameters.size(); n++) {
        	axisPtsValue[n] = Float.parseFloat(parameters.get(n));
        }
    }
}