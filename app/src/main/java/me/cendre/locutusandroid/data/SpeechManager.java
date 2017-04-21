package me.cendre.locutusandroid.data;

/**
 * Created by guillaumecendre on 28/12/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import me.cendre.locutusandroid.R;

import static android.content.Context.MODE_PRIVATE;
import static me.cendre.locutusandroid.data.AvailableSpeechAdapter.availableVoiceTypesKey;


public class SpeechManager extends SQLiteOpenHelper {


    public static final String TABLE_LOCUTUSSPEECH = "Speech";
    private static final int DATABASE_VERSION = 6;

    private static final String DATABASE_NAME = "Locutus";
    private static final String KEY_TYPE = "type";
    private static final String KEY_NAME = "name";
    private static final String KEY_FILE_NAME = "filename";
    public static SpeechManager defaultManager = null;
    private static Context ctx;


    public SpeechManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;

    }

    public static void initDefaultManager(Context context) {

        defaultManager = new SpeechManager(context);
        ctx = context;

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!LocutusApplication.tableExists(TABLE_LOCUTUSSPEECH, db)) {

            onCreate(db);

        }

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOCUTUSSPEECH_TABLE = "CREATE TABLE " + TABLE_LOCUTUSSPEECH + "("
                + KEY_NAME + " TEXT,"
                + KEY_FILE_NAME + " TEXT PRIMARY KEY,"
                + KEY_TYPE + " TEXT)";
        db.execSQL(CREATE_LOCUTUSSPEECH_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("LocutusDB", "Upgraded LocutusSpeech from " + oldVersion + " to " + newVersion);
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCUTUSSPEECH);
            onCreate(db);
        }

    }


    public List<String> getAvailableSpeechTypes() {

        ArrayList<String> list = new ArrayList<>();

        SharedPreferences prefs = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key), MODE_PRIVATE);
        String typesJson = prefs.getString(availableVoiceTypesKey, "[]");
        try {

            JSONArray types = new JSONArray(typesJson);
            for (int i = 0; i < types.length(); i++) {
                if (!list.contains(types.getString(i))) {
                    list.add(types.getString(i));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;

    }


    public void wipeTable(String tableName) {

        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(this.getWritableDatabase());

    }


}
