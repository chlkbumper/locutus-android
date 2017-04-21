package me.cendre.locutusandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.ConceptsAdapter;


/**
 * me.cendre.locutusandroid
 * Created by guillaume on 25/06/2016.
 */
public class ModuleCoursFragment extends Fragment {

    private String profileId;

    public ModuleCoursFragment() {

        super();

    }

    public ModuleCoursFragment(String profileId) {
        this.profileId = profileId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_module_cours, container, false);

        ConceptsAdapter conceptsAdapter = new ConceptsAdapter(getActivity(), profileId); //new ConceptsAdapter(profileId, KEY_CONCEPTS_COURS, getActivity());

        //Récupérer les concepts en cours d'apprentissage
        ListView conceptsListView = (ListView) rootView.findViewById(R.id.fragment_cours_listview);
        conceptsListView.setAdapter(conceptsAdapter);


        final SparseArray<Integer> radioButtons = new SparseArray<>();
        radioButtons.append(R.id.fragment_cours_radiobutton_level_1, 0);
        radioButtons.append(R.id.fragment_cours_radiobutton_level_2, 1);
        radioButtons.append(R.id.fragment_cours_radiobutton_level_3, 2);

        final RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.fragment_cours_radiogroup);


        Button button = (Button) rootView.findViewById(R.id.fragment_cours_button_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ModuleCoursActivity.class);
                intent.putExtra("learningLevel", radioButtons.indexOfKey(radioGroup.getCheckedRadioButtonId()));
                intent.putExtra("profileId", profileId);
                startActivity(intent);
            }
        });


        /*SparseIntArray startButtons = new SparseIntArray();
        startButtons.put(0, R.id.fragment_cours_button_level_1);
        startButtons.put(1, R.id.fragment_cours_button_level_2);
        startButtons.put(2, R.id.fragment_cours_button_level_3);

        for (int i = 0; i<startButtons.size(); i++) {

            final int index = i;
            Button button = (Button) rootView.findViewById(startButtons.get(i));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ModuleCoursActivity.class);
                    intent.putExtra("learningLevel", index);
                    intent.putExtra("profileId", profileId);
                    startActivity(intent);

                }
            });

        }*/


        return rootView;
    }

}
