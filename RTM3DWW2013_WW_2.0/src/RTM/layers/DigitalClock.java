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
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Implements a digital clock as a ScreenAnnotation.
 * @author Janusz Martyniak
 */
public class DigitalClock extends GenericCounter {

    /**
     * 
     * @param text
     * @param position
     * @param defaults
     */
    public DigitalClock(String text, Point position, AnnotationAttributes defaults) {
        super(text, position, defaults);
        this.setText(text + getFormattedTimeString());
    }

    /**
     * 
     * @param text
     * @param position
     */
    public DigitalClock(String text, Point position) {
        super(text, position);
        init();
        this.setText(text + getFormattedTimeString());
    }

    /**
     * 
     * @param text
     * @param position
     * @param font
     */
    public DigitalClock(String text, Point position, Font font) {
        super(text, position, font);
        init();
        this.setText(text + getFormattedTimeString());
    }

    /**
     * 
     * @param text
     * @param position
     * @param font
     * @param textColor
     */
    public DigitalClock(String text, Point position, Font font, Color textColor) {
        super(text, position, font, textColor);
        init();
        this.setText(text + getFormattedTimeString());
    }

    /**
     * 
     * @return
     */
    public String getCounter() {
        return getFormattedTimeString();
    }

    private String getFormattedTimeString() {
        return timeFormat.format(Calendar.getInstance().getTimeInMillis() - 180000) + " UTC";
    }
    private void init() {

        getAttributes().setTextColor(Color.red);
        getAttributes().setFont(Font.decode("Arial-BOLDITALIC-18"));
        getAttributes().setCornerRadius(0);
        getAttributes().setSize(new Dimension(200, 0));
        getAttributes().setAdjustWidthToText(Annotation.SIZE_FIT_TEXT); // use flexible dimension width - 200
        getAttributes().setDrawOffset(new Point(75, 0)); // screen point is annotation bottom left corner
        getAttributes().setHighlightScale(1);             // No highlighting either

    }
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * 
     * @param count
     */
    public void setCounter(String count) {
        throw new UnsupportedOperationException("Not supported for the clock!.");
    }
}
