/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seriesapi;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Christian
 */
public class Series implements Serializable {
    
    public String id;
    public String name;
    public String description;
    public String storyline;
    public int fromYear;
    public int toYear;
    
    public List<Season> seasons;
}
