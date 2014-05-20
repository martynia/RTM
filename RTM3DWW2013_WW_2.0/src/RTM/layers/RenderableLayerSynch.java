/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package RTM.layers;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.Logging;
import javax.media.opengl.GL2;

/**
 * The <code>RenderableLayerSynch</code> class manages a collection of {@link gov.nasa.worldwind.render.Renderable} objects
 * for rendering, picking, and disposal.
 *
 * @author tag
 * @version $Id: RenderableLayerSynch.java 1 2011-07-16 23:22:47Z dcollins $
 * @see gov.nasa.worldwind.render.Renderable
 * RTM: We inherit from {@link gov.nasa.worldwind.layers.RenderableLayer} and synchronize doRender/doPrerender methods to
 * allow concurrent modification of renderables in the RTM
 * GL2 fix for RTM 2.0 GL 2.0 (in doPick() ) JM Wed 19 Jul 2013
 */
public class RenderableLayerSynch extends RenderableLayer
{
    private java.util.Collection<Renderable> renderables = new java.util.concurrent.ConcurrentLinkedQueue<Renderable>();
    private Iterable<Renderable> renderablesOverride;
    protected PickSupport pickSupport = new PickSupport();

    /** Creates a new <code>RenderableLayerSynch</code> with a null <code>delegateOwner</code> */
    public RenderableLayerSynch()
    {
    }
    protected void doPreRender(DrawContext dc, Iterable<? extends Renderable> renderables)
    {
        synchronized (renderables) {
        for (Renderable renderable : renderables)
        {
            try
            {
                // If the caller has specified their own Iterable,
                // then we cannot make any guarantees about its contents.
                if (renderable != null && renderable instanceof PreRenderable)
                    ((PreRenderable) renderable).preRender(dc);
            }
            catch (Exception e)
            {
                String msg = Logging.getMessage("generic.ExceptionWhilePrerenderingRenderable");
                Logging.logger().severe(msg);
                // continue to next renderable
            }
        }
        }
    }

    protected void doPick(DrawContext dc, Iterable<? extends Renderable> renderables, java.awt.Point pickPoint)
    {
        GL2 gl = dc.getGL().getGL2(); // GL initialization checks for GL2 compatibility.
        this.pickSupport.clearPickList();
        this.pickSupport.beginPicking(dc);

        try
        {
            for (Renderable renderable : renderables)
            {
                // If the caller has specified their own Iterable,
                // then we cannot make any guarantees about its contents.
                if (renderable != null)
                {
//                    float[] inColor = new float[4];
//                    dc.getGL().glGetFloatv(GL2.GL_CURRENT_COLOR, inColor, 0);
                    java.awt.Color color = dc.getUniquePickColor();
                    gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

                    try
                    {
                        renderable.render(dc);
                    }
                    catch (Exception e)
                    {
                        String msg = Logging.getMessage("generic.ExceptionWhilePickingRenderable");
                        Logging.logger().severe(msg);
                        Logging.logger().log(java.util.logging.Level.FINER, msg, e); // show exception for this level
                        continue; // go on to next renderable
                    }
//
//                    dc.getGL().glColor4fv(inColor, 0);

                    if (renderable instanceof Locatable)
                    {
                        this.pickSupport.addPickableObject(color.getRGB(), renderable,
                            ((Locatable) renderable).getPosition(), false);
                    }
                    else
                    {
                        this.pickSupport.addPickableObject(color.getRGB(), renderable);
                    }
                }
            }

            this.pickSupport.resolvePick(dc, pickPoint, this);
        }
        finally
        {
            this.pickSupport.endPicking(dc);
        }
    }

    protected void doRender(DrawContext dc, Iterable<? extends Renderable> renderables)
    {
        synchronized (renderables) {
        for (Renderable renderable : renderables)
        {
            try
            {
                // If the caller has specified their own Iterable,
                // then we cannot make any guarantees about its contents.
                if (renderable != null)
                    renderable.render(dc);
            }
            catch (Exception e)
            {
                String msg = Logging.getMessage("generic.ExceptionWhileRenderingRenderable");
                Logging.logger().log(java.util.logging.Level.SEVERE, msg, e);
                // continue to next renderable
            }
        }
        }
    }
}
