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
public class Season implements Serializable {
    public int number;
    public List<Episode> episodes;
}
