package mjkarbasianapp.com.popular_movies_version_1;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import mjkarbasianapp.com.popular_movies_version_1.Data.MovieContract;

/**
 * Created by family on 4/15/2016.
 */
public class TestUtilities extends AndroidTestCase {

    public static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE,"Test movie title");
        movieValues.put(MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH,"Test movie backDropPath");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,"Test movie overview");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,"Test movie popularity");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,"Test movie poster path");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,"Test movie release date");
        movieValues.put(MovieContract.MovieEntry.COLUMN_SITE_ID,"Test movie site id");
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,"Test movie vote average");
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
}
