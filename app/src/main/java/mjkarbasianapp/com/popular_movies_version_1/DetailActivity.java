package mjkarbasianapp.com.popular_movies_version_1;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

import mjkarbasianapp.com.popular_movies_version_1.Data.MovieContract;
import mjkarbasianapp.com.popular_movies_version_1.Data.MovieContract.MovieEntry;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

        final static int DETAIL_LOADER = 1;
        private static final String BASE_PIC_URI = "http://image.tmdb.org/t/p/w185/";
        MediaController  mMediaController;
        private static String LOG_TAG = DetailFragment.class.getSimpleName();
        private final static  String MOVIE_SHARE_HASHTAG =" #MovieApp";
        private String mTitle;
        private int mPopularity;

        private final String[] MOVIE_COLUMNS = {
                MovieEntry._ID,
                MovieEntry.COLUMN_TITLE,
                MovieEntry.COLUMN_OVERVIEW,
                MovieEntry.COLUMN_RELEASE_DATE,
                MovieEntry.COLUMN_POSTER_PATH,
                MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieEntry.COLUMN_POPULARITY
        };

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_detail, container, false);

        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }

        private Intent createShareMovieIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Lets see " +'"' +mTitle+'"'+"\n" + "with popular Rating of " + Integer.toString(mPopularity) + MOVIE_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");
            if (!data.moveToFirst()) { return; }
            ImageView imageView = (ImageView)getView().findViewById(R.id.movie_detail_image);
            TextView nameView = (TextView)getView().findViewById(R.id.movie_name);
            TextView yearView = (TextView)getView().findViewById(R.id.movie_year);
            TextView rateView = (TextView)getView().findViewById(R.id.movie_ratings);
            RatingBar ratingBar =(RatingBar) getView().findViewById(R.id.ratingBar);
            TextView overviewView = (TextView)getView().findViewById(R.id.movie_description);
            mTitle = data.getString(data.getColumnIndex(MovieEntry.COLUMN_TITLE));
            mPopularity = data.getColumnIndex(MovieEntry.COLUMN_POPULARITY);
            nameView.setText(mTitle) ;
            yearView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)));
            rateView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE)));
            ratingBar.setRating(Float.parseFloat(data.getString(data.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE))) / 2);
            overviewView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)));
            String url = BASE_PIC_URI + data.getString(data.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
            Picasso.with(getActivity()).load(url).into(imageView);

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
