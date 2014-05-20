// GenericJob

package RTM.job;

import java.sql.Timestamp ;

  public interface GenericJob {

  public String getID();
  public String getState();
  public Timestamp getUpdate();
}
