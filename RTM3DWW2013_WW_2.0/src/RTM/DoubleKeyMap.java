/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM;

import java.util.Hashtable;

/**
 * A Map with 2 String keys.
 * @author Janusz Martyniak
 */
public class DoubleKeyMap<T> {

    /**
     * Store a value identified by two Strings in the Hashtable.
     * @param key1 first String key
     * @param key2 second String key
     * @param value  value stored
     */
    public void put(String key1, String key2, T value) {
        holder.put(new TwinString(key1, key2), value);
    }

    /**
     * Retrieve a value identified by two Strings from the Hashtable.
     * @param key1 first String key
     * @param key2 second String key
     * @return the value from the Hashtable
     */
    public T get(String key1, String key2) {
        return holder.get(new TwinString(key1, key2));
    }
    /**
     *
     */
    public void clear() {
        holder.clear();
    }
    private Hashtable<TwinString, T> holder = new Hashtable<TwinString, T>();

    /**
     * A "twin String object" with overriden equals and hashCode to be used as
     * a Map key.
     * @author Janusz Martyniak
     */
    class TwinString {

        public TwinString(String s1, String s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        public TwinString() {
        }

        /**
         * Only 2 not null identical Strings make it equal.
         * @param obj
         * @return
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TwinString other = (TwinString) obj;
            if ((this.s1 == null) ? (other.s1 != null) : !this.s1.equals(other.s1)) {
                return false;
            }
            if ((this.s2 == null) ? (other.s2 != null) : !this.s2.equals(other.s2)) {
                return false;
            }
            return true;
        }

        /**
         * hashCode that combines two strings. Order matters.
         * @return a hash code value on the pair of strings.
         */
        @Override
        public int hashCode() {
            /* from http://mindprod.com/jgloss/hashcode.html */
            int result = s1.hashCode();
            result = 37 * result + s2.hashCode();
            return result;
        }
        private String s1 = "", s2 = "";
    }
}
