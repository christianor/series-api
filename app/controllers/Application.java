package controllers;

import play.libs.F.Promise;
import seriesapi.Season;
import seriesapi.Series;
import com.google.gson.Gson;
import seriesServices.ImdbSeriesService;
import seriesServices.SeriesService;
import play.mvc.*;
import models.*;

import java.util.*;
import jobs.GetSeriesByIdJob;
import seriesServices.TheTvDbSeriesService;

// import models.*;
public class Application extends Controller {

    public static void searchSeries(String name) {
        SeriesService service = getServiceImplementation();
        List<Series> series = service.searchSeries(name);

        renderByFormat(request.format, series);
    }

    public static void getSeriesById(String id) {

        Promise<Series> seriesPromise = new GetSeriesByIdJob(id).now();
        Series series = await(seriesPromise);

        renderByFormat(request.format, series);
    }

    public static void getSeriesInfoById(String id) {
        SeriesService service = getServiceImplementation();
        Series series = service.getSeriesInfo(id);

        renderByFormat(request.format, series);
    }

    public static void getSeriesSeason(String id, int number) {
        SeriesService service = getServiceImplementation();
        Season season = service.getSeason(id, number);

        renderByFormat(request.format, season);

    }

    @Util
    private static SeriesService getServiceImplementation() {
        return new TheTvDbSeriesService();
    }

    @Util
    public static void renderJSON(Object o) {
        if (request.params._contains("callback")) {
            Gson gson = new Gson();
            String out = gson.toJson(o);
            response.contentType = "text/javascript";

            renderText(request.params.get("callback") + "(" + out + ")");
        } else {
            Controller.renderJSON(o);
        }
    }

    @Util
    public static void renderByFormat(String format, Object o) {
        if (format.equals("json")) {
            renderJSON(o);
        } else if (format.equals("xml")) {
            renderXml(o);
        }
    }


    @Deprecated
    @Util
    private static void savePopularity(String id, String service) {
        if (service == null || !service.equals("thetvdb")) {
            SeriesPopularity popularity = SeriesPopularity.find("imdbid", id).first();

            if (popularity != null) {
                popularity.searchCount++;
                popularity.save();
            } else {
                popularity = new SeriesPopularity();
                popularity.imdbId = id;
                popularity.searchCount = 1;
            }

            popularity.save();
        }
    }
}
