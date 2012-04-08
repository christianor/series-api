/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package notifiers;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.libs.Mail;

/**
 *
 * @author Christian
 */
public class Mails {

    public static void notifyAboutCaching() {
        SimpleEmail email = new SimpleEmail();
        try {
            email.setFrom("notifier@series-ortiz.rhcloud.com");
            email.addTo("chris.ortiz.reina@gmail.com");
            email.setSubject("Caching started");
            email.setMsg("Popular TV-Series Caching has been started");
            Mail.send(email);
            
        } catch (EmailException ex) {
            play.Logger.error("Fehler beim E-Mail Versand: " + ex.getMessage());
        }
    }
}
