package me.cendre.locutusandroid.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.ui.FileDialog;
import me.cendre.locutusandroid.ui.OnSpeechEditListener;

/**
 * Created by guillaumecendre on 04/01/2017.
 */

public class SpeechAdapter extends BaseAdapter {


    private LocutusConcept concept;

    private List<LocutusSpeech> speechList = new ArrayList<>();
    private List<String> otherAvailableSpeechTypes = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    private ArrayList<OnSpeechEditListener> listeners = new ArrayList<>();
    private ArrayList<Integer> positionsToReload = new ArrayList<>();


    public SpeechAdapter(Context ctx, LocutusConcept concept) {

        inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.concept = concept;


        populateForConcept(concept);

    }

    private void populateForConcept(LocutusConcept c) {

        speechList = ConceptManager.defaultManager.getLocutusConcept(c.getName()).getSpeech();
        otherAvailableSpeechTypes = SpeechManager.defaultManager.getAvailableSpeechTypes();
        //Log.d("SpeechAdapter", "otherAvailableSpeechTypes.size = " + otherAvailableSpeechTypes.size());

        for (LocutusSpeech speech : speechList) {

            if (otherAvailableSpeechTypes.contains(speech.getType())) {


                otherAvailableSpeechTypes.remove(speech.getType());

            }

        }

        for (OnSpeechEditListener listener : listeners) {
            listener.needsDataSetUpdate();
        }

        notifyDataSetInvalidated();
        notifyDataSetChanged();

    }


    public void addOnSpeechEditListener(OnSpeechEditListener listener) {
        listeners.add(listener);
    }

    @Override
    public int getCount() {
        return speechList.size() + otherAvailableSpeechTypes.size();
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
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (position < speechList.size()) {

            if (convertView == null || positionsToReload.contains(position)) {

                convertView = inflater.inflate(R.layout.item_existing_speech_list, null);


                ImageView speakerView = (ImageView) convertView.findViewById(R.id.item_existing_speech_speaker);
                TextView speechTypeTextView = (TextView) convertView.findViewById(R.id.item_existing_speech_type_textview);
                final TextView speechPathTextView = (TextView) convertView.findViewById(R.id.item_existing_speech_path_textview);

                ImageButton editButton = (ImageButton) convertView.findViewById(R.id.item_existing_speech_change_button);
                ImageButton removeButton = (ImageButton) convertView.findViewById(R.id.item_existing_speech_remove_button);

                speakerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LocutusSpeech.playFile(speechList.get(position).getPath());

                    }
                });

                speechTypeTextView.setText(speechList.get(position).getType());
                speechPathTextView.setText(new File(speechList.get(position).getPath()).getName());

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (context != null) {
                            File mPath = new File(LocutusApplication.getBasePath(context));
                            FileDialog fileDialog = new FileDialog(context, mPath);
                            fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                                public void fileSelected(File file) {


                                    LocutusSpeech newSpeechObject = new LocutusSpeech(speechList.get(position).getName(), speechList.get(position).getType(), file.getPath());


                                    speechPathTextView.setText(file.getName());

                                    for (OnSpeechEditListener listener : listeners) {

                                        listener.shouldSetSpeech(newSpeechObject);

                                    }

                                    populateForConcept(concept);

                                    getView(position, null, parent);
                                    //notifyDataSetChanged();

                                }
                            });
                            fileDialog.showDialog();
                        } else {

                            Log.e("Locutus", "ERROR : Context for SpeechAdapter was null !");

                        }
                    }
                });
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (OnSpeechEditListener listener : listeners) {

                            listener.shouldRemoveSpeechForType(speechList.get(position).getType());

                        }

                        populateForConcept(concept);

                        //notifyDataSetChanged();

                    }
                });

                if (positionsToReload.contains(position))
                    positionsToReload.remove(positionsToReload.indexOf(position));

            }


        } else if (position - speechList.size() < otherAvailableSpeechTypes.size()) {


            final int availableVoiceTypeIndex = position - speechList.size();
            if (convertView == null || positionsToReload.contains(position)) {
                convertView = inflater.inflate(R.layout.item_available_speech_list, null);

                TextView speechTypeTextView = (TextView) convertView.findViewById(R.id.item_available_speech_textview);
                speechTypeTextView.setText(otherAvailableSpeechTypes.get(availableVoiceTypeIndex) + " (aucun fichier)");

                ImageButton addButton = (ImageButton) convertView.findViewById(R.id.item_available_speech_add_button);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (context != null) {
                            File mPath = new File(LocutusApplication.getBasePath(context));
                            FileDialog fileDialog = new FileDialog(context, mPath);
                            fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                                public void fileSelected(File file) {

                                    LocutusSpeech newSpeechObject = new LocutusSpeech(concept.getName(), otherAvailableSpeechTypes.get(availableVoiceTypeIndex), file.getPath());
                                    LocutusConcept newConcept = ConceptManager.defaultManager.getLocutusConcept(concept.getName());
                                    newConcept.setSpeech(newSpeechObject);

                                    for (OnSpeechEditListener listener : listeners) {
                                        listener.shouldSetSpeech(newSpeechObject);
                                    }

                                    populateForConcept(concept);

                                    getView(position, null, parent);


                                }
                            });
                            fileDialog.showDialog();
                        } else {

                            Log.e("Locutus", "ERROR : Context for SpeechAdapter was null !");

                        }
                    }
                });

                if (positionsToReload.contains(position))
                    positionsToReload.remove(positionsToReload.indexOf(position));

            }

        }


        return convertView;
    }


}
