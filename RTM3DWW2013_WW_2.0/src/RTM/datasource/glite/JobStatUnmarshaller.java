/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.glite;

import RTM.jaxb.jobstat.JobStatistics;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Unmarshal an XML document which contains CE CE/RB/VO ordered job numbers to obtain global job statistics. 
 * This is a remote call procedure since the XML is created periodically on the server site.
 * @author Janusz Martyniak
 */
public class JobStatUnmarshaller {

    private Unmarshaller u;
    private JAXBContext jc;
    private URL url;
    private int timeout = 15000;
    private JobStatistics js = new JobStatistics();

    /**
     * Create JXB based unmarshaller for Job statistics.
     * @param is is the URL to unmarshal from.
     * @throws JAXBException JAXB context cannot be created.
     */
    public JobStatUnmarshaller(URL is) throws JAXBException {
        this.url = is;
        jc = JAXBContext.newInstance("RTM.jaxb.jobstat");
        u = jc.createUnmarshaller();
    }

    /**
     * Get a complete ( synchronized) list of CEs with number of jobs pre RB and VO.
     * If timeout - an empty list is returned.
     * @return CE list object as read from the server.
     * @throws JAXBException
     */
    public List<Object> getCEList(){
        try {
            Thread xmlReader = new Thread(new JobStatXMLReader());
            xmlReader.start();
            xmlReader.join(timeout); // wait most 15 seconds for the reply
            if(xmlReader.interrupted()) {
              Logger.getLogger(JobStatUnmarshaller.class.getName()).log(Level.WARNING," Interrupted - timeout ?");  
            }            
        } catch (InterruptedException ex) {
            Logger.getLogger(JobStatUnmarshaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        // return the list (might be empty)
        return Collections.synchronizedList(js.getCe());
    }

    /**
     * Innner class holding the XML unmarshaller call.
     */
    class JobStatXMLReader implements Runnable {

        public void run() {          
            try {
                js = (JobStatistics) u.unmarshal(url);
            } catch (JAXBException ex) {
                Logger.getLogger(JobStatUnmarshaller.class.getName()).log(Level.SEVERE," (Network down?) Will retry...", ex);
            }
        }
    }
}

