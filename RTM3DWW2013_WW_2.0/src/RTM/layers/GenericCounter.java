/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.layers;

import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ScreenAnnotation;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

/**
 * A ScreenAnnotation based generic counter. The counts are provided via {@link RTM.layers.Counter} interface.
 * @author Janusz Martyniak
 */
public abstract class GenericCounter extends ScreenAnnotation implements Counter{
  
    /**
     * 
     * @param text
     * @param position
     * @param defaults
     */
    public GenericCounter(String text, Point position, AnnotationAttributes defaults){
          super(text, position, defaults);
  }
  /**
   * 
   * @param text
   * @param position
   */
  public GenericCounter(String text, Point position){
          super(text, position);
          init();
  }
  /**
   * 
   * @param text
   * @param position
   * @param font
   */
  public GenericCounter(String text, Point position, Font font){
          super(text, position, font);
          init();

  }
  /**
   * 
   * @param text
   * @param position
   * @param font
   * @param textColor
   */
  public GenericCounter(String text, Point position, Font font, Color textColor){
          super(text, position, font, textColor);
          init();

  }

  /**
   * 
   * @param dc
   */
  public void render(DrawContext dc) {
      this.setText(getCounter());
      super.render(dc);

  }
  private void init() {
        AnnotationAttributes defaultAttributes = new AnnotationAttributes();
        defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, .5f));
        getAttributes().setDefaults(defaultAttributes);
  }
}
