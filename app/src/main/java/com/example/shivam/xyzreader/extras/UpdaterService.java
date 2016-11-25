package com.example.shivam.xyzreader.extras;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.text.format.Time;
import android.util.Log;

import com.example.shivam.xyzreader.data.XYZColumns;
import com.example.shivam.xyzreader.data.XYZProvider;
import com.example.shivam.xyzreader.ui.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by shivam on 15/11/16.
 */

public class UpdaterService extends IntentService {

    OkHttpClient client = new OkHttpClient();

    private static final String LOG_TAG = UpdaterService.class.getName();

    public UpdaterService() {
        super(LOG_TAG);
    }

    public UpdaterService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        URL url;
        String JSONdata;
        JSONArray jsonArray = null;
        // Don't even inspect the intent, we only do one thing, and that's fetch content.
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        //set refreshing true
        EventBus.getDefault().post(new MainActivity.RefreshingState(true));

        try {
            url = new URL("https://dl.dropboxusercontent.com/u/231329/xyzreader_data/data.json");
        } catch (MalformedURLException ignored) {
            Log.e(LOG_TAG, ignored.getMessage());
            return;
        }

        try {
            JSONdata = fetchData(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error fetching items JSON", e);
            return;
        }

        //Parsing JSON
        try {
            JSONTokener tokener = new JSONTokener(JSONdata);
            Object val = tokener.nextValue();
            if (!(val instanceof JSONArray)) {
                throw new JSONException("Expected JSONArray");
            }
            jsonArray = (JSONArray) val;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing items JSON", e);
        }

        Uri dirUri = XYZProvider.XYZ.CONTENT_URI;
        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());

        try {
            if (jsonArray == null) {
                throw new JSONException("Invalid parsed item array");
            }

            Time time = new Time();

            for (int i = 0; i < jsonArray.length(); i++) {
                ContentValues values = new ContentValues();
                JSONObject object = jsonArray.getJSONObject(i);
                values.put(XYZColumns._ID, i);
                values.put(XYZColumns.SERVER_ID, object.getString("id"));
                values.put(XYZColumns.AUTHOR, object.getString("author"));
                values.put(XYZColumns.TITLE, object.getString("title"));
                values.put(XYZColumns.BODY, object.getString("body"));
                values.put(XYZColumns.THUMB_URL, object.getString("thumb"));
                values.put(XYZColumns.PHOTO_URL, object.getString("photo"));
                values.put(XYZColumns.ASPECT_RATIO, object.getDouble("aspect_ratio"));
                time.parse3339(object.getString("published_date"));
                values.put(XYZColumns.PUBLISHED_DATE, time.toMillis(false));
                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            }

            getContentResolver().applyBatch(XYZProvider.AUTHORITY, cpo);

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating content.", e);
        } finally {
            //set refreshing false
            EventBus.getDefault().post(new MainActivity.RefreshingState(false));
        }


    }

    String fetchData(URL url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}

