/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.panda.xmlmapadapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * An example of how to use the xml map adapter
 * @author martynia
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JAXBException, FileNotFoundException, IOException, DatatypeConfigurationException {
        try {
            File temp_file = new File("/tmp/XMLAdapter_test.xml");
            FileOutputStream os = new FileOutputStream(temp_file);
            //JAXBContext jc = JAXBContext.newInstance(SiteJobList.class);
            JAXBContext jc = JAXBContext.newInstance(Jobsummary.class);
            Marshaller marshaller = jc.createMarshaller();
            SiteJobList foo = new SiteJobList();
            HashMap<String,SiteJobs> table = new HashMap<String,SiteJobs>();
            SiteJobs site1 = new SiteJobs("CERN-PROD", 120, 200, 150, 50);
            SiteJobs site2 = new SiteJobs("UKI-HEP-IC", 120, 200, 150, 50);
            table.put("CERN-PROD", site1);
            table.put("UKI-HEP-IC", site2);
            foo.setMap(table);
            // job summary holds a header (with start/end time, commmon to all sites)
            // and a SiteJobList instance which holds a hashtable of SiteJobs with a site
            // name as a key
            Jobsummary jobsummary = new Jobsummary();
            jobsummary.setSiteJobList(foo);
            // set the query time
            DateFormat dfmt = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            Date startTime = dfmt.parse("2011:12:20 13:25:00");
            GregorianCalendar now = new GregorianCalendar();
            now.setTime(startTime);
            // Obtain a DatatypeFactory instance.
            DatatypeFactory df = DatatypeFactory.newInstance();
            // Create an XMLGregorianCalendar with the trial date.
            XMLGregorianCalendar gcDateTime =
                    df.newXMLGregorianCalendar(
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH),
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE), now.get(Calendar.SECOND),
                    0, DatatypeConstants.FIELD_UNDEFINED);
            jobsummary.setStartdatetime(gcDateTime);
            jobsummary.setEnddatetime(gcDateTime);
            //marshaller.marshal(foo, System.out);
            //marshaller.marshal(foo, os);
            marshaller.marshal(jobsummary, System.out);
            marshaller.marshal(jobsummary, os);
            os.close();
            /*
            FileInputStream is = new FileInputStream(temp_file);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            SiteJobList infoo = (SiteJobList)unmarshaller.unmarshal(is);
            HashMap intable = (HashMap)infoo.getMap();
            Iterator iter =  intable.keySet().iterator();
            String key;
            System.out.println("\n=====================================");
            SiteJobs sitej;
            while(iter.hasNext()) {
            key = (String)iter.next();
            sitej = (SiteJobs)intable.get(key);
            System.out.println("Site name (key) " + key + " value " + sitej);
            }
             */
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
