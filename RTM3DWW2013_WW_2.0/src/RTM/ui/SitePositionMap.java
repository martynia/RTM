package RTM.ui;

import RTM.RealTimeMonitor;
//import RealTimeMonitor.layers.Locatable;
//import RealTimeMonitor.layers.Site;
import gov.nasa.worldwind.Locatable; // replaces RealTimeMonitor.layers.Site
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Mikhail Khrypach
 */
public class SitePositionMap {
    private Hashtable<Integer,LinkedList<Locatable>> sites;
    //number of zeros in the value represents the number of digits after decimal
    //point in lon./lat.
    int scale = 10;
    //number of zeros represents the number of digits in lat./lon. before decimal
    //point
    int order = 1000;

    public SitePositionMap() {
        sites = new Hashtable<Integer, LinkedList<Locatable>>();
    }

    public void addSites(Iterable<Locatable> itbl){
        for(Iterator<Locatable> i = itbl.iterator() ;i.hasNext();){
            Locatable s = i.next();
            addSite(s);
        }
    }

    public void addSite(Locatable s){
        int key = positionToInt(s.getPosition());
        if(sites.containsKey(key)){
            sites.get(key).add(s);
        } else {
            LinkedList<Locatable> l = new LinkedList<Locatable>();
            l.add(s);
            sites.put(key,l);
        }
    }

    public Iterable<Locatable> getSites(Position position){
        System.out.println("Getting Locatables (sites)>>>");
        LinkedList<Locatable> result = new LinkedList<Locatable>();
        int p = positionToInt(position);
        System.out.println("   position: " + p + " Lat "+ position.latitude.degrees + " Lon " + position.longitude.degrees);
        System.out.println("   zoom: " + ((BasicOrbitView)RealTimeMonitor.getAf().getWwd().getView()).getZoom());
        for (int i = -2 ; i < 3 ; i ++){
            int pt = p + i*order*scale;
            //System.out.println("   position mod1.: " + pt);
            for (int j = pt-2 ; j < pt + 3 ; j ++){
                //System.out.println("   position mod2.: " + j);
                if(sites.get(j)!=null){
                    //System.out.println("   number of sts: " + sites.get(j).size());
                    result.addAll(sites.get(j));
                } else {
                    //System.out.println("   no sites at " + j);
                }
            }
        }
        return result;
    }

    //combines lat./lon. coordinates into one unique int, that is used for hash table
    private int positionToInt(Position p){
        int result = order*scale;
        result = result * ((int)((p.latitude.degrees + 90)*scale));
        result = result + ((int)((p.longitude.degrees + 180)*scale));
        return result;
    }

    //assuming that the integer is computed by positionToInt
    //!!! not working properly now !!!
    private Position intToPosition(int i){
        int lat = i / 1000;
        int lon = i % 1000;
        return new Position(Angle.fromDegrees((double)lat), Angle.fromDegrees((double)lon), 0.0);
    }

    public int size(){
        return sites.size();
    }
}
