package wada1028.info3.oepnv_navigator.ui.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import wada1028.info3.oepnv_navigator.ui.db.RouteContract.RouteEntry;

public class RouteDB {
    private SQLiteDatabase db = null;
    private RouteDbHelper dbHelper = null;

    // Define 'where' part of queries
    private String selection = RouteEntry.COLUMN_NAME_DEP_NAME + " LIKE ? AND " +
            RouteEntry.COLUMN_NAME_DEST_NAME + " LIKE ? ";

    public RouteDB(Context context){
        dbHelper = new RouteDbHelper(context);
    }

    public void connect(){
        db = dbHelper.getWritableDatabase();
    }

    public void disconnect(){
        dbHelper.close();
        db = null;
    }

    public long insertRoute(String depName, String depID, String destName, String destID){

        if (db == null){
            Log.e("DBERROR", "no database");
            return -1;
        }

        int isRouteAvailable = isRouteAlreadyStored(depName, destName);
        if (isRouteAvailable != 0){
            return 0;
        }

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(RouteContract.RouteEntry.COLUMN_NAME_DEP_NAME, depName);
        values.put(RouteContract.RouteEntry.COLUMN_NAME_DEP_ID, depID);
        values.put(RouteContract.RouteEntry.COLUMN_NAME_DEST_NAME, destName);
        values.put(RouteContract.RouteEntry.COLUMN_NAME_DEST_ID, destID);
        values.put(RouteContract.RouteEntry.COLUMN_NAME_IS_FAV, "N");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(RouteEntry.TABLE_NAME, null, values);
        return newRowId;
    }

    public ArrayList<Route> getAllRoutes(){

        ArrayList<Route> result = new ArrayList<>();

        if (db == null){
            Log.e("DBERROR", "no database");
            return result;
        }


        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                RouteEntry.COLUMN_NAME_IS_FAV + " DESC, " +
                RouteEntry._ID + " DESC ";

        Cursor cursor = db.query(
                RouteEntry.TABLE_NAME,  // The table to query
                null,           // The array of columns to return (pass null to get all)
                null,           // The columns for the WHERE clause
                null,        // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                sortOrder               // The sort order
        );

        while(cursor.moveToNext()) {
            Route route = new Route();
            route.routeID = cursor.getLong(cursor.getColumnIndexOrThrow(RouteEntry._ID));
            route.depName = cursor.getString(cursor.getColumnIndexOrThrow(RouteEntry.COLUMN_NAME_DEP_NAME));
            route.depID = cursor.getString(cursor.getColumnIndexOrThrow(RouteEntry.COLUMN_NAME_DEP_ID));
            route.destName = cursor.getString(cursor.getColumnIndexOrThrow(RouteEntry.COLUMN_NAME_DEST_NAME));
            route.destID = cursor.getString(cursor.getColumnIndexOrThrow(RouteEntry.COLUMN_NAME_DEST_ID));
            route.isFav = cursor.getString(cursor.getColumnIndexOrThrow(RouteEntry.COLUMN_NAME_IS_FAV));

            result.add(route);
        }
        cursor.close();

        return result;
    }

    public int deleteRoute (String depName, String destName){

        if (db == null){
            Log.e("DBERROR", "no database");
            return -1;
        }

        // Specify arguments in placeholder order.
        String[] selectionArgs = { depName, destName };
        // Issue SQL statement.
        int deletedRows = db.delete(RouteEntry.TABLE_NAME,selection, selectionArgs);

        return deletedRows;
    }

    public int setRouteFav (String depName, String destName, boolean isFav){
        if (db == null){
            Log.e("DBERROR", "no database");
            return -1;
        }

        // Specify arguments in placeholder order.
        String[] selectionArgs = { depName, destName };

        String favValue = "N";
        if (isFav){
            favValue = "Y";
        }

        ContentValues values = new ContentValues();
        values.put(RouteEntry.COLUMN_NAME_IS_FAV, favValue);

        int count = db.update(
                RouteEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }

    public int isRouteAlreadyStored (String depName, String destName){
        if (db == null){
            Log.e("DBERROR", "no database");
            return -1;
        }

        // Specify arguments in placeholder order.
        String[] selectionArgs = { depName, destName };

        Cursor cursor = db.query(
                RouteEntry.TABLE_NAME,  // The table to query
                null,           // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // The sort order
        );

        int result = cursor.getCount();
        cursor.close();
        return result;
    }

}
