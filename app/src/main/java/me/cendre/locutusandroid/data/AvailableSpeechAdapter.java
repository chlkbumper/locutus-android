package me.cendre.locutusandroid.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import me.cendre.locutusandroid.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by guillaumecendre on 17/01/2017.
 */

public class AvailableSpeechAdapter extends BaseAdapter {


    public static String availableVoiceTypesKey = "availableVoiceTypes";

    private List<String> speechList;
    private LayoutInflater inflater;
    private Context context;

    public AvailableSpeechAdapter(Context ctx) {
        inflater = LayoutInflater.from(ctx);
        this.context = ctx;

        reloadData();

    }

    private void reloadData() {

        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);

        speechList = new ArrayList<>();
        if (prefs.contains(availableVoiceTypesKey)) {

            try {
                JSONArray typesJson = new JSONArray(prefs.getString(availableVoiceTypesKey, "[]"));
                for (int i = 0; i < typesJson.length(); i++) {

                    speechList.add(typesJson.getString(i));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void removeSpeechType(String type) {

        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        String typesJson = prefs.getString(availableVoiceTypesKey, "[]");
        try {

            JSONArray newTypesList = new JSONArray();
            JSONArray types = new JSONArray(typesJson);
            for (int i = 0; i < types.length(); i++) {

                if (!types.getString(i).equalsIgnoreCase(type)) {
                    newTypesList.put(types.getString(i));
                }

            }

            SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE).edit();
            editor.putString(availableVoiceTypesKey, newTypesList.toString());
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addSpeechType(String type) {


        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        String typesJson = prefs.getString(availableVoiceTypesKey, "[]");
        try {

            JSONArray types = new JSONArray(typesJson);
            boolean canAdd = true;
            for (int i = 0; i < types.length(); i++) {

                if (types.getString(i).equalsIgnoreCase(type)) {
                    canAdd = false;
                }

            }
            if (canAdd) {
                types.put(type);
            }


            SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE).edit();
            editor.putString(availableVoiceTypesKey, types.toString());
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getCount() {
        return Math.max(speechList.size() + 1, 1);
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

        if (speechList != null) {

            if (position < speechList.size()) {

                convertView = inflater.inflate(R.layout.item_settings_available_speech_list, null);

                TextView speechTypeTextView = (TextView) convertView.findViewById(R.id.item_settings_available_speech_type_textview);
                //Log.d("Locutus", "Creating speech type cell at position " + position + ", speecType = " + speechList.get(position));
                speechTypeTextView.setText(speechList.get(position));

                Button removeButton = (Button) convertView.findViewById(R.id.item_settings_available_speech_remove_button);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        removeSpeechType(speechList.get(position));
                        reloadData();
                        notifyDataSetChanged();

                    }
                });


            } else {

                //Cellule d'ajout

                convertView = inflater.inflate(R.layout.item_settings_add_speech_list, null);

                final TextView speechTypeTextView = (TextView) convertView.findViewById(R.id.item_settings_add_speech_type_textedit);
                speechTypeTextView.setHint("Nouveau type...");

                Button addButton = (Button) convertView.findViewById(R.id.item_settings_add_speech_add_button);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        addSpeechType(speechTypeTextView.getText().toString());
                        reloadData();
                        notifyDataSetChanged();

                    }
                });


            }
        }


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_profile_list, null);
        }

        return convertView;
    }


}
