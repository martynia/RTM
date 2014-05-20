/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RTM.datasource.panda;

import RTM.job.GenericJob;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author martynia
 */
public class PandaJob implements GenericJob{
    private String id;
    private String state;
    private long timestamp;
    private final Date date;
    private String rbSite;
    private String ceSite;

    public PandaJob(String id, String state, String rbSite, String ceSite,
            long randomTS, String vo) {
            this.id=id;
            this.state=state;
            this.rbSite=rbSite;
            this.ceSite=ceSite;
            this.date = new Date(randomTS);
            this.timestamp=randomTS;
    }
    public String toString() {
        return "Job "+id+" "+state+" "+rbSite+"<-->"+ceSite+" time: "+ date.toString();
    }

    public String getID() {
        return id;
    }

    public String getState() {
        return state;
    }

    public Timestamp getUpdate() {
        return new Timestamp(timestamp);
    }

    public long getTime() {
        return this.timestamp;
    }
    public void setTime(long time) {
        this.timestamp=time;
    }
    public String getRBsite(){ return this.rbSite; }
    public String getCEsite() { return this.ceSite; }
}
