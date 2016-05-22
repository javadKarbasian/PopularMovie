package mjkarbasianapp.com.popular_movies_version_1;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


import mjkarbasianapp.com.popular_movies_version_1.Data.MovieContract;
import mjkarbasianapp.com.popular_movies_version_1.service.MovieSyncAdapter;

/**
 * Created by family on 3/5/2016.
 */
public  class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    final static int MOVIE_LOADER = 0;
    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    static  ImageAdapter imageAdapter = null;
    private static final String[] Movie_COLUMNS = {MovieContract.MovieEntry._ID,MovieContract.MovieEntry.COLUMN_TITLE, MovieContract.MovieEntry.COLUMN_POSTER_PATH};

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
        Log.d(LOG_TAG, "onCreateView");
        imageAdapter = new ImageAdapter(getActivity(), null , 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final GridView gridView = (GridView) rootView.findViewById(R.id.gridview_main);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public String[] movieData;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class).
                            setData(MovieContract.MovieEntry.buildMovieUri(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID))));
                    startActivity(intent);
                }

        }
    });
        return rootView;
    }
    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();
        updateMovie();

    }

    private void updateMovie() {

          MovieSyncAdapter.syncImmediately(getActivity());
          getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");
        String sortSetting = Utility.getSortSetting(getActivity());
        Uri movieUri = MovieContract.MovieEntry.buildPopularMovieUri();
        switch (sortSetting)
        {
            case("popular"):
            {
                movieUri = MovieContract.MovieEntry.buildPopularMovieUri();
                break;
            }
            case ("top_rated"):
            {
                movieUri = MovieContract.MovieEntry.buildTopMovieUri();
                break;
            }
            default:
                new UnsupportedOperationException("Setting Not Match..!");
        }
        return new CursorLoader(getActivity(),movieUri,Movie_COLUMNS,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");
        imageAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoadReset");
        imageAdapter.swapCursor(null);
    }
}


