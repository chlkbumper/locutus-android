package me.cendre.locutusandroid.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.ConceptManager;
import me.cendre.locutusandroid.data.LocutusProfile;
import me.cendre.locutusandroid.data.ProfileManager;
import me.cendre.locutusandroid.data.ProfilesAdapter;


/**
 * Created by guillaume on 11/06/2016.
 */
public class ProfilesActivity extends LocutusActivity {


    private ListView profilesListView;
    private ProfilesAdapter adapter;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setShowHideAnimationEnabled(true);
        } else {
            Log.e("Locutus", "ProfilesActivity's action bar was null");
        }


        profilesListView = (ListView) findViewById(R.id.profiles_list_view);
        ProfileManager.initDefaultManager(this);
        ConceptManager.initDefaultManager(this);


        adapter = new ProfilesAdapter(this);
        profilesListView.setAdapter(adapter);

        final Context intentContext = this;
        profilesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(intentContext, ProfileActivity.class);
                intent.putExtra("profileId", adapter.getProfileList().get(position).getId());
                startActivity(intent);
            }
        });


        profilesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Show profile action toast ?
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profiles_actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profiles_new_profile_item) {

            LocutusDialog.showNewUserDialog(this, new NewUserDialog.NewUserListener() {
                @Override
                public void shouldAddNewUser(LocutusProfile profile) {

                    ProfileManager.defaultManager.addLocutusProfile(profile);
                    adapter.reloadProfiles();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });


        } else {

            launchActivity(LaunchActivity.class); //Pressed back

        }
        return super.onOptionsItemSelected(item);
    }


}