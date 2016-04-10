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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;


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
    public static class DetailFragment extends Fragment {
        private static final String BASE_PIC_URI = "http://image.tmdb.org/t/p/w185/";
        MediaController  mMediaController;
        private static String LOG_TAG = DetailFragment.class.getSimpleName();
        private String[] movieData;

        public DetailFragment() {
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ImageAdapter imageAdapter = new ImageAdapter(getActivity(),new ArrayList<JSONObject>());
            Intent intent = getActivity().getIntent();
            ImageView imageView = (ImageView)rootView.findViewById(R.id.movie_detail_image);
            TextView nameView = (TextView)rootView.findViewById(R.id.movie_name);
            TextView yearView = (TextView)rootView.findViewById(R.id.movie_year);
            TextView durationView = (TextView)rootView.findViewById(R.id.movie_duration);
            TextView rateView = (TextView)rootView.findViewById(R.id.movie_ratings);
            RatingBar ratingBar =(RatingBar) rootView.findViewById(R.id.ratingBar);
            TextView overviewView = (TextView)rootView.findViewById(R.id.movie_description);
            if (intent!=null && intent.hasExtra("movieData")){
               movieData = intent.getStringArrayExtra("movieData");
                if(movieData!=null){
                    nameView.setText(movieData[0]);
                    yearView.setText(movieData[5]);
                    durationView.setText(movieData[6]);
                    rateView.setText(movieData[4]);
                    overviewView.setText(movieData[1]);
                    ratingBar.setRating((Float.parseFloat(movieData[4]))/2);
                    String posterPath = movieData[2];
                    String url = BASE_PIC_URI + posterPath;
                    Picasso.with(getActivity()).load(url).into(imageView);
                    return rootView;  }
                return null;

            }
            return null;

        }
    }
}
