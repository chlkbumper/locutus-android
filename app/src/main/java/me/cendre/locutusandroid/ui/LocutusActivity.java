package me.cendre.locutusandroid.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;


/**
 * me.cendre.locutusandroid
 * Created by guillaume on 12/06/2016.
 */

public class LocutusActivity extends AppCompatActivity {

    void launchActivity(Class activityClass) {

        Intent intent = new Intent(this, activityClass);
        startActivity(intent);

    }


}