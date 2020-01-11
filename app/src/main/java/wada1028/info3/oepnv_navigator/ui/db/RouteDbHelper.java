package wada1028.info3.oepnv_navigator.ui.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import wada1028.info3.oepnv_navigator.ui.db.RouteContract.RouteEntry;

public class RouteDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Route.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RouteEntry.TABLE_NAME + " (" +
                    RouteEntry._ID + " INTEGER PRIMARY KEY," +
                    RouteEntry.COLUMN_NAME_DEP_NAME + " TEXT," +
                    RouteEntry.COLUMN_NAME_DEP_ID + " TEXT," +
                    RouteEntry.COLUMN_NAME_DEST_NAME + " TEXT," +
                    RouteEntry.COLUMN_NAME_DEST_ID + " TEXT," +
                    RouteEntry.COLUMN_NAME_IS_FAV + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RouteEntry.TABLE_NAME;

    public RouteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
