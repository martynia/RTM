/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.layers;

/**
 * The class plays a buffer role between pulser thread and a site object;
 * The site object will look for its circle size in a CircleSize object, which
 * is used to create a pulser object. The pulser puts its calculations into
 * CircleSize.
 * @author Mikhail Khrypach
 */
public class CircleSize {

    private int size;
    private int delta = 1;

    public int get(){
        return size;
    }

    public void set(int size){
        this.size =size;
    }

    public int getD(){
        return delta;
    }

    public void setD(int delta){
        this.delta =delta;
    }
}
