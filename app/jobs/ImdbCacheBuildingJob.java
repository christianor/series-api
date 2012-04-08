/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jobs;

import seriesServices.ImdbSeriesService;
import java.util.ArrayList;
import java.util.List;
import models.SeriesPopularity;
import notifiers.Mails;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/**
 *
 * @author Christian
 */
/* Currently disabled
@OnApplicationStart(async=true)
@Every("1d")*/
@Deprecated
public class ImdbCacheBuildingJob extends Job {

    @Override
    public void doJob() {
        play.Logger.info("Starting caching popular series");
        
        for (String id : getCachableSeriesIds()) {
            String _id = id;
            new CacheSeriesByIdThread(_id).start();
        }         
    }

    // TODO: je nach suchanzahl (suche speichern in der datenbank, counter inkrementieren)
    //
    public List<String> getCachableSeriesIds() {
        ArrayList<String> popularSeries = new ArrayList<String>();
        popularSeries.add("tt0773262");
        popularSeries.add("tt0898266");
        popularSeries.add("tt0313043");
        popularSeries.add("tt0460681");
        popularSeries.add("tt0096697");
        popularSeries.add("tt0491738");
        
        List<SeriesPopularity> seriesPopularitys = SeriesPopularity.find("searchcount > 30").fetch(50);
        
        for (SeriesPopularity popularity : seriesPopularitys) {
            
            if (!popularSeries.contains(popularity.imdbId)) {
                popularSeries.add(popularity.imdbId);
            }
        }
        
        return popularSeries;
    }
    
    class CacheSeriesByIdThread extends Thread {
        private String id;
        
        public CacheSeriesByIdThread(String id) {
            this.id = id;
        }
        
        @Override
        public void run() {
            ImdbSeriesService service = new ImdbSeriesService();
            service.getSeriesById(id);
            service.getSeriesInfo(id);
        }
    }
}
