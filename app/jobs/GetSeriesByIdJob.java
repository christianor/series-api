/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jobs;

import play.jobs.Job;
import seriesServices.ImdbSeriesService;
import seriesServices.SeriesService;
import seriesServices.TheTvDbSeriesService;
import seriesapi.Series;

/**
 *
 * @author Christian
 */
// long running job for complete series retrieving,
public class GetSeriesByIdJob extends Job<Series> {
    private String id;
    private String service;
    public GetSeriesByIdJob(String id)
    {
        this.id = id;
    }

    @Deprecated
    public GetSeriesByIdJob(String id, String s) {
        this(id);
        service = s;
    }

    @Override
    public Series doJobWithResult() {
        SeriesService service = getServiceImplementation();
        Series series = service.getSeriesById(id);
        return series;
    }

    private SeriesService getServiceImplementation() {
        return new TheTvDbSeriesService();
    }
}
