package mjkarbasianapp.com.popular_movies_version_1;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import mjkarbasianapp.com.popular_movies_version_1.Data.MovieContract;
import mjkarbasianapp.com.popular_movies_version_1.Data.MovieContract.MovieEntry;

/**
 * Created by family on 5/11/2016.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    final static String LOG_TAG = FetchMovieTask.class.getSimpleName();
    static  ImageAdapter mImageAdapter = null;
    private final Context mContext;
    private String movieJsonStr;

    public FetchMovieTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(String... params) {
        //if there is no sort mode we could not show anythings
        if (params.length == 0) {
          Log.v(LOG_TAG,"The params is null! please Check!");
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String MovieJsonStr = null;
        String pageNum = params[1];


        try {
            // Construct the URL for the http://api.themoviedb.org/3/movie/popular

            final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";
            final String PAGE_PARAM = "page";
            final String LANG_PARAM = "language";
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(PAGE_PARAM, pageNum)
                    .appendQueryParameter(LANG_PARAM, "en")
                    .appendQueryParameter(APPID_PARAM, "c4cba1cb560e5692f0a51575d4f1a145")
                    .build();
            Log.v(LOG_TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Connection Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    private void getMovieDataFromJson(String movieJsonStr)  throws JSONException {


        final String OWM_RESULTS = "results";

        try{
            // getting main data of movie query
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());


            JSONObject movie;
            for (int i=0;i<movieArray.length();i++){
                ContentValues contentValues = new ContentValues();
                movie = movieArray.getJSONObject(i);
                contentValues.put(MovieEntry.COLUMN_TITLE,Utility.getMovieDataFromJson(movie)[0]);
                contentValues.put(MovieEntry.COLUMN_OVERVIEW,Utility.getMovieDataFromJson(movie)[1]);
                contentValues.put(MovieEntry.COLUMN_POSTER_PATH,Utility.getMovieDataFromJson(movie)[2]);
                contentValues.put(MovieEntry.COLUMN_BACK_DROP_PATH,Utility.getMovieDataFromJson(movie)[3]);
                contentValues.put(MovieEntry.COLUMN_VOTE_AVERAGE,Utility.getMovieDataFromJson(movie)[4]);
                contentValues.put(MovieEntry.COLUMN_RELEASE_DATE,Utility.getMovieDataFromJson(movie)[5]);
                contentValues.put(MovieEntry.COLUMN_POPULARITY,Utility.getMovieDataFromJson(movie)[6]);
                contentValues.put(MovieEntry.COLUMN_SITE_ID,Utility.getMovieDataFromJson(movie)[7]);
                cVVector.add(contentValues);
            }
            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.buildPopularMovieUri(), cvArray);
            }
            Log.v(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");

        }
        catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

}

