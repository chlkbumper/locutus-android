package me.cendre.locutusandroid.data;

import android.content.Context;

/**
 * me.cendre.locutusandroid.data
 * Created by guillaume on 16/09/2016.
 */
public class LocutusPreferencesManager {

    public static LocutusPreferencesAdapter preferencesAdapterForUser(String profileId, Context ctx) {

        if (ProfileManager.defaultManager == null) {
            ProfileManager.initDefaultManager(ctx);
        }
        ProfileManager profileManager = ProfileManager.defaultManager;
        LocutusProfile userProfile = profileManager.getLocutusProfile(profileId);

        return new LocutusPreferencesAdapter(userProfile.preferences, userProfile, ctx);

    }

}

