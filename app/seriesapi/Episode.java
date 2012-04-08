/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seriesapi;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Christian
 */
public class Episode implements Serializable{
    public int number;
    public String name;
    public String description;
    public Date date;
}
