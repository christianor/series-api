/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seriesServices;

import seriesapi.Season;
import seriesapi.Episode;
import seriesapi.Series;
import helpers.UrlHelper;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.text.SimpleDateFormat;
import java.util.Locale;
import play.cache.Cache;

/**
 *
 * @author Christian
 */
public class ImdbSeriesService implements SeriesService {

    public List<Series> searchSeries(String query) {

        List<Series> series = new ArrayList<Series>();

        try {
            Document searchResultDocument = Jsoup.parse(UrlHelper.getTextByUrl("http://www.imdb.com/search/title?title=" + URLEncoder.encode(query, "UTF-8") + "&title_type=tv_series"));
            Elements titles = searchResultDocument.getElementsByClass("title");


            for (Element title : titles) {
                Series _series = new Series();

                Element titleLink = title.getElementsByTag("a").get(1);
                titleLink.attr("href");

                _series.name = titleLink.text();
                _series.fromYear = Integer.parseInt(title.getElementsByClass("year_type").first().text().split("\\(")[1].split(" ")[0]);
                _series.id = title.getElementsByClass("add_to_watchlist").first().attr("data-const");

                Element description = null;

                if ((description = title.getElementsByClass("outline").first()) != null) {
                    _series.description = description.text();
                }

                series.add(_series);
            }

        } catch (Exception e) {
            play.Logger.fatal("Error searching for series. " + e.getClass() + e.getMessage());
        }
        return series;
    }

    public Series getSeriesById(String id) {
        Series series = Cache.get("imdb:seriesById_" + id, Series.class);

        if (series == null) {
            series = getSeriesById(id, true);
            if (series != null)
                Cache.add("imdb:seriesById_" + id, series, "1d");
        }
        return series;
    }

    public Series getSeriesInfo(String id) {
        Series series = Cache.get("imdb:seriesInfo_" + id, Series.class);

        if (series == null) {
            series = getSeriesById(id, false);
            if (series != null)
                Cache.add("imdb:seriesInfo_" + id, series, "1d");
        }
        return series;
    }

    private Series getSeriesById(String id, boolean withEpisodes) {
        Series series = new Series();
        try {
            Document page = Jsoup.parse(UrlHelper.getTextByUrl("http://www.imdb.com/title/" + URLEncoder.encode(id, "UTF-8") + "/"));
            Element details = page.getElementById("maindetails_center_top");
            series.seasons = new ArrayList<Season>();

            Element header = details.getElementsByTag("h1").first();
            series.description = details.getElementById("overview-top").select("p[itemprop=description]").first().text();

            series.name = header.ownText();
            String fromToString = header.getElementsByTag("span").first().text();
            series.fromYear = Integer.parseInt(fromToString.split("\\(")[1].split("[^\\d]")[0]);
            try {
                series.toYear = Integer.parseInt(fromToString.split("\\(")[1].split("[^\\d]")[1]);
            } catch (Exception e) {
                // Nothing 
            }
            series.storyline = page.select(".article > h2:contains(Storyline)").first().parent().getElementsByTag("p").first().text();
            int i = series.storyline.lastIndexOf("Written by");

            if (i > -1) {
                series.storyline = series.storyline.substring(0, i);
            }

            Elements seasonLinks = page.select("div.article > div.txt-block").first().select("span a");

            if (seasonLinks.select(":contains(See more)").size() > 0) {
                
                Document episodePage = Jsoup.parse(UrlHelper.getTextByUrl("http://www.imdb.com/title/" + id + "/episodes")); 
                Element seasonSelect = episodePage.getElementById("bySeason");
                
                for (Element optionElement : seasonSelect.children()) {
                    int number = Integer.parseInt(optionElement.attr("value"));
                    Season season = null;
                    
                    if (withEpisodes) {
                        season = getSeason(id, number);
                    } else {
                        season = new Season();
                        season.number = number;
                    }
                    series.seasons.add(season);
                }
            } else {
                for (Element seasonLink : seasonLinks) {
                    Season season = null;
                    int number = Integer.parseInt(seasonLink.attr("href").split("\\?season=")[1]);

                    if (withEpisodes) {
                        season = getSeason(id, number);
                    } else {
                        season = new Season();
                        season.number = number;
                    }
                    series.seasons.add(season);
                }
            }
        } catch (Exception e) {
            play.Logger.fatal("IMDB series parsing went wrong. " + e.getClass() + " Message: " + e.getMessage());
            return null;
        }

        return series;
    }

    public Season getSeason(String id, int number) {

        Season season = Cache.get("imdb:season_" + id + "_" + number, Season.class);

        if (season
                == null) {
            season = new Season();
            season.episodes = new ArrayList<Episode>();

            try {
                Document seasonPage = Jsoup.parse(UrlHelper.getTextByUrl("http://www.imdb.com/title/" + URLEncoder.encode(id, "UTF-8") + "/episodes?season=" + number));

                DateFormat dateFormatDotted = new SimpleDateFormat("MMM. dd, yyyy", Locale.ENGLISH);
                DateFormat dateFormatUndotted = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

                for (Element episodeInfo : seasonPage.select(".info")) {
                    Episode episode = new Episode();
                    episode.number = Integer.parseInt(episodeInfo.select("meta").first().attr("content"));
                    String airDate = episodeInfo.select(".airdate").first().text();


                    try {
                        episode.date = dateFormatDotted.parse(airDate);
                    } catch (ParseException e) {
                        try {
                            episode.date = dateFormatUndotted.parse(airDate);
                        } catch (ParseException ex) {
                            play.Logger.error(ex.getMessage());
                        }
                    }
                    episode.name = episodeInfo.getElementsByTag("strong").first().text();
                    episode.description = episodeInfo.getElementsByClass("item_description").first().text();

                    season.episodes.add(episode);
                }

                season.number = number;
                Cache.add("imdb:season_" + id + "_" + number, season);

            } catch (Exception e) {
                play.Logger.fatal("IMDB season parsing went wrong " + e.getClass() + " Message: " + e.getMessage());
                return null;
            }
        }

        return season;
    }
            
}