// GenericJob

package RTM.datasource.glite ;

import java.sql.Timestamp ;

/**
 * Create a job with required attributes. The RTM job class holder.
 * @author Janusz Martyniak, original design Gidon Moont.
 */
public class Job implements RTM.job.GenericJob{

  private String id = null ;
  private String rb = null ;
  private String state = null ;
  private String ce = null ;
  private String queue = null ;
  private String ui = null ;
  private String vo = null ;

  private Timestamp registered = null ;
  private Timestamp update = null ;
  private Timestamp rtm_timestamp = null ;

  /**
   * Create a job with required attributes.
   * @param id  job id
   * @param rb  Resource Broker/WMS the jobs was submitted to
   * @param registered reristerd timestamp (UTC)
   * @param state job state (SUBMITTED, SCHEDULED etc)
   * @param update timestamp the job state changed (from LB, UTC)
   * @param rtm_timestamp RTM own timestamp of last state change
   * @param ce CE of the job
   * @param queue queue on that CE
   * @param ui UI the job originated from
   * @param vo VO of the job
   */
  public Job( String id , String rb , String registered , String state , String update , String rtm_timestamp , String ce , String queue , String ui , String vo )
  {

    this.id = id ;
    this.rb = rb ;
    this.state = state ;
    this.ce = ce ;
    this.queue = queue ;
    this.ui = ui ;
    this.vo = vo ;

    this.registered = Timestamp.valueOf( registered ) ;
    this.update = Timestamp.valueOf( update ) ;
    this.rtm_timestamp = Timestamp.valueOf( rtm_timestamp ) ;

  }

  /**
   * Get job id.
   * @return job id
   */

  public synchronized String getID()
  {
    return id ;
  }

  /**
   * Get the Resource Broker/WMS name.
   * @return
   */
  public synchronized String getRB()
  {
    return rb ;
  }

  /**
   * Get the current job state.
   * @return
   */
  public synchronized String getState()
  {
    return state ;
  }

  /**
   * Get the CE (may be unknown at early job lifetime).
   * @return
   */
  public synchronized String getCE()
  {
    return ce ;
  }

  /**
   * Get job queue name on the CE.
   * @return
   */
  public synchronized String getQueue()
  {
    return queue ;
  }

  /**
   * Get the UI job was submitted from.
   * @return
   */
  public synchronized String getUI()
  {
    return ui ;
  }

  /**
   * Get the Virtual Organisation the job belongs to.
   * @return
   */
  public synchronized String getVO()
  {
    return vo ;
  }

  /**
   * Get the current state update timestamp.
   * @return
   */
  public synchronized Timestamp getUpdate()
  {
    return update ;
  }

  /**
   * Get the job registered timeistamp.
   * @return
   */
  public synchronized Timestamp getRegistered()
  {
    return registered ;
  }

}
