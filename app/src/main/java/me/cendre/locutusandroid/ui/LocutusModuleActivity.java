package me.cendre.locutusandroid.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import org.json.JSONException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.LocutusConcept;
import me.cendre.locutusandroid.data.LocutusConceptTree;
import me.cendre.locutusandroid.data.LocutusProfile;
import me.cendre.locutusandroid.data.LocutusSpeech;
import me.cendre.locutusandroid.data.ProfileManager;

/**
 * Created by guillaume on 03/10/2016.
 */

public class LocutusModuleActivity extends AppCompatActivity {

    final Context ctx = this;
    int focusedComponent = -1;
    List<Integer> containersIds;
    String profileId;
    LocutusProfile currentProfile;
    ProfileManager profileManager;

    LayoutInflater inflater;

    int learningLevel; //1, 2 ou 3                                              //  Default, for Learning module only
    List<LocutusConcept> learningConcepts;

    int practiceInputType; //Scroll ou clic                                     //
    boolean practiceIsEditing = false, overridesComponents = false;             //  Default, for Practice module only
    List<LocutusConceptTree.LocutusConceptTreeComponent> practiceComponents;    //
    String overridenComponentsPath;

    int screenHeight, screenWidth;

    Timer updateTimer = new Timer();

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    protected void onPause() {
        super.onPause();

        updateTimer.cancel();
        updateTimer.purge();


    }

    @Override
    protected void onResume() {
        super.onResume();

        updateTimer = new Timer();

        long period = (long) currentProfile.getPreferences().getSpeed() * 1000;
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                focusNextComponent();

            }
        }, 0, period);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        containersIds = Arrays.asList(R.id.fragment_module_container1,
                R.id.fragment_module_container2,
                R.id.fragment_module_container3,
                R.id.fragment_module_container4,
                R.id.fragment_module_container5,
                R.id.fragment_module_container6);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y - 1;

        inflater = getLayoutInflater();

        setContentView(R.layout.activity_module);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        profileManager = new ProfileManager(this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                profileId = null;
            } else {
                profileId = extras.getString("profileId");
                if (extras.containsKey("learningLevel")) {
                    learningLevel = extras.getInt("learningLevel");
                } //Get learning level, ifdef
                if (extras.containsKey("practiceInputType")) {
                    practiceInputType = extras.getInt("practiceInputType");
                } //Get learning level, ifdef
                if (extras.containsKey("practiceIsEditing")) {
                    practiceIsEditing = extras.getBoolean("practiceIsEditing");
                } //Get learning level, ifdef

                if (extras.containsKey("overrideTree")) {
                    try {
                        practiceComponents = LocutusConceptTree.getComponentsFromJSON(extras.getString("overrideTree"));
                        overridenComponentsPath = extras.getString("overridenComponentsPath");
                        overridesComponents = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            profileId = (String) savedInstanceState.getSerializable("profileId");
        }

        if (profileId == null || profileId.isEmpty()) {

            Log.wtf("Locutus", "Wtf ! profileId is null in ProfileActivity");
            onBackPressed();

        } else {

            currentProfile = profileManager.getLocutusProfile(profileId);
            setupModule();


        }


    }

    public void setupModule() {

        //TO BE OVERRIDEN by ModuleCours and ModulePratique

    }

    public int getComponentsCount() {

        return 0;

    }

    void focusNextComponent() {


        if (getComponentsCount() <= currentProfile.getPreferences().getImagesPerScene()) {
            if (focusedComponent < getComponentsCount() - 1) {

                setFocusedComponent(focusedComponent + 1);

            } else {
                setFocusedComponent(0);
            }
        } else {

            Log.d("Locutus", "Error : too many images for user !");

        }

    }

    void setFocusedComponent(final int index) {

        if (!currentProfile.getPreferences().getVoiceType().equals("")) { //Si le profil a une voix de préférence (sinon, le concept n'est pas énoncé)

            LocutusConcept currentConcept = null;
            if (learningConcepts != null) { //Si c'est le module Cours
                if (learningConcepts.size() > index) {
                    currentConcept = learningConcepts.get(index);
                }
            } else if (practiceComponents != null) { //Si c'est le module pratique
                if (practiceComponents.size() > index) {
                    LocutusConceptTree.LocutusConceptTreeComponent currentComponent = practiceComponents.get(index);
                    currentConcept = currentComponent.concept;
                }
            }

            if (currentConcept != null) {
                LocutusSpeech currentSpeech = currentConcept.getSpeech(currentProfile.getPreferences().getVoiceType());
                if (currentSpeech != null) {
                    if (!currentSpeech.getPath().equals("")) { //Si le chemin est bien un fichier
                        LocutusSpeech.playFile(currentSpeech.getPath());
                    } else {
                        Log.d("Locutus", "Error: currentSpeech.getPath().equals(\"\") = true");
                    }
                } else {

                    Log.d("Locutus", "No Speech object for concept " + currentConcept.getName() + " of type " + currentProfile.getPreferences().getVoiceType() + ", currentConcept.getJSONSpeech() = " + currentConcept.getJSONSpeech());

                }
            } else {

                Log.d("Locutus", "Current concept was not found among activity concepts");

            }

        }

        for (int i = 0; i < getComponentsCount(); i++) {

            final int j = i;
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView containerImageView = (ImageView) findViewById(containersIds.get(j));

                    if (j == index) {
                        containerImageView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_border));
                    } else {
                        containerImageView.setBackground(null);
                    }

                }
            });

        }

        focusedComponent = index;

    }

    void showSubtree(List<LocutusConceptTree.LocutusConceptTreeComponent> subtree, LocutusConceptTree.LocutusConceptTreeComponent fromComponent) {


        Intent intent = new Intent(this, ModulePratiqueActivity.class);
        intent.putExtra("practiceIsEditing", practiceIsEditing);
        intent.putExtra("profileId", profileId);

        intent.putExtra("overrideTree", LocutusConceptTree.componentsToJSON(subtree));

        String path, subpath = fromComponent.concept.getName();
        if (overridenComponentsPath != null) {
            if (!overridenComponentsPath.isEmpty()) {
                path = overridenComponentsPath + "/" + subpath;
            } else {
                path = subpath;
            }
        } else {
            path = subpath;
        }
        intent.putExtra("overridenComponentsPath", path);

        startActivity(intent);

    }

    void speakOutConcept(LocutusConcept concept) {

        if (concept.getSpeech(currentProfile.preferences.getVoiceType()) != null) {

            LocutusSpeech currentSpeech = concept.getSpeech(currentProfile.preferences.getVoiceType());
            if (currentSpeech != null) {
                if (!currentSpeech.getPath().equals("")) {
                    LocutusSpeech.playFile(currentSpeech.getPath());
                }
            }
        }

    }

    void launchTarget(String target) {

        File targetFile = new File(target);
        Uri path = Uri.fromFile(targetFile);
        Intent openIntent = new Intent(Intent.ACTION_VIEW);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String mimeType = getMimeType(target);
        openIntent.setDataAndType(path, mimeType);
        try {
            startActivity(openIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }


    }

}
