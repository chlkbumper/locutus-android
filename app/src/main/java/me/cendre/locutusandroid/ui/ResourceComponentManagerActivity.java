package me.cendre.locutusandroid.ui;

import android.os.Bundle;

import me.cendre.locutusandroid.R;

/**
 * me.cendre.locutusandroid
 * Created by guillaume on 25/06/2016.
 */
public class ResourceComponentManagerActivity extends LocutusActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concept_component_manager);

        final int componentToSet;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                componentToSet = -1;
            } else {
                componentToSet = extras.getInt("conceptComponentToSet");
            }
        } else {
            componentToSet = (int) savedInstanceState.getSerializable("conceptComponentToSet");
        }

        if (componentToSet >= 0) {


        } else {

            //Send back to concept component selection

        }

    }

}
