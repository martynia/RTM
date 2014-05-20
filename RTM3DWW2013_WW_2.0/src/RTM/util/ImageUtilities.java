/*
 * To changraphics2De this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author martynia
 */
public class ImageUtilities {
    public static BufferedImage resizeImage(final BufferedImage originalImage, int width, int height) {
        int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        final BufferedImage bufferedImage = new BufferedImage(width, height, type);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
	RenderingHints.VALUE_RENDER_QUALITY);
	graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(originalImage, 0, 0, width, height, null);
        graphics2D.dispose();
 
        return bufferedImage;
    }
}
