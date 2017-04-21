package me.cendre.locutusandroid.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;

import java.util.HashMap;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.ConceptManager;
import me.cendre.locutusandroid.data.LocutusConcept;
import me.cendre.locutusandroid.data.LocutusConceptTree;

/**
 * Created by guillaumecendre on 05/12/2016.
 */

enum LocutusComponentType {

    CONCEPT, TARGET, SUBTREE

}


public class NewPracticeComponentDialog {


    private final Context ctx;
    private final HashMap<Integer, LocutusComponentType> radioButtonsIds = new HashMap<>();
    private ListenerList<NewPracticeComponentDialog.AddComponentListener> addComponentListenerList = new ListenerList<>();
    private Button chooseTargetButton;
    private RadioGroup componentTypeRadioGroup;
    private String targetFilePath;

    public NewPracticeComponentDialog(Context ctx) {
        this.ctx = ctx;
    }

    public Dialog createNewPracticeComponentDialog() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setTitle(ctx.getString(R.string.add_component_dialog_title));


        View newComponentView = LayoutInflater.from(ctx).inflate(R.layout.dialog_add_component, null);
        final AutoCompleteTextView conceptNameField = (AutoCompleteTextView) newComponentView.findViewById(R.id.dialog_add_component_concept_field);

        ArrayAdapter<String> conceptsSuggestionAdapter = new ArrayAdapter<>(ctx,
                android.R.layout.simple_dropdown_item_1line,
                ConceptManager.defaultManager.getAllLocutusConceptsString());
        conceptNameField.setAdapter(conceptsSuggestionAdapter);

        /*componentTypeRadioGroup = (RadioGroup)newComponentView.findViewById(R.id.dialog_add_component_radio_group);
        radioButtonsIds.put(R.id.dialog_add_component_radio_subtree, LocutusComponentType.SUBTREE);
        radioButtonsIds.put(R.id.dialog_add_component_radio_target,  LocutusComponentType.TARGET);
        radioButtonsIds.put(R.id.dialog_add_component_radio_nothing, LocutusComponentType.CONCEPT);
        //{R.id.dialog_add_component_radio_subtree, R.id.dialog_add_component_radio_target, R.id.dialog_add_component_radio_nothing};

        chooseTargetButton = (Button)newComponentView.findViewById(R.id.dialog_add_component_choose_target_button);
        chooseTargetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File mPath = new File(LocutusApplication.getBasePath(ctx));
                FileDialog fileDialog = new FileDialog(ctx, mPath);
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        Log.d("Target selector", "selected file " + file.getPath());
                        targetFilePath = file.getPath();
                    }
                });
                fileDialog.showDialog();

            }
        });

        componentTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                updateComponentType();

            }
        });

        updateComponentType();*/


        builder.setView(newComponentView).setPositiveButton(ctx.getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (!conceptNameField.getText().toString().isEmpty()) {

                    LocutusConcept concept = ConceptManager.defaultManager.getLocutusConcept(conceptNameField.getText().toString());
                    LocutusConceptTree.LocutusConceptTreeComponent component = new LocutusConceptTree.LocutusConceptTreeComponent(concept);
                    /*LocutusComponentType componentType = radioButtonsIds.get(componentTypeRadioGroup.getCheckedRadioButtonId());
                    switch (componentType) {

                        case TARGET:
                            component.target = targetFilePath;
                            break;
                        case SUBTREE:
                            component.children = new ArrayList<>();
                            break;
                        default: break;

                    }*/
                    fireAddComponentEvent(component);

                } else {

                    //Show "no text was entered" message

                }


            }
        }).setNegativeButton("Annuler", null).setCancelable(true);

        dialog = builder.show();
        return dialog;
    }

    public void addAddComponentListener(NewPracticeComponentDialog.AddComponentListener listener) {
        addComponentListenerList.add(listener);
    }

    /*private void updateComponentType() {

        LocutusComponentType componentType = radioButtonsIds.get(componentTypeRadioGroup.getCheckedRadioButtonId());

        switch (componentType) {

            case CONCEPT:
                chooseTargetButton.setVisibility(View.GONE);
                break;
            case TARGET:
                chooseTargetButton.setVisibility(View.VISIBLE);
                break;
            case SUBTREE:
                chooseTargetButton.setVisibility(View.GONE);
                break;

        }

    }*/

    /**
     * Show file dialog
     */
    public void showDialog() {
        createNewPracticeComponentDialog().show();
    }

    /*public void removeConceptListener(ConceptSelectedListener listener) {
        fileListenerList.remove(listener);
    }*/

    private void fireAddComponentEvent(final LocutusConceptTree.LocutusConceptTreeComponent component) {
        addComponentListenerList.fireEvent(new ListenerList.FireHandler<AddComponentListener>() {
            public void fireEvent(AddComponentListener listener) {
                listener.shouldAddComponent(component);
            }
        });
    }

    public interface AddComponentListener {
        void shouldAddComponent(LocutusConceptTree.LocutusConceptTreeComponent component);
    }


}
