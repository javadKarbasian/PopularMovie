package mjkarbasianapp.com.popular_movies_version_1;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by family on 4/6/2016.
 */
public  class Utility {

    public static String[] getMovieDataFromJson(JSONObject movieItem)  throws JSONException {

        final String OWM_PAGE = "page";
        final String OWM_RESULTS = "results";
        final String OWM_TOTAL_RESULTS = "total_results";
        final String OWM_TOTAL_PAGES = "total_pages";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_OVERVIEW = "overview";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_GENRE_IDS = "genre_ids";
        final String OWM_ORIGINAL_TITLE =  "original_title";
        final String OWM_BACKDROP_PATH = "backdrop_path";
        final String OWN_POPULARITY =  "popularity";
        final String OWN_VOTE_AVERAGE =  "vote_average";
        final String OWN_ID = "id";

        // getting main data of movie query

            String title;
            String overview;
            String posterPath;
            String backDropPath;
            String voteAverage;
            String releaseDate;
            String popularity;
            String id;

            title = movieItem.getString(OWM_ORIGINAL_TITLE);
            overview = movieItem.getString(OWM_OVERVIEW);
            posterPath = movieItem.getString(OWM_POSTER_PATH);
            backDropPath = movieItem.getString(OWM_BACKDROP_PATH);
            voteAverage = movieItem.getString(OWN_VOTE_AVERAGE);
            releaseDate = movieItem.getString(OWM_RELEASE_DATE);
            popularity = movieItem.getString(OWN_POPULARITY);
            id = movieItem.getString(OWN_ID);
            String[] movieData = new String[8];
            movieData  = new String[]{title, overview, posterPath, backDropPath, voteAverage, releaseDate, popularity, id};

        return movieData ;
    }
}
