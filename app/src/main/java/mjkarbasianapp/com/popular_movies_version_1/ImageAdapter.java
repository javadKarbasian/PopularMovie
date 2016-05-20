package mjkarbasianapp.com.popular_movies_version_1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import mjkarbasianapp.com.popular_movies_version_1.Data.MovieContract;

/**
 * Created by family on 3/5/2016.
 */
public class ImageAdapter extends CursorAdapter {
    private Context mContext;
    public String[] mPosterPath = null;
    public String[] backDropPath;
    public String[] mPopularity;
    private static String BASE_PIC_URI = "http://image.tmdb.org/t/p/w185/";
    private final Object mLock = new Object();
    private List<JSONObject> mObjects;
    //private final LayoutInflater mInflater;
    private String posterPath;


    public ImageAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }


//    @Override
//    public JSONObject getItem(int position) {
//       return mObjects.get(position);
//    }

//    @Override
//    public int getCount() {
//        return mObjects.size();
//    }

//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }


    int taskA = 0;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView iView = new ImageView(context);
        iView.setLayoutParams(new GridView.LayoutParams(500, 900));
        taskA++;
        Log.d("task s", taskA + " count");
        return iView;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView imageView = (ImageView) view;
        posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        String url = BASE_PIC_URI + posterPath;
        if (mContext != null && url != null && imageView != null) {
            Picasso.with(mContext).load(url).into(imageView);
        }

    }

}