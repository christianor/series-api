/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author Christian
 */
// Currently disabled
/*@Entity(name="seriespopularity")*/
public class SeriesPopularity extends Model {
    @Column(unique=true, nullable=false)
    public String imdbId;
    public long searchCount;
}
