/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seriesServices;

import seriesapi.Episode;
import seriesapi.Series;
import models.*;

/**
 *
 * @author Christian
 */
public interface ExtendableSeriesService extends SeriesService {
    public void addSeries(Series series);
    public void addSeason(String seasonId, int seasonNumber);
    public void addEpisode(String seasonId, int seasonNumber, Episode episode);
}
