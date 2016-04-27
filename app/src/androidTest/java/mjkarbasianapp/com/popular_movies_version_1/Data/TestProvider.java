package mjkarbasianapp.com.popular_movies_version_1.Data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by family on 4/24/2016.
 */
public class TestProvider extends AndroidTestCase {

    public void testGetType(){

        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildTopMovieUri());
        assertEquals("Error:  top movie type was not correct", MovieContract.MovieEntry.CONTENT_TYPE, type);

        long moiveID = 213;
        String idTest = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieUri(moiveID));
        assertEquals("Error: movieID uri was not correct", MovieContract.MovieEntry.CONTENT_ITEM_TYPE, idTest);

        String popTest = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildPopularMovieUri());


        String trailTest = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieTrailerUri(moiveID));
        Log.d("TestProvider",trailTest);
        assertEquals("Error: popular movie type was not correct", MovieContract.MovieEntry.CONTENT_TYPE,popTest);
        assertEquals("Error: trailer movie type was not correct", MovieContract.TrailerEntry.CONTENT_TYPE,trailTest);

    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }
}
