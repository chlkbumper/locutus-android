package me.cendre.locutusandroid.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.cendre.locutusandroid.R;

/**
 * me.cendre.locutusandroid
 * Created by guillaume on 16/06/2016.
 */
public class LocutusApplication extends Application {

    public static SharedPreferences sharedPref;

    public static String getBasePath(Context ctx) {

        if (ctx != null) {
            if (sharedPref == null) {
                sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            }
            if (sharedPref.contains(ctx.getString(R.string.locutus_folder_root))) {

                return sharedPref.getString(ctx.getString(R.string.locutus_folder_root), "/");

            }
        } else {

            Log.e("Locutus", "Error: LocutusApplication is trying to get base path from SharedPreferences with null Context");

        }
        return "/";

    }


    //Shared by all managers (speech, concept, profile)

    public static boolean tableExists(String tableName, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }


}
