/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.panda.xmlmapadapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Janusz Martyniak
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Jobsummary {

    public Jobsummary() {
    }
    @XmlElement
    public XMLGregorianCalendar startdatetime;
    @XmlElement
    public XMLGregorianCalendar enddatetime;

    public XMLGregorianCalendar getEnddatetime() {
        return enddatetime;
    }

    public XMLGregorianCalendar getStartdatetime() {
        return startdatetime;
    }

    public void setEnddatetime(XMLGregorianCalendar enddatetime) {
        this.enddatetime = enddatetime;
    }

    public void setStartdatetime(XMLGregorianCalendar startdatetime) {
        this.startdatetime = startdatetime;
    }


    @XmlElement
    public SiteJobList siteJobList;

    public void setSiteJobList(SiteJobList siteJobList) {
        this.siteJobList = siteJobList;
    }

    public SiteJobList getSiteJobList() {
        return siteJobList;
    }
}
