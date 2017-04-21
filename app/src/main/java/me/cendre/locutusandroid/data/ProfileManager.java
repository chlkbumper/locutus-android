package me.cendre.locutusandroid.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * me.cendre.locutusandroid
 * Created by guillaume on 15/06/2016.
 */
public class ProfileManager extends SQLiteOpenHelper {

    public static final String TABLE_LOCUTUSPROFILES = "Profiles";
    public static final String KEY_ID = "profile_id";
    public static final String KEY_CONCEPTS_COURS = "concepts_cours";
    public static final String KEY_CONCEPTS_PRATIQUE = "concepts_pratique";
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "Locutus";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_PREFERENCES = "preferences";
    private static final String KEY_IMAGE_PATH = "profile_image";
    public static ProfileManager defaultManager = null;


    public ProfileManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void initDefaultManager(Context ctx) {

        defaultManager = new ProfileManager(ctx);

    }

    public ProfileManager getDefaultManager() {

        return defaultManager;

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!LocutusApplication.tableExists(TABLE_LOCUTUSPROFILES, db)) {
            onCreate(db);
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOCUTUSPROFILES_TABLE = "CREATE TABLE " + TABLE_LOCUTUSPROFILES + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_FIRST_NAME + " TEXT,"
                + KEY_LAST_NAME + " TEXT,"
                + KEY_PREFERENCES + " INT,"
                + KEY_IMAGE_PATH + " TEXT,"
                + KEY_CONCEPTS_COURS + " TEXT,"
                + KEY_CONCEPTS_PRATIQUE + " TEXT)";
        db.execSQL(CREATE_LOCUTUSPROFILES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("LocutusDB", "Upgraded from oldVersion = " + oldVersion + " to newVersion = " + newVersion);
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCUTUSPROFILES);
            onCreate(db);
        }
    }


    public void addLocutusProfile(LocutusProfile locutusProfile) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, locutusProfile.getId());
        values.put(KEY_FIRST_NAME, locutusProfile.getFirstName()); // LocutusProfile First Name
        values.put(KEY_LAST_NAME, locutusProfile.getLastName()); // LocutusProfile Last Name
        values.put(KEY_PREFERENCES, locutusProfile.getPreferences().getJsonString());

        if (!(locutusProfile.getImagePath().isEmpty())) {
            values.put(KEY_IMAGE_PATH, locutusProfile.getImagePath());
        }// LocutusProfile Image path

        if (locutusProfile.getConceptsCours() != null) {
            values.put(KEY_CONCEPTS_COURS, LocutusConcept.conceptsListToJson(locutusProfile.getConceptsCours()));
        }
        if (locutusProfile.getConceptsPratique() != null) {
            values.put(KEY_CONCEPTS_COURS, locutusProfile.getConceptsPratique().toJSONString());
        }

        db.insert(TABLE_LOCUTUSPROFILES, null, values);
        db.close();

    }

    public LocutusProfile getLocutusProfile(String profileId) {

        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = new String[1];
        args[0] = profileId;
        String[] column = {KEY_ID, KEY_FIRST_NAME, KEY_LAST_NAME, KEY_PREFERENCES, KEY_IMAGE_PATH};
        Cursor cursor = db.query(TABLE_LOCUTUSPROFILES, column, KEY_ID + " = ?", args, null, null, null);


        if (cursor.moveToNext()) {

            LocutusProfile locutusProfile = new LocutusProfile(cursor.getString(0), //Id
                    cursor.getString(1), //First name
                    cursor.getString(2), //Last name
                    (cursor.getString(4) != null ? cursor.getString(4) : ""));

            LocutusUserPreferences preferences = LocutusUserPreferences.preferencesFromJsonString(cursor.getString(3));
            locutusProfile.setPreferences(preferences);


            cursor.close();

            return locutusProfile;


        } else {
            Log.wtf("Locutus", "Request returned null cursor");
        }

        return null;

    }

    public List<LocutusProfile> getAllLocutusProfiles() {

        List<LocutusProfile> locutusProfileList = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_ID + "," + KEY_FIRST_NAME + "," + KEY_LAST_NAME + "," + KEY_PREFERENCES + " FROM " + TABLE_LOCUTUSPROFILES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                LocutusProfile locutusProfile = new LocutusProfile();
                locutusProfile.setId(cursor.getString(0));
                locutusProfile.setFirstName(cursor.getString(1));
                locutusProfile.setLastName(cursor.getString(2));

                locutusProfileList.add(locutusProfile);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return locutusProfileList;
    }

    public int updateLocutusProfile(LocutusProfile locutusProfile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, locutusProfile.getId());
        values.put(KEY_FIRST_NAME, locutusProfile.getFirstName());
        values.put(KEY_LAST_NAME, locutusProfile.getLastName());
        values.put(KEY_PREFERENCES, locutusProfile.getPreferences().getJsonString());
        values.put(KEY_IMAGE_PATH, locutusProfile.getImagePath());
        values.put(KEY_CONCEPTS_COURS, LocutusConcept.conceptsListToJson(locutusProfile.getConceptsCours()));
        values.put(KEY_CONCEPTS_PRATIQUE, locutusProfile.conceptsPratique.toJSONString() /*locutusProfile.getConceptsPratique().toJSONString()*/);

        return db.update(TABLE_LOCUTUSPROFILES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(locutusProfile.getId())});
    }

    public void deleteLocutusProfile(String profileId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCUTUSPROFILES, KEY_ID + " = ?",
                new String[]{profileId});
        db.close();
    }


    public List<LocutusConcept> getLearningConceptsForUser(String profileId) {


        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = new String[1];
        args[0] = profileId;
        String[] column = {KEY_CONCEPTS_COURS};
        Cursor cursor = db.query(TABLE_LOCUTUSPROFILES, column, KEY_ID + " = ?", args, null, null, null);

        if (cursor.moveToNext()) {

            String conceptsJson = cursor.getString(0);
            if (conceptsJson.equals("")) {
                conceptsJson = "[]";
            }
            cursor.close();
            return LocutusConcept.conceptListFromJson(conceptsJson);

        } else {
            //Wtf empty request
            Log.wtf("Locutus", "Empty request for learning concepts");
            return new ArrayList<>();
        }

    }

    public void setLearningConceptsForUser(List<LocutusConcept> concepts, String profileId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CONCEPTS_COURS, LocutusConcept.conceptsListToJson(concepts));

        db.update(TABLE_LOCUTUSPROFILES, values, KEY_ID + " = ?",
                new String[]{profileId});

    }


    public LocutusConceptTree getPracticeConceptTreeForUser(String profileId) {

        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = new String[1];
        args[0] = profileId;
        String[] column = {KEY_CONCEPTS_PRATIQUE};
        Cursor cursor = db.query(TABLE_LOCUTUSPROFILES, column, KEY_ID + " = ?", args, null, null, null);


        if (cursor.moveToNext()) {

            try {
                List<LocutusConceptTree.LocutusConceptTreeComponent> components = new ArrayList<>();
                if (cursor.getString(0) != null) {
                    String rootsJsonString = cursor.getString(0);
                    if (rootsJsonString.equals("")) {
                        rootsJsonString = "[]";
                    } //Even if request is empty, it needs to comply to the JSON formatting
                    JSONArray jsonRoots = new JSONArray(rootsJsonString);

                    for (int i = 0; i < jsonRoots.length(); i++) {

                        JSONObject jsonComponent = new JSONObject(jsonRoots.getString(i));
                        if (jsonComponent.has("conceptName")) {
                            LocutusConcept concept = ConceptManager.defaultManager.getLocutusConcept(jsonComponent.getString("conceptName"));
                            LocutusConceptTree.LocutusConceptTreeComponent component = new LocutusConceptTree.LocutusConceptTreeComponent(concept);

                            if (jsonComponent.has("children")) {
                                component.children = LocutusConceptTree.getComponentsFromJSON(jsonComponent.getString("children"));
                            }
                            if (jsonComponent.has("target")) {
                                component.target = jsonComponent.getString("target");
                            }
                            components.add(component);
                        }

                    }

                    return new LocutusConceptTree(components);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        Log.wtf("Locutus", "Empty request for learning concepts");
        return new LocutusConceptTree();

    }


}
