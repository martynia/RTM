/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.Browser;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * An action litener to display a document in an extenal browser. It uses
 * Java 6  <code>java.awt.Desktop</code> class to start a default browser.
 * @see Desktop
 * @author Janusz Martyniak
 */
public class ExternalBrowser implements ActionListener{
    
    private final String uri;
    public ExternalBrowser(final String uri){
        this.uri=uri;
    }
    /**
     * implements <code>actionPerformed()</code> from <code>ActionListener<code> class
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop d = Desktop.getDesktop();
                d.browse(new URI(uri));
            } catch (URISyntaxException ex) {
                JOptionPane.showMessageDialog(null, "URI syntax wrong", "Can't launch default browser!", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(ExternalBrowser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "User default browser is not found or cannot be launched!", "Can't launch default browser!", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(ExternalBrowser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedOperationException  ex) {
                JOptionPane.showMessageDialog(null, "The OS does not support Desktop.Action.BROWSE", "Can't launch default browser!", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(ExternalBrowser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                JOptionPane.showMessageDialog(null, "Cannot create a subprocess - please report this bug", "Can't launch default browser!", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(ExternalBrowser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Can't launch default browser!", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(ExternalBrowser.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
           JOptionPane.showMessageDialog(null, "Your OS does not support java desktop application launch!", "Can't launch default browser!", JOptionPane.ERROR_MESSAGE);
        }
    }

}
