/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
 */
package gov.nasa.worldwind.examples;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.render.Cylinder;

import gov.nasa.worldwindx.examples.ApplicationTemplate;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Illustrates how to use the World Wind <code>{@link Cylinder}</code> rigid shape to display an arbitrarily sized and
 * oriented cylinder at a geographic position on the Globe.
 *
 * @author ccrick
 * @version $Id: Cylinders.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class Cylinders1 extends ApplicationTemplate {

    public static class AppFrame extends ApplicationTemplate.AppFrame {

        public AppFrame() {
            // Add detail hint slider panel
            this.getLayerPanel().add(this.makeDetailHintControlPanel(), BorderLayout.SOUTH);

            RenderableLayer layer = new RenderableLayer();

            // Create and set an attribute bundle.
            ShapeAttributes attrs = new BasicShapeAttributes();
            attrs.setInteriorMaterial(Material.YELLOW);
            attrs.setInteriorOpacity(0.7);
            attrs.setEnableLighting(true);
            attrs.setOutlineMaterial(Material.RED);
            attrs.setOutlineWidth(2d);
            attrs.setDrawInterior(true);
            attrs.setDrawOutline(false);

            // Create and set an attribute bundle.
            ShapeAttributes attrs2 = new BasicShapeAttributes();
            attrs2.setInteriorMaterial(Material.PINK);
            attrs2.setInteriorOpacity(1);
            attrs2.setEnableLighting(true);
            attrs2.setOutlineMaterial(Material.WHITE);
            attrs2.setOutlineWidth(2d);
            attrs2.setDrawOutline(false);

            // ********* sample  Cylinders  *******************

            // Cylinder with equal axes, ABSOLUTE altitude mode
            Cylinder cylinder3 = new Cylinder(Position.fromDegrees(40, -120, 80000), 100000, 50000);
            cylinder3.setAltitudeMode(WorldWind.ABSOLUTE);
            cylinder3.setAttributes(attrs);
            cylinder3.setVisible(true);
            cylinder3.setValue(AVKey.DISPLAY_NAME, "Cylinder with equal axes, ABSOLUTE altitude mode");
            layer.addRenderable(cylinder3);

            // Cylinder with equal axes, RELATIVE_TO_GROUND
            Cylinder cylinder4 = new Cylinder(Position.fromDegrees(37.5, -115, 50000), 50000, 50000, 50000);
            cylinder4.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            cylinder4.setAttributes(attrs);
            cylinder4.setVisible(true);
            cylinder4.setValue(AVKey.DISPLAY_NAME, "Cylinder with equal axes, RELATIVE_TO_GROUND altitude mode");
            layer.addRenderable(cylinder4);

            // Cylinder with equal axes, CLAMP_TO_GROUND
            Cylinder cylinder5 = new Cylinder(Position.fromDegrees(35, -110, 50000), 50000, 50000, 50000);
            cylinder5.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
            cylinder5.setAttributes(attrs);
            cylinder5.setVisible(true);
            cylinder5.setValue(AVKey.DISPLAY_NAME, "Cylinder with equal axes, CLAMP_TO_GROUND altitude mode");
            layer.addRenderable(cylinder5);

            // Cylinder with a texture, using Cylinder(position, height, radius) constructor
            //Cylinder cylinder9 = new Cylinder(Position.fromDegrees(0, -90, 600000), 1200000, 600000);
            MyCylinder cylinder9 = new MyCylinder(Position.fromDegrees(0, -90, 600000), 600000, 1200000, 600000, Angle.fromDegrees(0.), Angle.fromDegrees(90.), Angle.fromDegrees(0.));
            cylinder9.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            cylinder9.setImageSources("gov/nasa/worldwindx/examples/images/500px-Checkerboard_pattern.png");
            cylinder9.setAttributes(attrs);
            cylinder9.setVisible(true);
            cylinder9.setValue(AVKey.DISPLAY_NAME, "Cylinder with a texture");
            layer.addRenderable(cylinder9);



            // Scaled Cylinder with default orientation
            Cylinder cylinder = new Cylinder(Position.ZERO, 1000000, 500000, 100000);
            cylinder.setAltitudeMode(WorldWind.ABSOLUTE);
            cylinder.setAttributes(attrs);
            cylinder.setVisible(true);
            cylinder.setValue(AVKey.DISPLAY_NAME, "Scaled Cylinder with default orientation");
            layer.addRenderable(cylinder);

            // Scaled Cylinder with a pre-set orientation
            Cylinder cylinder2 = new Cylinder(Position.fromDegrees(0, 30, 750000), 1000000, 500000, 100000,
                    Angle.fromDegrees(90), Angle.fromDegrees(45), Angle.fromDegrees(30));
            cylinder2.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            cylinder2.setAttributes(attrs2);
            cylinder2.setValue(AVKey.DISPLAY_NAME, "Scaled Cylinder with a pre-set orientation");
            cylinder2.setVisible(true);

            layer.addRenderable(cylinder2);

            // Scaled Cylinder with a pre-set orientation
            Cylinder cylinder6 = new Cylinder(Position.fromDegrees(30, 30, 750000), 1000000, 500000, 100000,
                    Angle.fromDegrees(90), Angle.fromDegrees(45), Angle.fromDegrees(30));
            cylinder6.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            cylinder6.setImageSources("gov/nasa/worldwindx/examples/images/500px-Checkerboard_pattern.png");
            cylinder6.setAttributes(attrs2);
            cylinder6.setVisible(true);
            cylinder6.setValue(AVKey.DISPLAY_NAME, "Scaled Cylinder with a pre-set orientation");
            layer.addRenderable(cylinder6);

            // Scaled Cylinder with a pre-set orientation
            Cylinder cylinder7 = new Cylinder(Position.fromDegrees(60, 30, 750000), 1000000, 500000, 100000,
                    Angle.fromDegrees(90), Angle.fromDegrees(45), Angle.fromDegrees(30));
            cylinder7.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            cylinder7.setAttributes(attrs2);
            cylinder7.setVisible(true);
            cylinder7.setValue(AVKey.DISPLAY_NAME, "Scaled Cylinder with a pre-set orientation");
            layer.addRenderable(cylinder7);

            // Scaled, oriented Cylinder in 3rd "quadrant" (-X, -Y, -Z)
            Cylinder cylinder8 = new Cylinder(Position.fromDegrees(-45, -180, 750000), 1000000, 500000, 100000,
                    Angle.fromDegrees(90), Angle.fromDegrees(45), Angle.fromDegrees(30));
            cylinder8.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            cylinder8.setAttributes(attrs2);
            cylinder8.setVisible(true);
            cylinder8.setValue(AVKey.DISPLAY_NAME, "Scaled, oriented Cylinder in the 3rd 'quadrant' (-X, -Y, -Z)");
            layer.addRenderable(cylinder8);

            // Add the layer to the model.
            insertBeforeCompass(getWwd(), layer);
            // Update layer panel
            this.getLayerPanel().update(this.getWwd());

            Thread t = new Thread(cylinder9);
            t.start();
        }

        protected JPanel makeDetailHintControlPanel() {
            JPanel controlPanel = new JPanel(new BorderLayout(0, 10));
            controlPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9),
                    new TitledBorder("Detail Hint")));

            JPanel detailHintSliderPanel = new JPanel(new BorderLayout(0, 5));
            {
                int MIN = -10;
                int MAX = 10;
                int cur = 0;
                JSlider slider = new JSlider(MIN, MAX, cur);
                slider.setMajorTickSpacing(10);
                slider.setMinorTickSpacing(1);
                slider.setPaintTicks(true);
                Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
                labelTable.put(-10, new JLabel("-1.0"));
                labelTable.put(0, new JLabel("0.0"));
                labelTable.put(10, new JLabel("1.0"));
                slider.setLabelTable(labelTable);
                slider.setPaintLabels(true);
                slider.addChangeListener(new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        double hint = ((JSlider) e.getSource()).getValue() / 10d;
                        setCylinderDetailHint(hint);
                        getWwd().redraw();
                    }
                });
                detailHintSliderPanel.add(slider, BorderLayout.SOUTH);
            }

            JPanel sliderPanel = new JPanel(new GridLayout(2, 0));
            sliderPanel.add(detailHintSliderPanel);

            controlPanel.add(sliderPanel, BorderLayout.SOUTH);
            return controlPanel;
        }

        protected RenderableLayer getLayer() {
            for (Layer layer : getWwd().getModel().getLayers()) {
                if (layer.getName().contains("Renderable")) {
                    return (RenderableLayer) layer;
                }
            }

            return null;
        }

        protected void setCylinderDetailHint(double hint) {
            for (Renderable renderable : getLayer().getRenderables()) {
                Cylinder current = (Cylinder) renderable;
                current.setDetailHint(hint);
            }
            System.out.println("cylinder detail hint set to " + hint);
        }
    }

    public static void main(String[] args) {
        ApplicationTemplate.start("World Wind Cylinders", AppFrame.class);
    }
}

class MyCylinder extends Cylinder implements Runnable {

    MyCylinder(Position fromDegrees, int i, int i0, int i1, Angle fromDegrees0, Angle fromDegrees1, Angle fromDegrees2) {
        super(fromDegrees, i, i0, i1, fromDegrees0, fromDegrees1, fromDegrees2);
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MyCylinder.class.getName()).log(Level.SEVERE, null, ex);
            }

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    Position referencePos = getReferencePosition();
                    if (referencePos == null) {
                        System.out.println(" got null ref point ");
                        return;
                    }
                    double size = getVerticalRadius();
                    double nsize = size + 0.6 * size * Math.sin(size);
                    System.out.println("old size " + size + " new size " + nsize);
                    // if (nsize > 0.) {
                    setVerticalRadius(nsize);
                }
            });
        }   

    }
}