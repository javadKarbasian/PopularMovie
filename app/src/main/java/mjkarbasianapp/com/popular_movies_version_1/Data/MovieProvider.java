package mjkarbasianapp.com.popular_movies_version_1.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by family on 4/23/2016.
 */
public class MovieProvider extends ContentProvider {

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
            case (MOVIE_POPULAR|MOVIE_TOP_RATED|MOVIE_ID):
            {
                retCursor = movieHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,projection,
                        selection,selectionArgs,null,null,sortOrder);
                break;
            }
            case MOVIE_ID_TRAILERS:
            {
                retCursor = movieHelper.getReadableDatabase().query(MovieContract.TrailerEntry.TABLE_NAME,projection,
                        selection,selectionArgs,null,null,sortOrder);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        Log.d("MovieProvider","match is :"+ Integer.toString(match));
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
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
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
}
