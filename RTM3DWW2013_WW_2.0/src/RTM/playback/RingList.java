/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.playback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Janusz Martyniak
 */
public class RingList<E> extends ArrayList<E>{

    public RingList(List<E> l) {
        super(l);
    }
    @Override
    public Iterator<E> iterator() {
        return new RingIterator<E>(this);
    }
    @Override
    public String toString() {
        StringBuilder buf=new StringBuilder();
        for(int i = 0; i < size(); i++) {
            buf.append(get(i).toString()).append('\n');
        }
        return buf.toString();
    }
}
