package me.cendre.locutusandroid.data;

/**
 * me.cendre.locutusandroid.data
 * Created by guillaume on 08/09/2016.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.ui.BooleanListener;
import me.cendre.locutusandroid.ui.ConceptEditDialogDismissListener;
import me.cendre.locutusandroid.ui.ConceptPicker;
import me.cendre.locutusandroid.ui.EditSpeechDialog;
import me.cendre.locutusandroid.ui.LocutusDialog;
import me.cendre.locutusandroid.ui.OnResourcePickedListener;
import me.cendre.locutusandroid.ui.OnSpeechEditListener;

public class ConceptsAdapter extends BaseAdapter implements Filterable {

    private List<LocutusConcept> originalConceptsList;
    private List<LocutusConcept> displayedConceptsList;
    private LayoutInflater inflater;
    private Context context;
    //private ConceptManager conceptManager;
    private List<LocutusConcept> userConcepts;

    private Dialog currentConceptEditDialog;
    private ConceptEditDialogDismissListener conceptEditDismissedListener;

    private boolean shouldShowCheckbox = false, userInitiatedChecking = false;

    private String profileId;


    public ConceptsAdapter(Context ctx, @Nullable String profileId) {
        inflater = LayoutInflater.from(ctx);

        this.context = ctx;
        reloadData();

        if (profileId != null) {
            this.profileId = profileId;
            shouldShowCheckbox = true; //True if profileId is selected (ie: not in concept management activity, but in fragment)

            reloadData();

        }


    }

    public void reloadData() {

        originalConceptsList = ConceptManager.defaultManager.getAllLocutusConcepts();
        displayedConceptsList = originalConceptsList;

        if (shouldShowCheckbox) {// Means we're in ModuleCoursFragment, inits two lists : userConcepts and displayedConceptList

            userConcepts = ProfileManager.defaultManager.getLearningConceptsForUser(profileId);

            ArrayList<LocutusConcept> unusedConceptsList = new ArrayList<>();
            ArrayList<String> alreadyAddedUnusedConceptsList = new ArrayList<>();
            ArrayList<String> alreadyAddedUsedConceptsList = new ArrayList<>();

            for (LocutusConcept concept : userConcepts) {

                alreadyAddedUsedConceptsList.add(concept.getName());

            }

            for (LocutusConcept concept : displayedConceptsList) {

                if (!alreadyAddedUnusedConceptsList.contains(concept.getName())) {

                    if (!alreadyAddedUsedConceptsList.contains(concept.getName())) {

                        unusedConceptsList.add(concept);
                        alreadyAddedUnusedConceptsList.add(concept.getName());

                    }

                }

            }

            displayedConceptsList = unusedConceptsList;

        }

    }


    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                displayedConceptsList = (ArrayList<LocutusConcept>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<LocutusConcept> FilteredArrList = new ArrayList<>();

                if (originalConceptsList == null) {
                    originalConceptsList = new ArrayList<>(displayedConceptsList); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = originalConceptsList.size();
                    results.values = originalConceptsList;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < originalConceptsList.size(); i++) {
                        String data = originalConceptsList.get(i).name;
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new LocutusConcept(originalConceptsList.get(i).name));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
    }


    @Override
    public int getCount() {

        if (displayedConceptsList != null) {

            if (shouldShowCheckbox) return userConcepts.size() + displayedConceptsList.size();
            return displayedConceptsList.size();

        }
        return 1;

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


        if (shouldShowCheckbox) { // ConceptAdapter for ModuleCoursFragment, to select learning concepts

            convertView = inflater.inflate(R.layout.item_check_list, null);

            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.item_check_list_checkbox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (userInitiatedChecking) {
                        if (checkBox.isChecked()) {

                            int i = position - userConcepts.size();
                            int j = (i < 0 ? 0 : i);
                            userConcepts.add(displayedConceptsList.get(j));
                            displayedConceptsList.remove(j);
                            notifyDataSetChanged();
                        } else {

                            int i = position;
                            int j = (i < 0 ? 0 : i);
                            displayedConceptsList.add(userConcepts.get(j));
                            userConcepts.remove(j); // if i > 0 return i else return 0

                            notifyDataSetChanged();
                        }
                        ProfileManager.defaultManager.setLearningConceptsForUser(userConcepts, profileId);
                    }
                }
            });

            if (userConcepts.size() > position) {
                checkBox.setText(userConcepts.get(position).getName());
                userInitiatedChecking = false;
                checkBox.setChecked(true);
                userInitiatedChecking = true;
            } else {
                if (displayedConceptsList.get(position - userConcepts.size()) != null) {
                    checkBox.setText(displayedConceptsList.get(position - userConcepts.size()).getName());

                    userInitiatedChecking = false;
                    checkBox.setChecked(false);
                    userInitiatedChecking = true;
                }
            }


        } else { // ConceptsAdapter for ConceptActivity, enabling Long click to edit

            convertView = inflater.inflate(R.layout.item_basic_list, null);

            final TextView nameText = (TextView) convertView.findViewById(R.id.item_basic_list_textview);
            if (displayedConceptsList.get(position) != null) {
                nameText.setText(displayedConceptsList.get(position).getName());
            }

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    LayoutInflater inflater = LayoutInflater.from(context);
                    final LocutusConcept editingConcept = displayedConceptsList.get(position);


                    final View conceptItemView = inflater.inflate(R.layout.dialog_edit_concept, null);

                    final EditText nameField = (EditText) conceptItemView.findViewById(R.id.edit_concept_name_field);
                    nameField.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            if (i == KeyEvent.KEYCODE_ENTER) {

                                if (!nameField.getText().toString().isEmpty()) {

                                    nameField.setText(nameField.getText().toString().replace("\n", ""));

                                }

                            }
                            return false;
                        }
                    });

                    if (editingConcept.getName() != null) {
                        nameField.setText(editingConcept.getName());
                    }


                    final SparseArray<ConceptPicker> pickers = new SparseArray<>();
                    pickers.put(0, (ConceptPicker) conceptItemView.findViewById(R.id.concept_picker_picker_1));
                    pickers.put(1, (ConceptPicker) conceptItemView.findViewById(R.id.concept_picker_picker_2));
                    pickers.put(2, (ConceptPicker) conceptItemView.findViewById(R.id.concept_picker_picker_3));

                    for (int i = 0; i < 3; i++) {

                        final int actualLevel = i;

                        if (editingConcept.hasFilenameForLevel(i)) {

                            pickers.get(actualLevel).setFilename(editingConcept.getFilename(actualLevel));
                            pickers.get(actualLevel).setResource(BitmapFactory.decodeFile(LocutusApplication.getBasePath(context) + editingConcept.getFilename(actualLevel)));

                        }

                        pickers.get(actualLevel).setRemoveOnClickListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editingConcept.removeFilenameForLevel(actualLevel);
                                pickers.get(actualLevel).setResource(null);
                            }
                        });

                        pickers.get(actualLevel).setOnResourcePickedListener(new OnResourcePickedListener() {
                            @Override
                            public void onResourcePicked(boolean hasPicked, @Nullable String path) {

                                if (hasPicked && path != null) {

                                    String basePath = LocutusApplication.getBasePath(context);
                                    String subPath = path.replace(basePath, "");

                                    pickers.get(actualLevel).setResource(BitmapFactory.decodeFile(basePath + subPath));
                                    editingConcept.setFilename(subPath, actualLevel);
                                    ConceptManager.defaultManager.updateLocutusConcept(editingConcept);

                                }

                            }
                        });

                    }


                    Button editSpeechButton = (Button) conceptItemView.findViewById(R.id.edit_concept_voices_button);
                    editSpeechButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            EditSpeechDialog dialog = new EditSpeechDialog(context, editingConcept);
                            dialog.addOnSpeechEditListener(new OnSpeechEditListener() {

                                @Override
                                public void shouldRemoveSpeechForType(String type) {
                                    editingConcept.removeSpeechReferenceForType(type);
                                    ConceptManager.defaultManager.updateLocutusConcept(editingConcept);
                                }

                                @Override
                                public void shouldSetSpeech(LocutusSpeech newSpeech) {
                                    editingConcept.setSpeech(newSpeech);
                                    ConceptManager.defaultManager.updateLocutusConcept(editingConcept);
                                }

                                @Override
                                public void needsDataSetUpdate() {
                                }
                            });
                            dialog.show();


                        }
                    });

                    Button removeConceptButton = (Button) conceptItemView.findViewById(R.id.edit_concept_remove_button);
                    removeConceptButton.setText(context.getString(R.string.remove_concept));

                    removeConceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LocutusDialog.showAreYouSureDialog(context, context.getString(R.string.remove_concept_warning), new BooleanListener() {
                                @Override
                                public void didChoose(boolean b) {
                                    if (b) {
                                        ConceptManager.defaultManager.deleteLocutusConcept(editingConcept.getName());
                                        if (currentConceptEditDialog != null) {
                                            currentConceptEditDialog.dismiss();
                                        }
                                    }
                                }
                            });
                        }
                    });


                    builder.setView(conceptItemView)
                            // Add action buttons
                            .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    if (ConceptManager.defaultManager.getLocutusConcept(editingConcept.getName()) != null) {
                                        ConceptManager.defaultManager.updateLocutusConcept(editingConcept);
                                    } else {
                                        ConceptManager.defaultManager.addLocutusConcept(editingConcept);
                                    }

                                }
                            })
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    Dialog dialog = builder.create();
                    currentConceptEditDialog = dialog;
                    currentConceptEditDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (conceptEditDismissedListener != null) {
                                conceptEditDismissedListener.didDismissConceptEditDialog();
                            }
                        }
                    });
                    dialog.show();

                    return false;
                }
            });

        }
        //}

        return convertView;
    }

    public void setConceptEditDismissedListener(ConceptEditDialogDismissListener conceptEditDismissedListener) {
        this.conceptEditDismissedListener = conceptEditDismissedListener;
    }
}