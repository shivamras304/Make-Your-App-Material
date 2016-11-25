package com.example.shivam.xyzreader.extras;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.example.shivam.xyzreader.data.XYZColumns;
import com.example.shivam.xyzreader.data.XYZProvider;

/**
 * Created by shivam on 15/11/16.
 */

public class ArticleLoader extends CursorLoader {

    public ArticleLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, null);
    }

    public static ArticleLoader getAllArticlesInstance(Context context) {
        return new ArticleLoader(context, XYZProvider.XYZ.CONTENT_URI);
    }

    public static ArticleLoader getInstanceForItemId(Context context, long itemId) {
        return new ArticleLoader(context, XYZProvider.XYZ.withId(itemId));
    }

    public interface Query {
        String[] PROJECTION = {
                XYZColumns._ID,
                XYZColumns.TITLE,
                XYZColumns.PUBLISHED_DATE,
                XYZColumns.AUTHOR,
                XYZColumns.THUMB_URL,
                XYZColumns.PHOTO_URL,
                XYZColumns.ASPECT_RATIO,
                XYZColumns.BODY,
        };

        int _ID = 0;
        int TITLE = 1;
        int PUBLISHED_DATE = 2;
        int AUTHOR = 3;
        int THUMB_URL = 4;
        int PHOTO_URL = 5;
        int ASPECT_RATIO = 6;
        int BODY = 7;
    }
}
