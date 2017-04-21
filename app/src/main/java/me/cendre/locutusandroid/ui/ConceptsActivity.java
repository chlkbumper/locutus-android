package me.cendre.locutusandroid.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.ConceptManager;
import me.cendre.locutusandroid.data.ConceptsAdapter;
import me.cendre.locutusandroid.data.LocutusApplication;
import me.cendre.locutusandroid.data.LocutusConcept;
import me.cendre.locutusandroid.data.LocutusSpeech;
import me.cendre.locutusandroid.data.SpeechManager;

/**
 * me.cendre.locutusandroid.ui
 * Created by guillaume on 08/09/2016.
 */
public class ConceptsActivity extends LocutusActivity {

    static Context ctx;
    ConceptsAdapter conceptsAdapter;
    String emptyFieldWarning, noMatchWarning;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concepts_list);


        emptyFieldWarning = getString(R.string.dialog_link_file_emptyfield_warning);
        noMatchWarning = getString(R.string.dialog_link_file_nomatch_warning);


        ctx = this;

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Gestion des concepts");

        }


        if (ConceptManager.defaultManager.hasUnusedFiles(null)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);


            final View conceptItemView = inflater.inflate(R.layout.dialog_unknown_files_found, null);


            builder.setView(conceptItemView)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            showLinkFileDialog(ConceptManager.defaultManager.getFirstUnusedFiles(null)/*, 0*/);

                        }
                    })
                    .setNegativeButton("Plus tard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();

                        }
                    });

            Dialog dialog = builder.create();
            dialog.show();

        }


        final ListView conceptsListView = (ListView) findViewById(R.id.concepts_list_view);

        conceptsAdapter = new ConceptsAdapter(this, null); //No profileId (null), adapter will hide checboxes
        conceptsAdapter.setConceptEditDismissedListener(new ConceptEditDialogDismissListener() {
            @Override
            public void didDismissConceptEditDialog() {

                conceptsAdapter.reloadData();
                conceptsAdapter.notifyDataSetChanged();
                conceptsListView.invalidate();

                onBackPressed();
                startActivity(getIntent());

            }
        });
        conceptsListView.setAdapter(conceptsAdapter);


    }


    private void showLinkFileDialog(final String filename) {

        if (filename != null) {

            final SparseIntArray imageTypeRadioButtons = new SparseIntArray();
            final SparseIntArray voiceTypeRadioButtons = new SparseIntArray();
            final List<String> voiceTypes = SpeechManager.defaultManager.getAvailableSpeechTypes();


            imageTypeRadioButtons.put(0, R.id.dialog_link_file_level_radio_photo);
            imageTypeRadioButtons.put(1, R.id.dialog_link_file_level_radio_image);
            imageTypeRadioButtons.put(2, R.id.dialog_link_file_level_radio_pictogram);


            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            LayoutInflater inflater = LayoutInflater.from(ctx);


            boolean isAudio = ConceptManager.defaultManager.isAudio(filename);


            final View conceptItemView = inflater.inflate(R.layout.dialog_link_file, null);

            final ImageView preview = (ImageView) conceptItemView.findViewById(R.id.dialog_link_file_imageview);
            final TextView warningTextView = (TextView) conceptItemView.findViewById(R.id.dialog_link_file_warning);
            final TextView filenameTextView = (TextView) conceptItemView.findViewById(R.id.dialog_link_file_filename_textview);
            final RadioGroup resourceTypeRadio = (RadioGroup) conceptItemView.findViewById(R.id.dialog_link_file_level_radiogroup);
            final RadioGroup voiceTypeRadio = new RadioGroup(this);
            final LinearLayout voiceTypeRadioContainer = (LinearLayout) conceptItemView.findViewById(R.id.dialog_link_file_voice_type_radiogroup_container);


            filenameTextView.setText(filename);

            boolean canAddResource = true;


            if (!isAudio) {
                preview.setImageBitmap(BitmapFactory.decodeFile(LocutusApplication.getBasePath(this) + filename));
                warningTextView.setVisibility(View.GONE);
                resourceTypeRadio.setVisibility(View.VISIBLE);
                voiceTypeRadioContainer.setVisibility(View.GONE);


            } else {
                warningTextView.setVisibility(View.VISIBLE);
                warningTextView.setText(R.string.show_link_file_warning_audio);

                preview.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_speaker));
                preview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LocutusSpeech.playFile(filename);

                    }
                });

                resourceTypeRadio.setVisibility(View.GONE);
                voiceTypeRadioContainer.setVisibility(View.VISIBLE);


                if (voiceTypes.size() > 0) {
                    final RadioButton[] rb = new RadioButton[voiceTypes.size()];
                    voiceTypeRadio.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                    for (int j = 0; j < voiceTypes.size(); j++) {
                        rb[j] = new RadioButton(this);
                        rb[j].setText(voiceTypes.get(j));
                        int id = j + 100;
                        rb[j].setId(id);
                        if (j == 0) {
                            rb[j].setChecked(true);
                        } else {
                            rb[j].setChecked(false);
                        }
                        voiceTypeRadioButtons.append(j, id);
                        voiceTypeRadio.addView(rb[j]);
                    }
                    voiceTypeRadioContainer.addView(voiceTypeRadio);

                } else {

                    canAddResource = false;
                    warningTextView.setText(getString(R.string.show_link_file_warning_audio_no_voice));

                }

            }


            final AutoCompleteTextView conceptField = (AutoCompleteTextView) conceptItemView.findViewById(R.id.dialog_link_file_edittext);

            ArrayAdapter<String> conceptsSuggestionAdapter = new ArrayAdapter<>(ctx,
                    android.R.layout.simple_dropdown_item_1line,
                    ConceptManager.defaultManager.getAllLocutusConceptsString());

            conceptField.setAdapter(conceptsSuggestionAdapter);
            conceptField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if (ConceptManager.defaultManager.getLocutusConcept(conceptField.getText().toString()) != null) {

                        conceptField.setTextColor(Color.GREEN);
                        warningTextView.setVisibility(View.GONE);


                    } else if (conceptField.getText().toString().isEmpty()) {

                        warningTextView.setText(emptyFieldWarning);
                        warningTextView.setVisibility(View.VISIBLE);

                    } else {

                        warningTextView.setText(noMatchWarning);
                        warningTextView.setVisibility(View.VISIBLE);
                        conceptField.setTextColor(Color.DKGRAY);

                    }

                }
            });


            final boolean finalIsAudio = isAudio;

            builder.setView(conceptItemView)
                    .setNegativeButton("Plus tard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();

                        }
                    }).setNeutralButton("Ignorer ce fichier", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    showLinkFileDialog(ConceptManager.defaultManager.getFirstUnusedFiles(null));

                }
            });

            if (canAddResource) {

                builder.setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if (!conceptField.getText().toString().isEmpty()) {

                            if (!finalIsAudio) {


                                int chosenLevel = imageTypeRadioButtons.indexOfValue(resourceTypeRadio.getCheckedRadioButtonId());
                                if (ConceptManager.defaultManager.getLocutusConcept(conceptField.getText().toString()) != null) {

                                    LocutusConcept existingConcept = ConceptManager.defaultManager.getLocutusConcept(conceptField.getText().toString());
                                    existingConcept.setFilename(filename, chosenLevel);
                                    ConceptManager.defaultManager.updateLocutusConcept(existingConcept);


                                } else {


                                    LocutusConcept newConcept = new LocutusConcept(conceptField.getText().toString());
                                    newConcept.setFilename(filename, chosenLevel);

                                    ConceptManager.defaultManager.addLocutusConcept(newConcept);

                                }


                                findViewById(R.id.concepts_list_view).invalidate();

                            } else {

                                int checkedButtonId = voiceTypeRadio.getCheckedRadioButtonId();
                                int checkedIndex = voiceTypeRadioButtons.indexOfValue(checkedButtonId);

                                String voiceType = voiceTypes.get(checkedIndex);
                                LocutusSpeech newSpeech = new LocutusSpeech(conceptField.getText().toString(), voiceType, filename);

                                if (ConceptManager.defaultManager.getLocutusConcept(conceptField.getText().toString()) != null) {

                                    LocutusConcept existingConcept = ConceptManager.defaultManager.getLocutusConcept(conceptField.getText().toString());
                                    existingConcept.setSpeech(newSpeech);
                                    ConceptManager.defaultManager.updateLocutusConcept(existingConcept);


                                } else {

                                    LocutusConcept newConcept = new LocutusConcept(conceptField.getText().toString());
                                    newConcept.setSpeech(newSpeech);

                                    ConceptManager.defaultManager.addLocutusConcept(newConcept);

                                }

                            }

                        }

                        showLinkFileDialog(ConceptManager.defaultManager.getFirstUnusedFiles(null));

                    }
                });

            }

            Dialog dialog = builder.create();
            dialog.show();

        } else {

            Log.e("ConceptsActvity", "Error : requesting showLinkFileDialog(String filename) with filename = null...");

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.concepts_actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.concepts_list_view_item_link:
                if (ConceptManager.defaultManager.hasUnusedFiles(null)) {

                    showLinkFileDialog(ConceptManager.defaultManager.getFirstUnusedFiles(null));

                } else {

                    LocutusDialog.showAlert(this, "Aucun fichier inconnu", "Locutus n'a trouvé aucun fichier qui ne soit pas déjà affecté à un concept.");

                }
                break;
            case R.id.concepts_list_view_item_add:
                showNewConceptDialog();
                //Log.d("Locutus", "User wants to add a concept to the list");
                break;

            case R.id.concepts_list_view_item_search:
                showSearchDialog();
                break;

            case android.R.id.home:
                launchActivity(LaunchActivity.class);
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    public void showSearchDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View dialogView = inflater.inflate(R.layout.dialog_search_concept, null);
        final EditText searchEditText = (EditText) dialogView.findViewById(R.id.search_concept_dialog_edittext);

        builder.setView(dialogView);
        builder.setPositiveButton("Chercher", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String request = searchEditText.getText().toString();
                conceptsAdapter.getFilter().filter(request);

            }
        });
        builder.setNegativeButton("Annuler", null);

        Dialog dialog = builder.create();
        dialog.show();


    }


    public void showNewConceptDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        final View dialogView = inflater.inflate(R.layout.dialog_add_concept, null);
        final EditText nameField = (EditText) dialogView.findViewById(R.id.add_concept_name_field);

        final int[] pickersIds = {R.id.concept_picker_picker_1, R.id.concept_picker_picker_2, R.id.concept_picker_picker_3};
        for (int pickerId : pickersIds) {
            final ConceptPicker picker = (ConceptPicker) dialogView.findViewById(pickerId);
            picker.setContext(ctx);
            picker.setOnResourcePickedListener(new OnResourcePickedListener() {
                @Override
                public void onResourcePicked(boolean hasPicked, String path) {

                    if (hasPicked && path != null) {

                        String basePath = LocutusApplication.getBasePath(ctx);
                        String subPath = path.replace(basePath, "");
                        picker.setResource(BitmapFactory.decodeFile(basePath + subPath));

                    }

                }
            });
        }


        builder.setView(dialogView)
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String conceptName = nameField.getText().toString();
                        LocutusConcept concept = new LocutusConcept(conceptName);


                        for (int pickerId : pickersIds) {
                            ConceptPicker picker = (ConceptPicker) dialogView.findViewById(pickerId);
                            if (picker.hasResource()) {
                                concept.setFilename(picker.getFilename(), picker.getLevel());
                            }
                        }

                        ConceptManager.defaultManager.addLocutusConcept(concept);


                    }
                })
                .setNegativeButton("Annuler", null);

        Dialog dialog = builder.create();
        dialog.show();

    }


}
