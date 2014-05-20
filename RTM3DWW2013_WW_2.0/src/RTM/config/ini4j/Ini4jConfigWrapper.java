package RTM.config.ini4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author martynia
 */
public class Ini4jConfigWrapper {

    private static Ini ini = new Ini();
    public static String defaultFile = "/RTM/config/ini4j/ini_config.cnf"; // or absolute: /RTM/config/ini4j/ini_config.cnf 

    /**
     * Return a filed value by the section name and field name
     * @param sectionName name of the section i.e [database]
     * @param fieldName  name of a filed at this section
     * @return the field value
     */
    public String getSectionValue(String sectionName, String fieldName) {
        Ini.Section section = ini.get(sectionName);
            if (section != null) {
                return section.get(fieldName);
            } else {
                return null;
            }
    }
    /**
     * Return a Ini Section by name
     * @param name name of the section
     * @return the Ini.Section object
     */
    public Ini.Section getSection(String name) {
        return ini.get(name);
    }
    /**
     * Load the configuration for a file
     * @param propName property which holds the configuration file name
     * @throws IOException 
     */
    public void loadConfiguration(String propName) throws IOException {

        String cfgFileName = System.getProperty(propName);
        
        if (cfgFileName != null) {
            File file = new File(cfgFileName);
            try {
                ini.load(file); 

            } catch (IOException ioe) {  // this catches InvalidFileFormatException as well
                // try the built-in default file
                loadDefaultConfiguration(defaultFile);
            }
            // OK, got the config file:
        } else {
            loadDefaultConfiguration(defaultFile);
        }

    }

    public void loadDefaultConfiguration(String defaultFile) throws IOException {
        URL url = null;
        url = Ini4jConfigWrapper.class.getResource(defaultFile);
        if (url != null) {
            ini.load(url);
        } else {
            System.out.println(" Cannot load default resource " + defaultFile);
            throw new IOException("The default configuration file NOT found !");
        }
    }

    public String getClasspathString() {
        StringBuffer classpath = new StringBuffer();
        ClassLoader applicationClassLoader = this.getClass().getClassLoader();
        if (applicationClassLoader == null) {
            applicationClassLoader = ClassLoader.getSystemClassLoader();
        }
        URL[] urls = ((URLClassLoader) applicationClassLoader).getURLs();
        for (int i = 0; i < urls.length; i++) {
            classpath.append(urls[i].getFile()).append("\r\n");
        }

        return classpath.toString();
    }
    public static void main(String[] agrs) throws IOException {
        
       
        Ini4jConfigWrapper t = new Ini4jConfigWrapper(); 
        t.loadConfiguration("rtmConfig");
        String speed = t.getSectionValue("spin","speed");
        System.out.println(" Speed = " + speed);
        
        // Path following;
        
        //int[] n = sneezy.getAll("fortuneNumber", int[].class);
        Ini.Section  path = t.getSection("path");
        String[] names = path.getAll("place", String[].class);
        double[] lat = path.getAll("latitude", double[].class);
        double[] lon = path.getAll("longitude", double[].class);
       
        System.out.println("latitudes len"+lat.length);
        System.out.println("names len"+names.length);
        for(int i=0; i < names.length; i++){
            System.out.print("elem "+i+": "+names[i]);
            System.out.print(" latitude "+ lat[i]);
            System.out.println(" longitude "+ lon[i]);
        }
        System.out.println(" Classpath : \n" + t.getClasspathString());
    }
    
}
