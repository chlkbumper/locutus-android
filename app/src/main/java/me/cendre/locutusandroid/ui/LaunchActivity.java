package me.cendre.locutusandroid.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.ConceptManager;
import me.cendre.locutusandroid.data.LocutusApplication;
import me.cendre.locutusandroid.data.LocutusSpeech;
import me.cendre.locutusandroid.data.ProfileManager;
import me.cendre.locutusandroid.data.SpeechManager;

public class LaunchActivity extends LocutusActivity {

    Button profilesButton, conceptsButton, settingsButton;
    TextView logoTextView;

    Typeface defaultFont;

    Context context;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //Cache
        /*CacheProvider.setCache(new Cache() {
            //Not thread safe simple cache
            private Map<String, JsonPath> map = new HashMap<String, JsonPath>();

            @Override
            public JsonPath get(String key) {
                return map.get(key);
            }

            @Override
            public void put(String key, JsonPath jsonPath) {
                map.put(key, jsonPath);
            }
        });*/

        context = this;

        LocutusSpeech.initSoundPool();

        ConceptManager.initDefaultManager(getBaseContext());
        ProfileManager.initDefaultManager(getBaseContext());
        SpeechManager.initDefaultManager(getBaseContext());


        profilesButton = (Button) findViewById(R.id.launch_profiles_button);
        conceptsButton = (Button) findViewById(R.id.launch_concepts_button);
        settingsButton = (Button) findViewById(R.id.launch_settings_button);


        defaultFont = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Text-Regular.otf");

        logoTextView = (TextView) findViewById(R.id.launch_logo_textview);
        logoTextView.setText(Html.fromHtml("<b>LOCUTUS</b> ANDROID"));
        logoTextView.setTypeface(defaultFont);
        logoTextView.setTextColor(Color.DKGRAY);
        logoTextView.setTextSize(80);


        profilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConceptManager.defaultManager.getAllLocutusConcepts().size() > 0) {
                    launchActivity(ProfilesActivity.class);
                } else {

                    LocutusDialog.showAlert(context, "Aucun concept disponible", "Vous devez ajouter des concepts à la base de donnée avant de pouvoir les afficher.");

                }
            }
        });

        conceptsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LocutusApplication.getBasePath(context).equalsIgnoreCase("/")) {
                    LocutusDialog.showAlert(context, "Aucune racine", "Il semble que vous n'ayez pas encore précisé l'emplacement de votre dossier Locutus sur le stockage. Allez dans Réglages pour le spécifier.");
                } else {
                    launchActivity(ConceptsActivity.class);
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(AppSettingsActivity.class);
            }
        });


    }

}
