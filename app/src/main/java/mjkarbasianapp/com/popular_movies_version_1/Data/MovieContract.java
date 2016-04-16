package mjkarbasianapp.com.popular_movies_version_1.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by family on 4/12/2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "mjkarbasianapp.com.popular_movies_version_1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    //List of out uri`s are : 1)content://AUTHORITY/movie 2)content://AUTHORITY/movie/id
    // 3)content://AUTHORITY/movie/id/videos 4)content://AUTHORITY/movie/trailers

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

    //Defining columns
    public static final String TABLE_NAME = "movie";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_POSTER_PATH = "poster_path";
    public static final String COLUMN_BACK_DROP_PATH = "back_drop_path";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_VOTE_AVERAGE = "vote_average";
    public static final String COLUMN_SITE_ID = "site_id";

}
    public static final class TrailerEntry implements BaseColumns{
        //Defining Columns
        public static final String TABLE_NAME = "trailer";
        public static final String COLUMN_TRAILER_KEY = "trailer_key";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_MOV_ID = "movie_id";
    }

}
