package mjkarbasianapp.com.popular_movies_version_1.Data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by family on 4/23/2016.
 */
public class MovieProvider extends ContentProvider {

    final static String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher  = buildUriMatcher();
    private  MovieDbHelper movieHelper;

    //content://AUTHORITY/movie/popular 1.5)content://AUTHORITY/movie/top_rated 2)content://AUTHORITY/movie/id
    // 3)content://AUTHORITY/movie/id/trailer 4)content://AUTHORITY/trailers

    static final int MOVIE_POPULAR = 100;
    static final int MOVIE_TOP_RATED = 101;
    static final int MOVIE_ID_TRAILERS = 200 ;
    static final int MOVIE_ID = 102 ;

    @Override
    public boolean onCreate() {
        movieHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case (MOVIE_POPULAR):
            {
                retCursor = movieHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,projection,
                        selection,selectionArgs,null,null, MovieContract.MovieEntry.COLUMN_POPULARITY +" DESC");

                break;
            }
            case (MOVIE_TOP_RATED):
            {
                retCursor = movieHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,projection,
                        selection,selectionArgs,null,null, MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE +" DESC");
                break;
            }
            case (MOVIE_ID):
            {
                String movieID = uri.getLastPathSegment();
                retCursor = movieHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,projection,
                        MovieContract.MovieEntry._ID+"="+movieID ,selectionArgs,null,null, MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE +" DESC");
                break;
            }
            case MOVIE_ID_TRAILERS:
            {
                List<String> paths =uri.getPathSegments();
                String movieID = paths.get(1);
                retCursor = movieHelper.getReadableDatabase().query(MovieContract.TrailerEntry.TABLE_NAME,projection,
                        MovieContract.MovieEntry._ID+"="+movieID,selectionArgs,null,null,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri :"+uri);

        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_POPULAR:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_TOP_RATED:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID_TRAILERS:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :"+uri.toString());

        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri))
        {
            case (MOVIE_POPULAR|MOVIE_TOP_RATED):
            {
                long _id =  db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                if(_id>0){
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case (MOVIE_ID_TRAILERS):
            {
                long _id =  db.insert(MovieContract.TrailerEntry.TABLE_NAME,null,values);
                if(_id>0){
                    returnUri = MovieContract.MovieEntry.buildMovieTrailerUri(_id);
                }else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri: "+ uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = movieHelper.getWritableDatabase();
        int rowsDeleted;
        if(null==selection)selection="1";
        switch (sUriMatcher.match(uri))
        {
            case(MOVIE_POPULAR|MOVIE_TOP_RATED):
            {
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            case (MOVIE_ID_TRAILERS):
            {
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri: "+ uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieHelper.getWritableDatabase();
        int rowsUpdated;
        switch (sUriMatcher.match(uri))
        {
            case(MOVIE_POPULAR|MOVIE_TOP_RATED):
            {
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            }
            case (MOVIE_ID_TRAILERS):
            {
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri: "+ uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = movieHelper.getWritableDatabase();
        int returnCount = 0;
        switch (sUriMatcher.match(uri))
        {
            case(MOVIE_POPULAR|MOVIE_TOP_RATED):{
                db.beginTransaction();
                try{
                    for (ContentValues value:values){
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,value);
                        if(_id!=-1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
            }
            case (MOVIE_ID_TRAILERS):
            {
                db.beginTransaction();
                try{
                    for (ContentValues value:values){
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME,null,value);
                        if(_id!=-1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                break;
            }
            default:
                return super.bulkInsert(uri, values);
        }
        return returnCount;
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE+"/popular" ,MOVIE_POPULAR);
        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE+"/top_rated",MOVIE_TOP_RATED);
        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE+"/#",MOVIE_ID);
        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE+"/#"+"/trailer",MOVIE_ID_TRAILERS);

        return uriMatcher;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        movieHelper.close();
        super.shutdown();
    }
}
