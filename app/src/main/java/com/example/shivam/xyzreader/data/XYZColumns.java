package com.example.shivam.xyzreader.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by shivam on 15/11/16.
 */

public class XYZColumns {

    /** Type: INTEGER PRIMARY KEY AUTOINCREMENT */
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    public static final String _ID = "_id";

    /** Type: TEXT */
    @DataType(DataType.Type.TEXT)
    public static final String SERVER_ID = "server_id";

    /** Type: TEXT NOT NULL */
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String TITLE = "title";

    /** Type: TEXT NOT NULL */
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String AUTHOR = "author";

    /** Type: TEXT NOT NULL */
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String BODY = "body";

    /** Type: TEXT NOT NULL */
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String THUMB_URL = "thumb";

    /** Type: TEXT NOT NULL */
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String PHOTO_URL = "photo";

    /** Type: REAL NOT NULL DEFAULT 1.5 */
    @DataType(DataType.Type.REAL)
    @NotNull
    @DefaultValue("1.5")
    public static final String ASPECT_RATIO = "aspect_ratio";

    /** Type: TEXT NOT NULL DEFAULT 0 */
    @DataType(DataType.Type.TEXT)
    @NotNull
    @DefaultValue("0")
    public static final String PUBLISHED_DATE = "published_date";

}
