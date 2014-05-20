/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.layers;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Cylinder;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Draw a cylinder.
 * @author martynia, based on NASA Cylider example
 */
public class RTMCylinder implements Runnable, Observer {

    private final double lat;
    private final double lon;
    private final double globeRadius;
    private double latShiftIN, latShiftOUT;
    private BasicShapeAttributes gattrs;
    private ShapeAttributes attrs;
    private BasicShapeAttributes emptydisk;
    private DataTransferSite.Rates rates;

    private void init() {
        // Create and set an attribute bundle.
        attrs = new BasicShapeAttributes();
        attrs.setInteriorMaterial(Material.YELLOW);
        attrs.setInteriorOpacity(0.95);
        attrs.setEnableLighting(true);
        attrs.setOutlineMaterial(Material.RED);
        attrs.setOutlineWidth(2d);
        attrs.setDrawInterior(true);
        attrs.setDrawOutline(false);

        //
        // glass around an opaque cylinder hides it completely
        gattrs = new BasicShapeAttributes();
        gattrs.setInteriorMaterial(Material.CYAN);
        gattrs.setInteriorOpacity(0.95);
        gattrs.setEnableLighting(true);
        gattrs.setOutlineMaterial(Material.RED);
        gattrs.setOutlineWidth(2d);
        gattrs.setDrawInterior(true);
        gattrs.setDrawOutline(false);

        emptydisk = new BasicShapeAttributes();
        emptydisk.setInteriorMaterial(Material.GRAY);
        emptydisk.setInteriorOpacity(0.95);
        emptydisk.setEnableLighting(true);
        emptydisk.setOutlineMaterial(Material.RED);
        emptydisk.setOutlineWidth(2d);
        emptydisk.setDrawInterior(true);
        emptydisk.setDrawOutline(false);
    }

    RTMCylinder(DataTransferSite site) {
        this.site=site;
        //super(Position.fromDegrees(lat, lon, 60000), 60000, rates.getRatesAsDoubles()[0]/500000., 60000, Angle.fromDegrees(0.), Angle.fromDegrees(90.), Angle.fromDegrees(0.));
        this.lat = site.getLatitude();
        this.lon = site.getLongitude();

        rates = site.getRates();

        in_size = size(rates.getRatesAsDoubles()[0]);
        out_size = size(rates.getRatesAsDoubles()[1]);

        // globe radius at this posision:
        globeRadius = RTM.RealTimeMonitor.getAf().getWwd().getModel().getGlobe().getRadiusAt(Position.fromDegrees(lat, lon));
        // initial cylinder shift in degrees
        latShiftIN = 180. / Math.PI * in_size / globeRadius;
        double radius = 12500;
        init();
        cylinderIN = new Cylinder(Position.fromDegrees(lat + latShiftIN, lon - 20. / 60., in_size), radius, Math.max(in_size,min_size), radius, Angle.fromDegrees(0.), Angle.fromDegrees(90.), Angle.fromDegrees(0.));
        cylinderIN.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        double glassRadius = radius;
        latShiftOUT = 180. / Math.PI * out_size / globeRadius;
        cylinderOUT = new Cylinder(Position.fromDegrees(lat+latShiftOUT, lon + 20. / 60., glassRadius), glassRadius, Math.max(out_size,mout_size), glassRadius, Angle.fromDegrees(0.), Angle.fromDegrees(90.), Angle.fromDegrees(0.));
        cylinderOUT.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        // we want to paint rate 0 cylinders gray
        if (rates.getRatesAsDoubles()[0] > 0.) {
            //cylinderIN.setImageSources("gov/nasa/worldwindx/examples/images/500px-Checkerboard_pattern.png");
            cylinderIN.setAttributes(attrs);
            cylinderIN.setVisible(true);
            cylinderIN.setValue(AVKey.DISPLAY_NAME, "Inbound Data Traffic:" + site.getName());
        } else {
            cylinderIN.setAttributes(emptydisk);
            cylinderIN.setVisible(true);
            cylinderIN.setValue(AVKey.DISPLAY_NAME, "No Inbound Data Traffic:" + site.getName());
        }
        if (rates.getRatesAsDoubles()[1] > 0.) {
            cylinderOUT.setAttributes(gattrs);
            cylinderOUT.setVisible(true);
            cylinderOUT.setValue(AVKey.DISPLAY_NAME, "Outbound Data Traffic:" + site.getName());

        } else {
            cylinderOUT.setAttributes(emptydisk);
            cylinderOUT.setVisible(true);
            cylinderOUT.setValue(AVKey.DISPLAY_NAME, "No Outbound Data Traffic:" + site.getName());
        }
        LayerList layers = RTM.RealTimeMonitor.getAf().getWwd().getModel().getLayers();
        RenderableLayer l = (RenderableLayer) layers.getLayerByName("FTS Disks");
        if (l != null) {
            l.addRenderable(cylinderIN);
            l.addRenderable(cylinderOUT);

        }
    }

    public void run() {

        if (hasChanged) {
            in_size = size(rates.getRatesAsDoubles()[0]);
            if (rates.getRatesAsDoubles()[0] > 0) {
                cylinderIN.setAttributes(attrs);
                cylinderIN.setValue(AVKey.DISPLAY_NAME, "Inbound Data Traffic:" + site.getName());
            } else {
                cylinderIN.setAttributes(emptydisk);
                cylinderIN.setVerticalRadius(in_size);
                cylinderIN.setValue(AVKey.DISPLAY_NAME, "No Inbound Data Traffic:" + site.getName());
            }
            out_size = size(rates.getRatesAsDoubles()[1]);
            if (rates.getRatesAsDoubles()[1] > 0) {
                cylinderOUT.setAttributes(gattrs);
                cylinderOUT.setValue(AVKey.DISPLAY_NAME, "Outbound Data Traffic:" + site.getName());
            } else {
                cylinderOUT.setAttributes(emptydisk);
                cylinderOUT.setVerticalRadius(out_size);
                cylinderOUT.setValue(AVKey.DISPLAY_NAME, "No Outbound Data Traffic:" + site.getName());
            }
            hasChanged = false;
        }

        int ncycles = 50;
        final double step = out_size / ncycles;
        final double step2 = in_size / ncycles;
        while (true) {
            referencePos = cylinderIN.getReferencePosition();
            if (referencePos == null) {
                Logger.getLogger(RTMCylinder.class.getName()).log(Level.WARNING, " got null ref point at this lat/lon " + lat + ":" + lon);
                continue;
            }

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    // rates are in bytes/s done_bytes are bytes transfered in a timebin (3600s)
                    // so done_bytes =  rate*3600 
                    // outgoing traffic
                    if (out_size > 0. && (rates.getRatesAsDoubles()[1]) > 0) {
                        
                        double size = cylinderOUT.getVerticalRadius();
                        double nsize = size - step;
                        latShiftOUT = 180. / Math.PI * nsize / globeRadius;
                        //System.out.println("old size " + size + " new size " + nsize + " zoom " + zoom() + " lat shift " + latShiftOUT);
                        if (nsize <= 0.01) {
                            nsize = out_size; // reset the size to the initial value
                        }
                        cylinderOUT.setVerticalRadius(nsize);
                        cylinderOUT.moveTo(Position.fromDegrees(lat + latShiftOUT, lon + 20. / 60., referencePos.getElevation()));
                    }
                    if (in_size > 0 && rates.getRatesAsDoubles()[0] > 0) {
                        // incoming traffic
                        
                        double size2 = Math.max(cylinderIN.getVerticalRadius(), 1);
                        double nsize2 = size2 + step2;
                        latShiftIN = 180. / Math.PI * nsize2 / globeRadius;
                        if (nsize2 > in_size) {
                            nsize2 = 1.;
                        }
                        cylinderIN.setVerticalRadius(nsize2);
                        cylinderIN.moveTo(Position.fromDegrees(lat + latShiftIN, lon - 20. / 60., referencePos.getElevation()));
                    }
                }
            });

            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(RTMCylinder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is called when rates change.
     * @param o - an object which reports about the changes. 
     * @param rates - actual set of (new) rates.
     */
    public void update(Observable o, Object rates) {
        this.rates = (DataTransferSite.Rates) rates;
        hasChanged = true;
    }

    private double zoom() {
        return Math.sqrt(((BasicOrbitView) RTM.RealTimeMonitor.getAf().getWwd().getView()).getZoom());
    }

    private double size(double rate) {
        double scale_factor = Math.log10(rate);
        scale_factor = (scale_factor > 1) ? scale_factor : 1;
        return scale_factor * zoom();
    }
    private Cylinder cylinderIN, cylinderOUT, glassIN, glassOUT;
    private Position referencePos;
    private double min_size = 1.;
    private double mout_size = 1.;
    private double in_size = 0.;
    private double out_size = 0.;
    private boolean hasChanged = false;
    private DataTransferSite site;
}
