package me.cendre.locutusandroid.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.AvailableSpeechAdapter;

/**
 * Created by guillaumecendre on 15/12/2016.
 */

public class AppSettingsActivity extends Activity implements DirectoryChooserFragment.OnFragmentInteractionListener {

    public static final int PICKFILE_REQUEST_CODE = 1;

    private Button rootSelectionButton;
    private TextView rootTextView;

    //private Context ctx;

    private DirectoryChooserFragment mDialog;


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_settings);

        rootTextView = (TextView) findViewById(R.id.activity_app_settings_root_textview);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (sharedPref.contains(getString(R.string.locutus_folder_root))) {

            rootTextView.setText(sharedPref.getString(getString(R.string.locutus_folder_root), "Pas de racine. (non-recommandé)"));

        }


        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("Locutus")
                .allowNewDirectoryNameModification(true)
                .allowReadOnlyDirectory(true)
                .build();
        mDialog = DirectoryChooserFragment.newInstance(config);

        rootSelectionButton = (Button) findViewById(R.id.activity_app_settings_root_button);
        rootSelectionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mDialog.show(getFragmentManager(), null);

            }
        });


        ListView voiceTypesListView = (ListView) findViewById(R.id.activity_app_settings_voicetypes_listview);
        AvailableSpeechAdapter voiceTypesAdapter = new AvailableSpeechAdapter(this);
        voiceTypesListView.setAdapter(voiceTypesAdapter);


    }

    @Override
    public void onSelectDirectory(@NonNull String path) {

        if (mDialog != null) {

            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.locutus_folder_root), path + "/");
            editor.apply();

            rootTextView.setText(path);

            mDialog.dismiss();
        }

    }

    @Override
    public void onCancelChooser() {

        if (mDialog != null) {

            mDialog.dismiss();

        }

    }

}
