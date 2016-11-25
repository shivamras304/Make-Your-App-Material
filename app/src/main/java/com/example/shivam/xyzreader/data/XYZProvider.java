package com.example.shivam.xyzreader.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import static com.example.shivam.xyzreader.data.XYZDatabase.xyz;

/**
 * Created by shivam on 15/11/16.
 */

@ContentProvider(authority = XYZProvider.AUTHORITY, database = XYZDatabase.class)
public class XYZProvider {

    public static final String AUTHORITY = "com.example.shivam.xyzreader.data.XYZProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String XYZ = "xyz";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = xyz)
    public static class XYZ {
        @ContentUri(
                path = Path.XYZ,
                type = "vnd.android.cursor.dir/xyz",
                defaultSort = XYZColumns.PUBLISHED_DATE + " DESC"
        )
        public static final Uri CONTENT_URI = buildUri(Path.XYZ);

        @InexactContentUri(
               name = "SELECTED_XYZ",
                path = Path.XYZ + "/#",
                type = "vnd.android.cursor.item/xyz",
                whereColumn = XYZColumns._ID,
                pathSegment = 1
        )
        public static Uri withId(long id) {
            String _id = "" + id;
            return buildUri(Path.XYZ, _id);
        }

        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getLastPathSegment());
        }
    }
}
