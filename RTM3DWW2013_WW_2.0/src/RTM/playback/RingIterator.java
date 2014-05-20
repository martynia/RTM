/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.playback;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Circular List Iterator . Implementation of the iterator inspired by a StackOverflow discussion
 * @author Janusz Martyniak
 */
class RingIterator<T> implements Iterator<T> {

    private int cur = 0;
    private RingList<T> coll = null;

    public RingIterator(RingList<T> l) {
        coll = l;
    }

    public boolean hasNext() {
        return coll.size() > 0;
    }
    /**
     * 
     * @return Next element in the list. If the end is reached returns the first element again
     * @throws NoSuchElementException 
     */
    public T next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        int i = cur++;
        cur = cur % coll.size();
        return coll.get(i);
    }
    /**
     * Removing of elements not supported.
     */
    public void remove() {
        throw new UnsupportedOperationException("Operation not supported.");
    }
}
