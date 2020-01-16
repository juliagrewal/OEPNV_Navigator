package wada1028.info3.oepnv_navigator.ui.db;

import android.provider.BaseColumns;

public final class RouteContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private RouteContract() {}

    /* Inner class that defines the table contents */
    public static class RouteEntry implements BaseColumns {
        public static final String TABLE_NAME = "route";
        public static final String COLUMN_NAME_DEP_NAME = "dep_name";
        public static final String COLUMN_NAME_DEP_ID = "dep_id";
        public static final String COLUMN_NAME_DEST_NAME = "dest_name";
        public static final String COLUMN_NAME_DEST_ID = "dest_id";
        public static final String COLUMN_NAME_IS_FAV = "is_fav";
    }

}
