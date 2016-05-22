package mjkarbasianapp.com.popular_movies_version_1.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import mjkarbasianapp.com.popular_movies_version_1.Data.MovieContract;
import mjkarbasianapp.com.popular_movies_version_1.R;
import mjkarbasianapp.com.popular_movies_version_1.Utility;

/**
 * Created by family on 5/22/2016.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        String[] params = new String[2];
        params[1]="1";
        params[0]=Utility.getSortSetting(getContext());
        //if there is no sort mode we could not show anythings
        if (params.length == 0) {
            Log.v(LOG_TAG, "The params is null! please Check!");
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String MovieJsonStr = null;
        String pageNum = params[1];


        String movieJsonStr = null;
        try {
            // Construct the URL for the http://api.themoviedb.org/3/movie/popular

            final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";
            final String PAGE_PARAM = "page";
            final String LANG_PARAM = "language";
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(PAGE_PARAM, pageNum)
                    .appendQueryParameter(LANG_PARAM, "en")
                    .appendQueryParameter(APPID_PARAM, "c4cba1cb560e5692f0a51575d4f1a145")
                    .build();
            Log.v(LOG_TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
//                    return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Connection Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
//                return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private void getMovieDataFromJson(String movieJsonStr)  throws JSONException {


        final String OWM_RESULTS = "results";

        try{
            // getting main data of movie query
            if(movieJsonStr==null)return;
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());


            JSONObject movie;
            for (int i=0;i<movieArray.length();i++){
                ContentValues contentValues = new ContentValues();
                movie = movieArray.getJSONObject(i);
                contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, Utility.getMovieDataFromJson(movie)[0]);
                contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,Utility.getMovieDataFromJson(movie)[1]);
                contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,Utility.getMovieDataFromJson(movie)[2]);
                contentValues.put(MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH,Utility.getMovieDataFromJson(movie)[3]);
                contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,Utility.getMovieDataFromJson(movie)[4]);
                contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,Utility.getMovieDataFromJson(movie)[5]);
                contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,Utility.getMovieDataFromJson(movie)[6]);
                contentValues.put(MovieContract.MovieEntry.COLUMN_SITE_ID,Utility.getMovieDataFromJson(movie)[7]);
                cVVector.add(contentValues);
            }
            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.buildPopularMovieUri(), cvArray);
            }
            Log.v(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");

        }
        catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }
    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
       MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

}
