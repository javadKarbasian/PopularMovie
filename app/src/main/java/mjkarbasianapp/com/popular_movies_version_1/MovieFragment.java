package mjkarbasianapp.com.popular_movies_version_1;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by family on 3/5/2016.
 */
public  class MovieFragment extends Fragment {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    static  ImageAdapter imageAdapter = null;

    public MovieFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateMovie();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imageAdapter = new ImageAdapter(getActivity(), new ArrayList<JSONObject>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final GridView gridView = (GridView) rootView.findViewById(R.id.gridview_main);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public String[] movieData;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(),DetailActivity.class);
                JSONObject movie = imageAdapter.getItem(position);
                try {
                    movieData = Utility.getMovieDataFromJson(movie);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("movieData",movieData);
                startActivity(intent);
            }
        });
        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute("popular","1");
    }


    public class FetchMovieTask extends AsyncTask<String, Void, JSONArray>{


        private String movieJsonStr;

        @Override
        protected JSONArray doInBackground(String... params) {
            //if there is no sort mode we could not show anythings
            if (params.length == 0) {
                return null;
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
                Log.d(LOG_TAG,builtUri.toString());
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
                Log.d(LOG_TAG,movieJsonStr);

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
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        private JSONArray getMovieDataFromJson(String movieJsonStr)  throws JSONException {


            final String OWM_RESULTS = "results";

            // getting main data of movie query
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

            return movieArray;
        }

        @Override
        protected void onPostExecute(JSONArray result) {

                // New data is back from the server.  Hooray!
                if(imageAdapter!=null){
                imageAdapter.clear();
                }
            JSONObject movie;
            if(result!=null){
                for(int i= 0 ; i<result.length();i++){
                        try
                        {
                            movie = result.getJSONObject(i);
                            imageAdapter.add(movie);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
                }
            }
        }
    }


