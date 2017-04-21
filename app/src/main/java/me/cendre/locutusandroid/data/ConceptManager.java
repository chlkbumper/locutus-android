package me.cendre.locutusandroid.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * me.cendre.locutusandroid.data
 * Created by guillaume on 08/09/2016.
 */
public class ConceptManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "Locutus";
    private static final String TABLE_LOCUTUSCONCEPTS = "Concepts";
    private static final String KEY_NAME = "name";
    private static final String KEY_FILE_NAMES = "filenames";
    private static final String KEY_VOICES = "voices";
    @SuppressLint("StaticFieldLeak")
    public static ConceptManager defaultManager = null;
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;


    public ConceptManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;

    }

    public static void initDefaultManager(Context context) {

        ctx = context;
        defaultManager = new ConceptManager(context);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!LocutusApplication.tableExists(TABLE_LOCUTUSCONCEPTS, db)) {
            onCreate(db);
        }
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOCUTUSCONCEPTS_TABLE = "CREATE TABLE " + TABLE_LOCUTUSCONCEPTS + "("
                + KEY_NAME + " TEXT PRIMARY KEY,"
                + KEY_FILE_NAMES + " TEXT,"
                + KEY_VOICES + " TEXT)";
        db.execSQL(CREATE_LOCUTUSCONCEPTS_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("LocutusDB", "Upgraded LocutusConcepts from " + oldVersion + " to " + newVersion);
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCUTUSCONCEPTS);

            onCreate(db);
        }

    }

    public void addLocutusConcept(LocutusConcept locutusConcept) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, locutusConcept.getName());
        values.put(KEY_FILE_NAMES, locutusConcept.getJSONFilenames());
        values.put(KEY_VOICES, locutusConcept.getJSONSpeech());

        db.insert(TABLE_LOCUTUSCONCEPTS, null, values);
        db.close();
    }

    public LocutusConcept getLocutusConcept(String conceptName) {

        if (conceptName.equals("")) return null;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = new String[1];
        args[0] = conceptName;
        String[] column = {KEY_NAME, KEY_FILE_NAMES, KEY_VOICES};
        Cursor cursor = db.query(TABLE_LOCUTUSCONCEPTS, column, KEY_NAME + " = ?", args, null, null, null);

        if (cursor.moveToNext()) {

            LocutusConcept locutusConcept = new LocutusConcept(cursor.getString(0));

            try {
                locutusConcept.setFilenames(new JSONObject(cursor.getString(1)));
                if (cursor.getString(2) != null) {
                    String speechJson = cursor.getString(2);
                    locutusConcept.setJSONSpeech(new JSONArray(speechJson));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            cursor.close();

            return locutusConcept;


        } else {
            Log.wtf("Locutus", "Request returned null cursor");
        }

        return null;


    }

    public List<LocutusConcept> getAllLocutusConcepts() {

        List<LocutusConcept> locutusConceptList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LOCUTUSCONCEPTS;

        SQLiteDatabase db = this.getReadableDatabase();

        try {

            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    LocutusConcept locutusConcept = new LocutusConcept(cursor.getString(0));
                    try {
                        locutusConcept.setFilenames(new JSONObject(cursor.getString(1)));
                        locutusConcept.setJSONSpeech(new JSONArray(cursor.getString(2)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    locutusConceptList.add(locutusConcept);
                } while (cursor.moveToNext());
            }

            cursor.close();

            return locutusConceptList;

        } catch (SQLiteException e) {

            return new ArrayList<>();

        }
    }

    public List<String> getAllLocutusConceptsString() {

        ArrayList<String> conceptNames = new ArrayList<>();
        List<LocutusConcept> concepts = getAllLocutusConcepts();
        for (LocutusConcept concept : concepts) {

            conceptNames.add(concept.getName());

        }
        return conceptNames;

    }


    public int updateLocutusConcept(LocutusConcept locutusConcept) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, locutusConcept.getName());
        values.put(KEY_FILE_NAMES, locutusConcept.getJSONFilenames());
        values.put(KEY_VOICES, locutusConcept.getJSONSpeech());

        return db.update(TABLE_LOCUTUSCONCEPTS, values, KEY_NAME + " = ?",
                new String[]{String.valueOf(locutusConcept.getName())});
    }

    void deleteLocutusConcept(String conceptName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCUTUSCONCEPTS, KEY_NAME + " = ?",
                new String[]{conceptName});
        db.close();
    }


    private String filenameFromPath(String path) {

        return new File(path).getName();

    }

    public boolean isAudio(String filename) {

        List<String> audioExtensions = Arrays.asList("mp3", "ogg", "wav", "aiff", "aac", "flac", "m4a");

        File f = new File(filename);
        String p = Pattern.quote(".");
        if (f.getName().split(p).length >= 2) {
            if (audioExtensions.contains(f.getName().split(p)[f.getName().split(p).length - 1])) {
                return true;
            }
        }

        return false;

    }

    public String getFirstUnusedFiles(@Nullable String atPath) {

        List<String> knownFiles = getAllKnownFiles();

        String path = LocutusApplication.getBasePath(ctx);
        if (atPath != null) {
            path = atPath;
        }

        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (!knownFiles.contains(file.getName())) {
                if (!file.isDirectory()) {
                    return file.getName();
                }
            } //Returns the first 'no match'
            if (file.isDirectory()) {
                if (getFirstUnusedFiles(file.getPath()) != null) {
                    return file.getName() + "/" + getFirstUnusedFiles(file.getPath());
                }
            }
        }
        return null; //No file wasn't matching a known file

    }


    public boolean hasUnusedFiles(@Nullable String atPath) {
        List<String> knownFiles = getAllKnownFiles();

        String path = LocutusApplication.getBasePath(ctx);
        if (atPath != null) {
            path = atPath;
        }

        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (hasUnusedFiles(file.getPath())) {
                    return true;
                }
            } else {
                if (!knownFiles.contains(file.getName())) {
                    if (isAudio(file.getName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private List<String> getAllKnownFiles() {

        List<String> knownFiles = new ArrayList<>();
        for (LocutusConcept concept : getAllLocutusConcepts()) {

            if (concept.getRawFilenames() != null) {
                for (String filename : concept.getRawFilenames()) {
                    knownFiles.add((filename.contains("/") ? filenameFromPath(filename) : filename));
                }
            }
            if (concept.getRawSpeechFilenames() != null) {
                for (String filename : concept.getRawSpeechFilenames()) {
                    knownFiles.add((filename.contains("/") ? filenameFromPath(filename) : filename));
                }
            }

        }
        return knownFiles;

    }


}
