/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.dboard.xml.jaxb;

import RTM.datasource.panda.PandaJob;
import RTM.datasource.panda.PandaJobFactory;
import RTM.datasource.panda.xmlmapadapter.Jobsummary;
import RTM.datasource.panda.xmlmapadapter.SiteJobs;
import RTM.job.GenericJob;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Janusz Martyniak
 */
public class JobSummariesUnmarshaller {
   private Jobsummary jobsummary;

   public Jobsummary unmarshal(String inputFile) {
        FileInputStream is = null;
        try {
            URL url = new URL(inputFile);
            JAXBContext jc = JAXBContext.newInstance(Jobsummary.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            jobsummary = (Jobsummary)unmarshaller.unmarshal(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(JobSummariesUnmarshaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(JobSummariesUnmarshaller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
          //  try {
          //      is.close();
          //  } catch (IOException ex) {
          //      Logger.getLogger(JobSummariesMarshaller.class.getName()).log(Level.SEVERE, null, ex);
          //  }
        }
     return jobsummary;
    }    
   public String toString() {
        Map map = jobsummary.getSiteJobList().getMap();
        Collection c = map.values();
        StringBuffer sb = new StringBuffer();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            SiteJobs elem = (SiteJobs) it.next();
            sb = sb.append(elem.toString()).append("\n");
        }
        return sb.toString();
    }

   public static void main(String[] args) {
       //temporary Events buffer
       TreeMap<java.sql.Timestamp, GenericJob> Events = new TreeMap<java.sql.Timestamp, GenericJob>();
       JobSummariesUnmarshaller jsu = new JobSummariesUnmarshaller();
       Jobsummary jsum = jsu.unmarshal(inputFile);
       System.out.println(jsu.toString());
       PandaJobFactory factory = new PandaJobFactory();
       while(true) {
          PandaJob job = factory.createJob(jsum);
          Events.put(job.getUpdate(), job);
          if(job==null) {
              System.out.println(" No more jobs ");
              break;
          }
          System.out.println(job.toString());
       }
   }
   private static String inputFile ="/tmp/panda_all_prog.xml";
}
