package mjkarbasianapp.com.popular_movies_version_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by family on 3/5/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public String[] mPosterPath = null;
    public String[] backDropPath;
    public String[] mPopularity;
    private static String BASE_PIC_URI = "http://image.tmdb.org/t/p/w185/";
    private final Object mLock = new Object();
    private List<JSONObject> mObjects;
    private final LayoutInflater mInflater;
    private String posterPath;


    public ImageAdapter(Context context,List<JSONObject> objects) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObjects = objects;
    }



    @Override
    public JSONObject getItem(int position) {
       return mObjects.get(position);
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView ;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }



        try {
            posterPath = Utility.getMovieDataFromJson(getItem(position))[2];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = BASE_PIC_URI + posterPath;
            Picasso.with(mContext).load(url).into(imageView);


        return imageView;

    }

    // references to our images
    public  Integer[] mThumbIds = {
            R.drawable.pic1, R.drawable.pic2,
            R.drawable.pic3, R.drawable.pic4,
            R.drawable.pic5,R.drawable.pic6
    };
    //video Names
    public  String[] mNames = {
      "Star Wars","Scream",
      "the Silence of The Lambs","Snow White",
      "Pirates of the Caribbean","Finding The Nemo"
    };
    //Video Year
    public  String[] mYear = {
      "2010","2008","2002","2000","2015","2011"
    };
    //Video Time
    public String[] mDuration = {
       "120min","280min","150min","180min","110min","200min"
    };
    //ratings
    public  String[] mRates = {
        "6.5/10","8/10","5/10","7/10","9/10","8.5/10"
    };
    //movie overview
    public  String[] mOverview = {
        "Thirty years after defeating the Galactic Empire, Han Solo and his allies face a new threat from the evil Kylo Ren and his army of Stormtroopers.",
        "What starts as a YouTube video going viral, soon leads to problems for the teenagers of Lakewood and serves as the catalyst for a murder that opens up a window to the town's troubled past. Everyone has secrets. Everyone tells lies. Everyone is fair game.",
        "FBI trainee Clarice Starling ventures into a maximum-security asylum to pick the diseased brain of Hannibal Lecter, a psychiatrist turned homicidal cannibal. Starling needs clues to help her capture a serial killer. ",
        "After the Evil Queen marries the King, she performs a violent coup in which the King is murdered and his daughter, Snow White, is taken captive. Almost a decade later, a grown Snow White is still in the clutches of the Queen. In order to obtain immortality, The Evil Queen needs the heart of Snow White. After Snow escapes the castle, the Queen sends the Huntsman to find her in the Dark Forest. ",
        "Captain Jack Sparrow crosses paths with a woman from his past, and he's not sure if it's love -- or if she's a ruthless con artist who's using him to find the fabled Fountain of Youth",
        "A tale which follows the comedic and eventful journeys of two fish, the fretful Marlin and his young son Nemo, who are separated from each other in the Great Barrier Reef when Nemo is unexpectedly taken from his home and thrust into a fish tank in a dentist's office overlooking Sydney Harbor."
    };
    private Integer[] mTrailers ={
            R.raw.trailer_starwars,R.raw.trailer_scream,
            R.raw.trailer1_ilence,R.raw.trailer_snowwhite,
            R.raw.trailer_caribbean,R.raw.trailer_nemo
    };
    private static String[] mID;


    public void clear() {
        synchronized (mLock) {
                mObjects.clear();
        }
        notifyDataSetChanged();

    }

    public void add(JSONObject object) {
        synchronized (mLock) {
            mObjects.add(object);
        }
        notifyDataSetChanged();
    }
}
