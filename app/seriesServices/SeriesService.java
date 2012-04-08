/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seriesServices;

import seriesapi.Season;
import seriesapi.Series;
import java.util.List;

/**
 *
 * @author Christian
 */
public interface SeriesService {
    // returns a list of series by a query,
    // should be limited to a number between 10-20
    // note: only simple data should be retrieved, no cascaded
    // items as seasons and episodes have to be provided here.
    // user shall not expect that cascaded information is available 
    public List<Series> searchSeries(String query);
    
    // returns a series by id. includes casceded items
    public Series getSeriesById(String id);
    
    // returns series without cascaded items
    // but has to retrieve at least the season numbers
    public Series getSeriesInfo(String id);
    
    public Season getSeason(String id, int number);
}
