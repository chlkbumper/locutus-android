package me.cendre.locutusandroid.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * me.cendre.locutusandroid.data
 * Created by guillaume on 21/09/2016.
 */
public class LocutusUserPreferences {


    public String voiceType;
    public int imagesPerScene;
    public double speed;
    List<String> globalSettings = Arrays.asList("twoLinesPictogramsEnabled",
            "appLaunchingEnabled",
            "targetOnPictogramsEnabled",
            "isDefaultUser",
            "voiceType");

    public static LocutusUserPreferences preferencesFromJsonString(String jsonString) {

        LocutusUserPreferences preferences = new LocutusUserPreferences();
        try {

            JSONObject prefs = new JSONObject(jsonString);
            if (prefs.has("voiceType")) {
                preferences.setVoiceType(prefs.getString("voiceType"));
            }
            if (prefs.has("speed")) {
                preferences.setSpeed(prefs.getDouble("speed"));
            }
            if (prefs.has("imagesPerScene")) {
                preferences.setIps(prefs.getInt("imagesPerScene"));
            }
            if (prefs.has("voiceType")) {
                preferences.setVoiceType(prefs.getString("voiceType"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return preferences;

    }

    public String getJsonString() {

        JSONObject preferences = new JSONObject();
        try {

            preferences.put("voiceType", getVoiceType());
            preferences.put("speed", getSpeed());
            preferences.put("imagesPerScene", getImagesPerScene());
            preferences.put("voiceType", getVoiceType());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return preferences.toString();

    }

    public HashMap<Integer, LocutusPreferenceType> getPreferencesTypes() {

        HashMap<Integer, LocutusPreferenceType> types = new HashMap<>();

        types.put(0, LocutusPreferenceType.FIRST_NAME); //First name
        types.put(1, LocutusPreferenceType.LAST_NAME);
        types.put(2, LocutusPreferenceType.IMAGES_PER_SCENE);
        types.put(3, LocutusPreferenceType.SCROLL_SPEED);
        types.put(4, LocutusPreferenceType.VOICE_TYPE);
        types.put(5, LocutusPreferenceType.REMOVE_PROFILE);


        return types;

    }

    public void setIps(int imagesPerScene) {

        if (imagesPerScene < 1 || imagesPerScene > 8) {
            this.imagesPerScene = 1;
        } else {
            this.imagesPerScene = imagesPerScene;
        }

    }

    public String getVoiceType() {
        if (voiceType != null) {
            return voiceType;
        }
        return "";
    }

    public void setVoiceType(String voiceType) {

        this.voiceType = voiceType;

    }

    public int getImagesPerScene() {
        return imagesPerScene;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {

        if (speed < 2.0 || speed > 12.0) {
            this.speed = 3.0;
        } else {
            this.speed = speed;
        }

    }


}
