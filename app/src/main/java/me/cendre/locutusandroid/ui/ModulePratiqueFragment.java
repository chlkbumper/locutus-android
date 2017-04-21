package me.cendre.locutusandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.cendre.locutusandroid.R;

/**
 * me.cendre.locutusandroid
 * Created by guillaume on 25/06/2016.
 */
public class ModulePratiqueFragment extends Fragment {

    private String profileId;


    public ModulePratiqueFragment() {

        super();

    }

    public ModulePratiqueFragment(String profileId) {

        this.profileId = profileId;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_module_pratique, container, false);


        Button editButton = (Button) rootView.findViewById(R.id.module_pratique_fragment_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ModulePratiqueActivity.class);
                intent.putExtra("practiceIsEditing", true);
                intent.putExtra("profileId", profileId);
                startActivity(intent);
            }
        });

        Button startButton = (Button) rootView.findViewById(R.id.module_pratique_fragment_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ModulePratiqueActivity.class);
                intent.putExtra("practiceIsEditing", false);
                intent.putExtra("profileId", profileId);
                startActivity(intent);
            }
        });

        /*treeContainer = (ScrollView)rootView.findViewById(R.id.module_pratique_navigation_container);
        if (profileId != null) {


        }*/

        return rootView;

    }

}
