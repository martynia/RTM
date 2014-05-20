/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.datasource.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author martynia
 */
public class Reader {

    private final String datafile;

    public Reader(String source) {
        this.datafile = source;
    }

    public void read(DefaultHandler handler) {
        URL dataURL;
        InputStream xmlStream = null;
        InputSource source;
        XMLReader parser;
        URLConnection dataURLConnection;
        try {
            dataURL = new URL(datafile);

            dataURLConnection = dataURL.openConnection();
            if (System.getProperty("rtm.http.proxy") != null) {
                dataURLConnection.setRequestProperty("Proxy-Authorization",
                        System.getProperty("rtm.http.proxy"));
            }
            xmlStream = dataURLConnection.getInputStream();
            source = new InputSource(xmlStream);

            parser = XMLReaderFactory.createXMLReader();
            
            parser.setContentHandler(handler);
            parser.setErrorHandler(handler);

            parser.parse(source);

        } catch (MalformedURLException mue) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE,
                        "URL problem", mue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                xmlStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Reader.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
    }
}
