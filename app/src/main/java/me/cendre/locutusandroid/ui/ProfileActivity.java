package me.cendre.locutusandroid.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.LocutusPreferencesAdapter;
import me.cendre.locutusandroid.data.LocutusPreferencesManager;
import me.cendre.locutusandroid.data.LocutusProfile;
import me.cendre.locutusandroid.data.ProfileManager;

/**
 * me.cendre.locutusandroid
 * Created by guillaume on 17/06/2016.
 */
public class ProfileActivity extends LocutusActivity {

    private ProfileManager profileManager;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        profileManager = new ProfileManager(this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                profileId = null;
            } else {
                profileId = extras.getString("profileId");
            }
        } else {
            profileId = (String) savedInstanceState.getSerializable("profileId");
        }

        if (profileId == null || profileId.isEmpty()) {

            Log.wtf("Locutus", "Wtf ! profileId is null in ProfileActivity");
            onBackPressed();

        } else {

            LocutusProfile userProfile = profileManager.getLocutusProfile(profileId);

            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(userProfile.getFirstName() + " " + userProfile.getLastName());


        }

        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        final Context intentContext = this;


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View modificationsDialogView = inflater.inflate(R.layout.dialog_profile_modifications, null);
        ListView modificationsList = (ListView) modificationsDialogView.findViewById(R.id.dialog_profile_modifications_listview);
        final LocutusPreferencesAdapter preferencesAdapter = LocutusPreferencesManager.preferencesAdapterForUser(profileId, this);
        preferencesAdapter.setOnProfileRemoveListener(new OnProfileRemoveListener() {
            @Override
            public void shouldRemoveUser(String id) {
                ProfileManager.defaultManager.deleteLocutusProfile(id);
            }
        });
        modificationsList.setAdapter(preferencesAdapter);

        builder.setView(modificationsDialogView)
                // Add action buttons
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ProfileManager.defaultManager.updateLocutusProfile(preferencesAdapter.tempProfile);
                        // save user preferences ...
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //this.getDialog().cancel();
                    }
                });

        Dialog dialog = builder.create();
        dialog.show();

        return super.onOptionsItemSelected(item);

    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return new ModuleCoursFragment(profileId);
            } else {
                return new ModulePratiqueFragment(profileId);

            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MODULE COURS";
                case 1:
                    return "MODULE PRATIQUE";
            }
            return null;
        }
    }

}
