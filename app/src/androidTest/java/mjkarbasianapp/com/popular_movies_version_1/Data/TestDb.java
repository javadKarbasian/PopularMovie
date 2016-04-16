package mjkarbasianapp.com.popular_movies_version_1.Data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

import mjkarbasianapp.com.popular_movies_version_1.TestUtilities;

/**
 * Created by family on 4/15/2016.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase(){ mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);}

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp(){ deleteTheDatabase();}

    public void testCreateDb() throws Throwable{

    final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);
        deleteTheDatabase();
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );


        // if this fails, it means that your database doesn't contain both the Movie entry
        // and Trailer entry tables
        assertTrue("Error: Your database was created without both the Movie entry and Trailer entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.TrailerEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> trailerColumnHashSet = new HashSet<String>();
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_MOV_ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_SITE);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY);
        trailerColumnHashSet.add(MovieContract.TrailerEntry._ID);

        int columnNameIndex = c.getColumnIndex("name");
        do {

            String columnName = c.getString(columnNameIndex);
            Log.d(LOG_TAG, columnName);
            trailerColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required trailer entry columns",
                trailerColumnHashSet.isEmpty());
        c.close();

        c=db.rawQuery( "PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_SITE_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            Log.d(LOG_TAG, columnName);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                trailerColumnHashSet.isEmpty());
        c.close();
        db.close();

    }

    public void testTrailerTable(){
        long movieRowId = insertMovieTable();
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createTrailerValues(movieRowId);
        long trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, testValues);
        assertTrue(trailerRowId != -1);

        Cursor cursor =  db.query(MovieContract.TrailerEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        assertTrue("Error: No Records returned from location query",cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Trailer Query Validation Failed",cursor,testValues);
        assertFalse("Error: More than one record returned from location query",
                cursor.moveToNext());
        cursor.close();
        db.close();
    }

    public long testMovieTable(){
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();
        long movieRowId;
        movieRowId = db.insert( MovieContract.MovieEntry.TABLE_NAME,null,testValues);
        assertTrue(movieRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        assertTrue("Error: No Records returned from location query",cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed",
                cursor, testValues);

        assertFalse("Error: More than one record returned from location query",
                cursor.moveToNext());

        cursor.close();
        db.close();
        return movieRowId;
    }

    public long insertMovieTable(){

    MovieDbHelper dbHelper = new MovieDbHelper(mContext);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues testValues = TestUtilities.createMovieValues();
    long movieRowId;
    movieRowId = db.insert( MovieContract.MovieEntry.TABLE_NAME,null,testValues);
    return movieRowId;
}


}
