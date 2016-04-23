package mjkarbasianapp.com.popular_movies_version_1.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by family on 4/23/2016.
 */
public class MovieProvider extends ContentProvider {
    //content://AUTHORITY/movie/popular 1.5)content://AUTHORITY/movie/top_rated 2)content://AUTHORITY/movie/id
    // 3)content://AUTHORITY/movie/id/trailer 4)content://AUTHORITY/trailers

    static final int MOVIE_POPULAR = 100;
    static final int MOVIE_TOP_RATED = 101;
    static final int MOVIE_ID_TRAILERS = 200 ;
    static final int MOVIE_ID = 102 ;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
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
}
