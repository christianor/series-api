/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seriesServices;

import helpers.UrlHelper;
import helpers.XmlHelper;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import play.cache.Cache;
import play.libs.XPath;
import seriesapi.Episode;
import seriesapi.Season;
import seriesapi.Series;

/**
 *
 * @author Christian
 */
public class TheTvDbSeriesService implements SeriesService {

    private final String accountIdentifier = "7DAA22918D68800E";

    public List<Series> searchSeries(String query) {
        List<Series> series = null;
        try {
            String searchUrl = "http://www.thetvdb.com/api/GetSeries.php?seriesname=" + URLEncoder.encode(query, "UTF-8");


            Document search = XmlHelper.getDoc(UrlHelper.getTextByUrl(searchUrl));


            if (search == null) {
                return null;
            }

            series = new ArrayList<Series>();

            for (Node node : XPath.selectNodes("//Series", search)) {
                Series _series = new Series();
                _series.id = XPath.selectText("seriesid", node);
                _series.name = XPath.selectText("SeriesName", node);
                _series.description = XPath.selectText("Overview", node);
                String firstAired = XPath.selectText("FirstAired", node);

                if (firstAired != null) {
                    String fromYear = firstAired.split("-")[0];
                    if (fromYear != null) {
                        _series.fromYear = Integer.parseInt(fromYear);
                    }
                }

                series.add(_series);
            }
        } catch (UnsupportedEncodingException e) {
            play.Logger.fatal(e.getMessage());
        }

        return series;
    }

    public Series getSeriesById(String id) {
        return getSeriesById(id, false);
    }

    public Series getSeriesById(String id, boolean noEpisodes) {

        Series series = Cache.get("thetvdb:seriesById_" + id, Series.class);

        if (series == null) {


            Document langXml = getLangXmlDocument(id);

            if (langXml == null) {
                return null;
            }

            series = new Series();
            series.seasons = new ArrayList<Season>();
            HashMap<String, Season> seasonMap = new HashMap<String, Season>();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);


            Node seriesInfo = XPath.selectNode("//Data/Series", langXml);
            series.id = XPath.selectText("seriesid", seriesInfo);
            series.name = XPath.selectText("SeriesName", seriesInfo);
            series.description = XPath.selectText("Overview", seriesInfo);
            String firstAired = XPath.selectText("FirstAired", seriesInfo);

            if (firstAired != null) {
                String fromYear = firstAired.split("-")[0];
                if (fromYear != null) {
                    series.fromYear = Integer.parseInt(fromYear);
                }
            }


            for (Node node : XPath.selectNodes("//Data/Episode", langXml)) {
                String seasonNumber = XPath.selectText("SeasonNumber", node);
                Season season = null;


                if (!seasonMap.containsKey(seasonNumber)) {
                    season = new Season();
                    seasonMap.put(seasonNumber, season);
                    season.episodes = new ArrayList<Episode>();
                } else {
                    season = seasonMap.get(seasonNumber);
                }


                Episode episode = new Episode();
                episode.number = Integer.parseInt(XPath.selectText("EpisodeNumber", node));
                episode.name = XPath.selectText("EpisodeName", node);
                // 2009-10-25
                try {
                    episode.date = dateFormat.parse(XPath.selectText("FirstAired", node));
                } catch (Exception e) {
                    // no date
                }
                episode.description = XPath.selectText("Overview", node);
                season.episodes.add(episode);

            }

            List<Integer> sortedSeasons = new ArrayList<Integer>();

            // sortedSeasons.addAll(seasonMap.keySet());

            for (String key : seasonMap.keySet()) {
                Integer _key = Integer.parseInt(key);
                sortedSeasons.add(_key);
            }

            Collections.sort(sortedSeasons);


            for (Number key : sortedSeasons) {
                Season season = seasonMap.get(key.toString());
                season.number = Integer.parseInt(key.toString());

                series.seasons.add(season);
            }

            Cache.add("thetvdb:seriesById_" + id, series);
        }

        // remove episodes for info

        if (noEpisodes) {
            Series seriesNoEpisodes = new Series();
            seriesNoEpisodes.description = series.description;
            seriesNoEpisodes.fromYear = series.fromYear;
            seriesNoEpisodes.id = series.id;
            seriesNoEpisodes.name = series.name;
            seriesNoEpisodes.seasons = new ArrayList<Season>();

            for (Season season : series.seasons) {
                Season seasonNoEpisodes = new Season();
                seasonNoEpisodes.number = season.number;
                seriesNoEpisodes.seasons.add(seasonNoEpisodes);
            }

            seriesNoEpisodes.storyline = series.storyline;
            seriesNoEpisodes.toYear = series.toYear;

            return seriesNoEpisodes;
        }

        return series;
    }

    public Series getSeriesInfo(String id) {
        return getSeriesById(id, true);

    }

    public Season getSeason(String id, int number) {
        try {
            for (Season season : getSeriesById(id).seasons) {
                if (season.number == number) {
                    return season;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Document getLangXmlDocument(String id) {
        // Retrieve <mirrorpath_zip>/api/<apikey>/series/<seriesid>/all/<language>.zip and extract <language>.xml and banners.xml.
        String url = "http://www.thetvdb.com/api/" + accountIdentifier + "/series/" + id + "/all/en.zip";
        byte[] zipBytes = UrlHelper.getByteArrayByUrl(url);


        try {

            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(zipBytes)));
            byte[] bytes = null;

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("en.xml")) {
                    ByteArrayOutputStream dynBytes = new ByteArrayOutputStream();

                    for (int i = 0; zis.available() > 0; i++) {
                        dynBytes.write((byte) zis.read());
                    }

                    bytes = dynBytes.toByteArray();
                    break;
                }
            }

            if (bytes == null) {
                throw new Exception("Konnte en.xml nicht im Zip finden");
            }

            String xml = new String(bytes, "UTF-8");
            xml = xml.substring(0, xml.length() - 1);
            Document doc = XmlHelper.getDoc(xml);

            return doc;

        } catch (Exception e) {
            play.Logger.error("Fehler beim laden/parsen der Zip Datei. " + e.getMessage());
            return null;
        }

    }
}
