package mjkarbasianapp.com.popular_movies_version_1.Data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import mjkarbasianapp.com.popular_movies_version_1.TestUtilities;

import static mjkarbasianapp.com.popular_movies_version_1.Data.MovieContract.*;

/**
 * Created by family on 4/24/2016.
 */
public class TestProvider extends AndroidTestCase {
    final static String LOG_TAG = TestProvider.class.getSimpleName();

    public void testGetType(){

        String type = mContext.getContentResolver().getType(MovieEntry.buildTopMovieUri());
        assertEquals("Error:  top movie type was not correct", MovieEntry.CONTENT_TYPE, type);

        long moiveID = 213;
        String idTest = mContext.getContentResolver().getType(MovieEntry.buildMovieUri(moiveID));
        assertEquals("Error: movieID uri was not correct", MovieEntry.CONTENT_ITEM_TYPE, idTest);

        String popTest = mContext.getContentResolver().getType(MovieEntry.buildPopularMovieUri());


        String trailTest = mContext.getContentResolver().getType(MovieEntry.buildMovieTrailerUri(moiveID));
        assertEquals("Error: popular movie type was not correct", MovieEntry.CONTENT_TYPE,popTest);
        assertEquals("Error: trailer movie type was not correct", TrailerEntry.CONTENT_TYPE,trailTest);

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
                            " instead of authority: " + CONTENT_AUTHORITY,
                    providerInfo.authority, CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testBasicQuery(){

        MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        ContentValues movieValues = TestUtilities.createMovieValues();
        long movieId = db.insert(MovieEntry.TABLE_NAME,null,movieValues);
        assertTrue("Unable to insert Movie row",movieId!=-1);

        ContentValues trailerValues = TestUtilities.createTrailerValues(movieId);
        long trailerId = db.insert(TrailerEntry.TABLE_NAME,null,trailerValues);
        assertTrue("Unable to insert Movie row",trailerId!=-1);

        db.close();

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.buildPopularMovieUri(),
                null,
                null,
                null,
                null
        );
        assertNotNull("Cursor is null", movieCursor);
        TestUtilities.validateCursor("Cursor does not match expected value", movieCursor, movieValues);

        movieCursor.close();

        Cursor theMovieTrailers = mContext.getContentResolver().query(
                MovieEntry.buildMovieTrailerUri(movieId),
                null,
                null,
                null,
                null
        );
        assertNotNull("Cursor is null", theMovieTrailers);
        TestUtilities.validateCursor("Cursor does not match expected value", theMovieTrailers, trailerValues);
        theMovieTrailers.close();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    private void deleteAllRecords() {
        deleteAllRecordsFromProvider();

    }

    private void deleteAllRecordsFromProvider() {

          mContext.getContentResolver().delete(
            MovieEntry.buildPopularMovieUri(),
            null,
            null
    );

        mContext.getContentResolver().delete(
                TrailerEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.buildPopularMovieUri(),
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

         cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

    }

    public void testUpdate(){

        ContentValues values = TestUtilities.createMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(
                MovieEntry.buildPopularMovieUri(),
                values
        );
        long rowId  = ContentUris.parseId(movieUri);
        // Verify we got a row back.
        assertTrue(rowId != -1);
        Log.d(LOG_TAG, "New row id: " + rowId);

        ContentValues updateValues = new ContentValues(values);
        updateValues.put(MovieEntry._ID,rowId);
        updateValues.put(MovieEntry.COLUMN_VOTE_AVERAGE,"My vote");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.buildPopularMovieUri(),
                null, null, null, null);
        cursor.moveToFirst();
        String voteValue = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));
        Log.d(LOG_TAG,"vote value is: "+ voteValue);
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        cursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(MovieEntry.buildPopularMovieUri(),updateValues, MovieEntry._ID +"="+
                Long.toString(rowId),null);
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        cursor.unregisterContentObserver(tco);
        cursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieUri(rowId),null,null,null,null);
        cursor.moveToFirst();
        voteValue = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));
        Log.d(LOG_TAG,"vote value is: "+ voteValue);
        TestUtilities.validateCursor("testUpdate.  Error validating movie entry update.",cursor,updateValues);
        cursor.close();

    }

    public void testInsertReadProvider(){
        ContentValues testValues = TestUtilities.createMovieValues();
        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.buildPopularMovieUri(), testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieId = ContentUris.parseId(movieUri);

        assertTrue(movieId != -1);

        Cursor cursor = mContext.getContentResolver().query(MovieEntry.buildMovieUri(movieId), null, null, null, null);
        TestUtilities.validateCursor("testInsert.  Error validating movie entry Insert.", cursor, testValues);
        cursor.close();

        ContentValues trailerTestValues = TestUtilities.createTrailerValues(movieId);
        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, tco);
        Uri trailerUri = mContext.getContentResolver().insert(MovieEntry.buildMovieTrailerUri(movieId), trailerTestValues);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        long trailerId = ContentUris.parseId(trailerUri);
        assertTrue(trailerId!=-1);

        cursor = mContext.getContentResolver().query(TrailerEntry.CONTENT_URI,null,null,null,null);
        TestUtilities.validateCursor("testInsert.  Error validating trailer entry Insert.",cursor,trailerTestValues);
        cursor.close();



    }

    public void testDelete(){
        testInsertReadProvider();
        //register observer

        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI,true,movieObserver);

        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI,true,trailerObserver);

        deleteAllRecords();

        movieObserver.waitForNotificationOrFail();
        trailerObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);


    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertMovieValues() {

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++ ) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry._ID,i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE,"Test Movie title"+Integer.toString(i));
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH,"Test Movie backDropPath"+Integer.toString(i));
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,"Test Movie overview"+Integer.toString(i));
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,"TestMovie popularity"+Integer.toString(i));
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,"Test Movie poster path"+Integer.toString(i));
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,"Test Movie release date"+Integer.toString(i));
            movieValues.put(MovieContract.MovieEntry.COLUMN_SITE_ID,"Test movie Site id"+Integer.toString(i));
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,"Test Movie vote average"+Integer.toString(i));
            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
    }

    public void testBulkInsert(){

        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues();
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);
        int movieinserted = mContext.getContentResolver().bulkInsert(MovieEntry.buildPopularMovieUri(), bulkInsertContentValues);

        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals("Bulk insertion failed",movieinserted,BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.buildPopularMovieUri(),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
               null  // sort order
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 9; i > -1; i--, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();



    }
}
