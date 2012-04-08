/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seriesServices;

import seriesapi.Season;
import seriesapi.Episode;
import seriesapi.Series;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import models.*;

/**
 *
 * @author Christian
 */
public class TestSeriesService implements SeriesService {

    public List<Series> searchSeries(String query) {

        List<Series> series = new ArrayList<Series>();
        Series bigBangTheory = new Series();

        bigBangTheory.name = "The Big Bang Theory";
        bigBangTheory.fromYear = 2007;
        bigBangTheory.description = "A woman who moves into an apartment next door to two brilliant but socially awkward physicists shows them how little they know about life outside of the laboratory.";
        bigBangTheory.id = "1";

        series.add(bigBangTheory);

        return series;
    }

    public Series getSeriesById(String id) {
        
        if (id.equals("1")) {
            Series bigBangTheory = new Series();
            Season season1 = new Season();
            Episode episode1 = new Episode();

            try {
                episode1.date = DateFormat.getDateInstance().parse("21.09.2007");
            } catch (ParseException ex) {
                play.Logger.error(ex.getMessage());
            }
            episode1.description = "A pair of brilliant theoretical physicists meet a woman who shows them how little they know about the real world.";
            episode1.number = 1;
            episode1.name = "Pilot";


            season1.number = 1;
            season1.episodes = new ArrayList<Episode>();
            season1.episodes.add(episode1);

            bigBangTheory.name = "The Big Bang Theory";
            bigBangTheory.fromYear = 2007;
            bigBangTheory.description = "A woman who moves into an apartment next door to two brilliant but socially awkward physicists shows them how little they know about life outside of the laboratory.";
            bigBangTheory.seasons = new ArrayList<Season>();
            bigBangTheory.seasons.add(season1);
            bigBangTheory.id = "1";
            
            return bigBangTheory;
        }
        else return null;
    }

    public Series getSeriesInfo(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Season getSeason(String id, int number) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
