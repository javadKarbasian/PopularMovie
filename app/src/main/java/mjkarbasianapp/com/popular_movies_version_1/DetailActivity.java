package mjkarbasianapp.com.popular_movies_version_1;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.VideoView;

import java.net.URI;


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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {
        MediaController  mMediaController;
        private static String LOG_TAG = DetailFragment.class.getSimpleName();

        public DetailFragment() {
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ImageAdapter imageAdapter = new ImageAdapter(getActivity());
            Intent intent = getActivity().getIntent();
            ImageView imageView = (ImageView)rootView.findViewById(R.id.movie_detail_image);
            TextView nameView = (TextView)rootView.findViewById(R.id.movie_name);
            TextView yearView = (TextView)rootView.findViewById(R.id.movie_year);
            TextView durationView = (TextView)rootView.findViewById(R.id.movie_duration);
            TextView rateView = (TextView)rootView.findViewById(R.id.movie_ratings);
            TextView overviewView = (TextView)rootView.findViewById(R.id.movie_description);
            VideoView trailerView = (VideoView)rootView.findViewById(R.id.movie_trailer);
            mMediaController = new MediaController(getActivity());

            if (intent!=null && intent.hasExtra("id")){
            int position = intent.getIntExtra("id", 0);
            Bundle mBundle = imageAdapter.getItem(position);
            imageView.setImageResource(mBundle.getInt("movieImage"));
            nameView.setText(mBundle.getString("movieName"));
            yearView.setText(mBundle.getString("movieYear"));
            durationView.setText(mBundle.getString("movieDuration"));
            rateView.setText(mBundle.getString("movieRate"));
            overviewView.setText(mBundle.getString("movieOverview"));
            Uri uri = Uri.parse("android.resource://" + getActivity().getPackageName()+"/raw/"+ mBundle.getInt("movieTrailer"));
            Log.d(LOG_TAG, "uri parsed :" + uri);
            trailerView.setVideoURI(uri);
            trailerView.setMediaController(mMediaController);
            trailerView.seekTo(10000);
                        }
            return rootView;
        }

    }
}
