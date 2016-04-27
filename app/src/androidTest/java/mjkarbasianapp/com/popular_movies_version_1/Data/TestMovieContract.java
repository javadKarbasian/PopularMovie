package mjkarbasianapp.com.popular_movies_version_1.Data;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;

/**
 * Created by family on 4/24/2016.
 */
public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_MOVIE_POPULAR = "movie";
    private static final long TEST_MOVIE_ID = 28329;
    private static final String TEST_MOVIE_TRAILER = "top_rated";
    private static final String LOG_TAG = TestMovieContract.class.getSimpleName();

    public void testBuildMovieUri() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);
        Log.d(LOG_TAG, "created uri is: " + movieUri.toString());

        Uri trailerUri = MovieContract.MovieEntry.buildTopMovieUri();
        List<String> paths = movieUri.getPathSegments();
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieiD in " +
                        "MovieContract.",
                movieUri);

        assertEquals("Error: movie  not properly appended to the end of the Uri",
                TEST_MOVIE_POPULAR,paths.get(0));

        assertEquals("trailer sort order not properly appended to the end of the Uri",TEST_MOVIE_TRAILER,trailerUri.getLastPathSegment());
    }
}
