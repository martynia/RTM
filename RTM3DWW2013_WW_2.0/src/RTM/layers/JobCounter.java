/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.layers;

import gov.nasa.worldwind.render.Annotation;
import gov.nasa.worldwind.render.AnnotationAttributes;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Janusz Martyniak
 */
public class JobCounter extends GenericCounter implements Observer{

    public JobCounter(String text, Point position, AnnotationAttributes defaults) {
        super(text, position, defaults);
        this.setText(text + getCounter());
        this.text=text;
    }

    public JobCounter(String text, Point position) {
        super(text, position);
        init();
        this.setText(text + getCounter());
        this.text=text;
    }

    public JobCounter(String text, Point position, Font font) {
        super(text, position, font);
        this.setText(text + getCounter());
        init();
        this.text=text;
    }

    public JobCounter(String text, Point position, Font font, Color textColor) {
        super(text, position, font, textColor);
        this.setText(text + getCounter());
        init();
        this.text=text;

    }
    /**
     * Called by render in the suprclass. The text label is placed in front of the count.
     * @return counter value as String
     */
    public String getCounter() {
        return counter;
    }

    private void init() {
        
        getAttributes().setTextColor(Color.red);
        getAttributes().setFont(Font.decode("Arial-BOLDITALIC-18"));
        getAttributes().setCornerRadius(0);
        getAttributes().setSize(new Dimension(400, 0));
        getAttributes().setAdjustWidthToText(Annotation.SIZE_FIT_TEXT); // use flexible dimension width - 200
        getAttributes().setDrawOffset(new Point(100, 0)); // screen point is annotation bottom left corner
        getAttributes().setHighlightScale(1);             // No highlighting either
        
    }

    public void setCounter(String count) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update(Observable arg0, Object arg1) {
        /* JobStats sends us a string .. */
        counter=arg1.toString();
    }
    private String counter = "0";
    private String text="";
}
