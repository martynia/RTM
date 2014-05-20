/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.util;

/**
 *
 * @author martynia
 */
public class StringUtils {
  public static String chop(String o, String t) {
        if (o.endsWith(t)) {
            int index = o.lastIndexOf(t);
            String s = o.substring(0, index);
            return s;
        } else {
            return o;
        }
    } 
}
