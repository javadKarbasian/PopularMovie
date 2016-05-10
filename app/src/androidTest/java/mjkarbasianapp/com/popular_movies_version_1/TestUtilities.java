package mjkarbasianapp.com.popular_movies_version_1;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import android.util.Log;

import mjkarbasianapp.com.popular_movies_version_1.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

import mjkarbasianapp.com.popular_movies_version_1.Data.MovieContract;

/**
 * Created by family on 4/15/2016.
 */
public class TestUtilities extends AndroidTestCase {

    /*
       Students: The functions we provide inside of TestProvider use this utility class to test
       the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
       CTS tests.

       Note that this only tests that the onChange function is called; it does not test that the
       correct Uri is returned.
    */
    public static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    public static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    public static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry._ID,1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE,"Test Movie title");
        movieValues.put(MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH,"Test Movie backDropPath");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,"Test Movie overview");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,"TestMovie popularity");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,"Test Movie poster path");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,"Test Movie release date");
        movieValues.put(MovieContract.MovieEntry.COLUMN_SITE_ID,"Test movie Site id");
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,"Test Movie vote average");
        return movieValues;
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public static ContentValues createTrailerValues(long movieId) {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry._ID,1);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY,"Test trailer key");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOV_ID,movieId);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_SITE,"Test trailer site");

        return trailerValues;
    }

    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }


}
