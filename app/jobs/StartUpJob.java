/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jobs;
import play.jobs.*;
/**
 *
 * @author Christian
 */
@OnApplicationStart
public class StartUpJob extends Job{
    
    @Override
    public void doJob()
    {
        System.setProperty("http.agent", "");
    }
}
