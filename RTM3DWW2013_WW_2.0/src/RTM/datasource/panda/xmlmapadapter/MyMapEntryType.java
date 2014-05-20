/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.datasource.panda.xmlmapadapter;

/**
 *
 * @author martynia
 */
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class MyMapEntryType {

   @XmlAttribute
   public String key;

   @XmlElement
   public SiteJobs value;

}