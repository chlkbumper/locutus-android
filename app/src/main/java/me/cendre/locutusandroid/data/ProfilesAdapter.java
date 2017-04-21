package me.cendre.locutusandroid.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.cendre.locutusandroid.R;

/**
 * me.cendre.locutusandroid
 * Created by guillaume on 15/06/2016.
 */
public class ProfilesAdapter extends BaseAdapter {

    private List<LocutusProfile> profileList;
    private LayoutInflater inflater;

    public ProfilesAdapter(Context ctx) {
        inflater = LayoutInflater.from(ctx);
        profileList = ProfileManager.defaultManager.getAllLocutusProfiles();
    }

    public void reloadProfiles() {

        profileList = new ArrayList<>();
        if (ProfileManager.defaultManager.getAllLocutusProfiles() != null) {

            profileList = ProfileManager.defaultManager.getAllLocutusProfiles();

        }


    }


    @Override
    public int getCount() {
        return profileList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_profile_list, null);
        }


        final ImageView profileImage = (ImageView) convertView.findViewById(R.id.item_profile_list_imageview);
        final TextView nameText = (TextView) convertView.findViewById(R.id.item_profile_list_textview);
        nameText.setText(profileList.get(position).getFirstName() + " " + profileList.get(position).getLastName());


        return convertView;
    }

    public List<LocutusProfile> getProfileList() {

        return profileList;

    }
}