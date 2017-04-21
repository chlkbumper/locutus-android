package me.cendre.locutusandroid.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import me.cendre.locutusandroid.R;

/**
 * Created by guillaume on 11/06/2016.
 */
public class ResourcesActivity extends LocutusActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setShowHideAnimationEnabled(true);
            actionBar.setTitle("Gestion des resources");

        } else {

            Log.e("Locutus", "ProfilesActivity's action bar was null");

        }

        Button conceptsButton = (Button) findViewById(R.id.resource_concepts_button);
        conceptsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchActivity(ConceptsActivity.class);

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        launchActivity(LaunchActivity.class);
        return true;
    }

}
