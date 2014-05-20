/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.datasource.panda.xmlmapadapter;

/**
 *
 * @author martynia
 */
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

//@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SiteJobList {

   @XmlJavaTypeAdapter(MyMapAdapter.class)
   HashMap<String,SiteJobs> map = new HashMap<String,SiteJobs>();

   public HashMap<String,SiteJobs> getMap() {
      return map;
   }

   public void setMap(HashMap<String,SiteJobs> map) {
      this.map = map;
   }

}