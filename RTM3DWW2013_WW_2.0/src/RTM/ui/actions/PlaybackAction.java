/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTM.ui.actions;

import RTM.RealTimeMonitor;
import RTM.config.Configurator;
import RTM.datasource.glite.DataPlayback;
import RTM.datasource.glite.dataGetter;
import RTM.datasource.panda.PandaUpdatesHandler;
import RTM.playback.PandaPlayback;
import RTM.playback.PlaybackFactory;
import RTM.playback.Playback;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;

/**
 *
 * @author Janusz Martynia
 */
public class PlaybackAction extends AbstractAction {

    public void actionPerformed(ActionEvent ae) {
        boolean sel = ((JCheckBoxMenuItem) ae.getSource()).isSelected();
        DataPlayback dataPlayback;
        dataGetter dataGetter;
        // both threads should never run at the same time,
        // but stopping a thread might take a while because
        // of sleep() calls.
        if (sel) {
            System.out.println(" stopping data, starting playback");
            Thread dgt = RealTimeMonitor.getDataGetterThread();
            if (dgt.isAlive()) {
                RealTimeMonitor.getDataGetter().setThreadStop();
                dataPlayback = new DataPlayback();
                Thread pbt = new Thread(dataPlayback);
                pbt.start();
                RealTimeMonitor.setDataPlayback(dataPlayback);
                RealTimeMonitor.setgLitePlaybackThread(pbt);
                RealTimeMonitor.enableJobStats(false);

            }
            PandaUpdatesHandler puh = RealTimeMonitor.getPandaUpdatesHandler();
            if (!puh.isStopped()) {
                puh.stop();
            }
            String f = Configurator.getInstance().getPandaPlaybackListFilename();
            if (f != null) {
                Playback pl = (PandaPlayback<String>) PlaybackFactory.getInstance().createPlaybackList("Panda");
                if(pl != null) {
                   puh = new PandaUpdatesHandler(600, 0, pl.getRingList());
                }
                
            }
             else {
                System.out.println(" #### Panda playback error - no playback list ! ");
            }
            // start Panda playback:
        } else {
            Logger.getLogger(PlaybackAction.class.getName()).warning("Starting real data, stopping playback for all feeds...");
            Thread pbt = RealTimeMonitor.getgLitePlaybackThread();
            if (pbt.isAlive()) {
                RealTimeMonitor.getDataPlayback().setThreadStop();
                dataGetter = RealTimeMonitor.getDataGetter();
                Thread dat = new Thread(dataGetter);
                dat.start();
                RealTimeMonitor.setDataGetter(dataGetter);
                RealTimeMonitor.setgLitePlaybackThread(dat);
                // todo, check if config wants this.
                if (System.getProperty("rtm.noStats") == null) {
                    RealTimeMonitor.enableJobStats(true);
                }
            }
            // stop Panda playback, start panda data
            PandaUpdatesHandler puh = RealTimeMonitor.getPandaUpdatesHandler();
            if (!puh.isStopped()) {
                puh.stop();
            }
            puh = new PandaUpdatesHandler(600, 0);
        }
    }
}
