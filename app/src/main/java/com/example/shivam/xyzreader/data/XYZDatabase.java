package com.example.shivam.xyzreader.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by shivam on 15/11/16.
 */

@Database(version = XYZDatabase.VERSION)
public class XYZDatabase {

    public static final int VERSION = 1;

    @Table(XYZColumns.class)
    public static final String xyz = "xyz";
}
