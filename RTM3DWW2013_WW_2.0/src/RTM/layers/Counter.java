/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.layers;

/**
 * A simple counter interface to get and set a value as a string.
 * @author Janusz Martyniak
 */
public interface Counter {
    /**
     * Return value of the counter as  String.
     * @return
     */
    public String getCounter();
  /**
   * Sets the value of the counter.
   * @param count
   */
  public void setCounter(String count);
}
