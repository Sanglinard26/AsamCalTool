/*
 * Creation : 14 janv. 2020
 */
package a2l;

import java.util.EventListener;

public interface A2lStateListener extends EventListener {

    void stateChange(String state);

}
