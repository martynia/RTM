/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.apple;

import java.awt.Window;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martynia
 */
public class FullScreen {

    
    public static void init(Window w) {
        if (isMacOSX()) {
            enableFullScreenMode(w);
        }
    }
    
    public static void toggleFullScreen(Window window) {
        
        String className = "com.apple.eawt.Application";
        String methodName = "getApplication";
        String methodName2 = "requestToggleFullScreen";

        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, new Class<?>[]{});
            Object object = method.invoke(null);
            System.out.println(" object " + object);

            Method method2 = object.getClass().getMethod(methodName2, new Class<?>[]{Window.class});
            method2.invoke(object, window);

        } catch (Throwable t) {
            Logger.getLogger(FullScreen.class.getName()).log(Level.CONFIG, "Toggle full screen mode is not supported", t);
            t.printStackTrace();
        }
    }

    private static void enableFullScreenMode(Window window) {
        String className = "com.apple.eawt.FullScreenUtilities";
        String methodName = "setWindowCanFullScreen";

        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, new Class<?>[]{
                        Window.class, boolean.class});
            method.invoke(null, window, true);
        } catch (Throwable t) {
            Logger.getLogger(FullScreen.class.getName()).log(Level.CONFIG, "Full screen mode is not supported", t);
            t.printStackTrace();
        }
    }

    private static boolean isMacOSX() {
        //System.out.println("os.name property:" + System.getProperty("os.name"));
        return System.getProperty("os.name").indexOf("Mac OS X") >= 0;
    }
}
