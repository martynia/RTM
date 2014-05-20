package RTM.layers;

import RTM.RealTimeMonitor;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import javax.media.opengl.GL2;

/**
 * A Transfer class represents a job transfer between an RB/WMS and a CE. A 3D arc is drawn.
 * 
 * @author Gidon
 */
final public class Transfer implements Renderable {

    // private double duration = 5000 ; // duration of transfer in milliseconds
    /**
     * 
     */
    protected String name = null;
    /**
     * 
     */
    protected String type = null;
    /**
     * 
     */
    protected double start_latitude = 0;
    /**
     * 
     */
    protected double start_longitude = 0;
    /**
     * 
     */
    protected double finish_latitude = 0;
    /**
     * 
     */
    protected double finish_longitude = 0;
    private int line_point = 0;
    /**
     * 
     */
    protected double[][] line_coordinates = null;
    /**
     * 
     */
    protected double[][] bad_line_coordinates = null;
    private int delta = 0;
    // protected double epochStart = 0 ;
    /**
     * 
     */
    protected boolean displayed = false;
    /**
     * 
     */
    protected boolean finished = false;
    /**
     * 
     */
    protected double zoom = 0;
    private boolean drawn;

    /**
     * 
     * @param name
     * @param type
     * @param start_latitude
     * @param start_longitude
     * @param finish_latitude
     * @param finish_longitude
     */
    public Transfer(String name, String type,
            double start_latitude,
            double start_longitude,
            double finish_latitude,
            double finish_longitude) {
        this.name = name;
        this.type = type;
        this.start_latitude = start_latitude;
        this.start_longitude = start_longitude;
        this.finish_latitude = finish_latitude;
        this.finish_longitude = finish_longitude;
    }

    /**
     * 
     */
    public void addDrawInfo() {

        this.calculateCoords();
        //this.calculateZoom() ;
        displayed = true;
    }

    /**
     * 
     * @return
     */
    public boolean isDisplayed() {
        return displayed;
    }

    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if finished, false otherwise. Called by {@link RealTimeMonitor.Redraw}.
     * @return true or false.
     */
    public boolean isFinished() {
        return finished;
    }

    private void calculateZoom() {
        //zoom = Math.sqrt( wwd.getModel().getViewPosition().getElevation() / 500000 ) ;
        //zoom = Math.sqrt(wwd.getCurrentPosition().getElevation() / 500000);
        zoom = Math.sqrt(((BasicOrbitView) RealTimeMonitor.getAf().getWwd().getView()).getZoom() / 500000);
        //zoom = (Math.sqrt(wwd.getModel().getGlobe().getElevation(Angle.fromDegrees(start_latitude), Angle.fromDegrees(start_longitude)) / 50000) + Math.sqrt(wwd.getModel().getGlobe().getElevation(Angle.fromDegrees(finish_latitude), Angle.fromDegrees(finish_longitude)) / 50000)) / 2;
    }

    private void calculateCoords() {

        int intermediate_points = (int) (Math.sqrt((finish_latitude - start_latitude) * (finish_latitude - start_latitude) + (finish_longitude - start_longitude) * (finish_longitude - start_longitude)) / 0.1);

        delta = intermediate_points / 50;
        if (delta < 1) {
            delta = 1;
        }

        line_coordinates = new double[intermediate_points + 1][3];

        Vec4 ps = RealTimeMonitor.getAf().getWwd().getModel().getGlobe().computePointFromPosition(new Position(Angle.fromDegrees(start_latitude), Angle.fromDegrees(start_longitude), 10000.0));
        double x_start = ps.x;
        double y_start = ps.y;
        double z_start = ps.z;
        double radius = Math.sqrt(x_start * x_start + y_start * y_start + z_start * z_start);
        Vec4 pf = RealTimeMonitor.getAf().getWwd().getModel().getGlobe().computePointFromPosition(new Position(Angle.fromDegrees(finish_latitude), Angle.fromDegrees(finish_longitude), 10000.0));
        double x_finish = pf.x;
        double y_finish = pf.y;
        double z_finish = pf.z;

        double x_step = (x_finish - x_start) / (float) intermediate_points;
        double y_step = (y_finish - y_start) / (float) intermediate_points;
        double z_step = (z_finish - z_start) / (float) intermediate_points;
        for (int i = 0; i <= intermediate_points; i++) {
            double x = x_start + (i * x_step);
            double y = y_start + (i * y_step);
            double z = z_start + (i * z_step);
            double height = Math.sqrt(x * x + y * y + z * z);
            line_coordinates[i][0] = x * radius / height;
            line_coordinates[i][1] = y * radius / height;
            line_coordinates[i][2] = z * radius / height;
        }

    }

    /**
     * Draw transfers.
     * @param dc
     */
    public void render(DrawContext dc) {

        addDrawInfo();
        this.calculateZoom();

        GL2 gl = dc.getGL().getGL2();
        // red, green, blue, alpha
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        if (type.equals("s")) {
            // s == Scheduled (magenta)
            gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
        } else {
            // d == Done (yellow)
            if (type.equals("d")) {
                gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            } else {
                if (type.equals("a")) {
                    gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                }
            }
        }

        gl.glDisable(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_TEXTURE_2D);

        // enable polygon antialiasing
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glLineWidth(2.0f);

        gl.glBegin(GL2.GL_LINE_STRIP);
        for (int i = 0; i < line_coordinates.length; i++) {
            gl.glVertex3d(line_coordinates[i][0], line_coordinates[i][1], line_coordinates[i][2]);
        }
        gl.glEnd();
        gl.glPopAttrib();


        double size = zoom * 10000;
        double none = 0;

        if (line_point < line_coordinates.length) {
            //while(line_point < line_coordinates.length){


            /*
            GLU glu = drawable.getGLU() ;
            GLUquadric q = glu.gluNewQuadric() ;
            glu.gluSphere( q , 0.35f, 32, 16) ;
             */

            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(line_coordinates[line_point][0] + size, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + size, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + size);
            gl.glEnd();
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + size, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] - size, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + size);
            gl.glEnd();
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(line_coordinates[line_point][0] - size, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] - size, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + size);
            gl.glEnd();
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] - size, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + size, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + size);
            gl.glEnd();
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + size, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + size, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] - size);
            gl.glEnd();
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(line_coordinates[line_point][0] + size, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] - size, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] - size);
            gl.glEnd();
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] - size, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] - size, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] - size);
            gl.glEnd();
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(line_coordinates[line_point][0] - size, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + size, line_coordinates[line_point][2] + none);
            gl.glVertex3d(line_coordinates[line_point][0] + none, line_coordinates[line_point][1] + none, line_coordinates[line_point][2] - size);
            gl.glEnd();

            line_point = line_point + delta;
            //gl.glColor4f(1f, 1f, 1f, 1f);
        } else {
            //gl.glColor4f(1f, 1f, 1f, 1f);
            finished = true;
        }

        gl.glColor4f(1f, 1f, 1f, 1f);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDisable(GL2.GL_POLYGON_SMOOTH);

    }
}
