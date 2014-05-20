/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.exceptions;

/**
 *
 * @author Janusz Martyniak
 */
public class BadCoordinatesException extends Exception {

    private static final long serialVersionUID = 426204280515L;

    public BadCoordinatesException(String string) {
        super(string);
    }
}
