package me.cendre.locutusandroid.ui;

/**
 * Created by guillaumecendre on 03/12/2016.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.List;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.ConceptManager;
import me.cendre.locutusandroid.data.LocutusConcept;


public class ConceptDialog {

    private final Context ctx;
    private String[] conceptList;
    private ListenerList<ConceptSelectedListener> conceptListenerList = new ListenerList<>();

    public ConceptDialog(Context ctx) {
        this.ctx = ctx;
        loadConceptsList();
    }

    public Dialog createConceptsDialog() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setTitle(ctx.getString(R.string.concept_dialog_title));


        builder.setItems(conceptList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LocutusConcept chosenConcept = ConceptManager.defaultManager.getLocutusConcept(conceptList[which]);
                fireConceptSelectedEvent(chosenConcept);
            }
        });

        dialog = builder.show();
        return dialog;
    }

    public void addConceptListener(ConceptSelectedListener listener) {
        conceptListenerList.add(listener);
    }

    /**
     * Show file dialog
     */
    public void showDialog() {
        createConceptsDialog().show();
    }

    /*public void removeConceptListener(ConceptSelectedListener listener) {
        fileListenerList.remove(listener);
    }*/

    private void fireConceptSelectedEvent(final LocutusConcept concept) {
        conceptListenerList.fireEvent(new ListenerList.FireHandler<ConceptSelectedListener>() {
            public void fireEvent(ConceptSelectedListener listener) {
                listener.conceptSelected(concept);
            }
        });
    }

    private void loadConceptsList() {

        List<LocutusConcept> concepts = ConceptManager.defaultManager.getAllLocutusConcepts();
        String[] list = new String[concepts.size()];
        int i = 0;
        for (LocutusConcept concept : concepts) {

            list[i] = concept.getName();
            i += 1;

        }
        conceptList = list;

    }


    public interface ConceptSelectedListener {
        void conceptSelected(LocutusConcept concept);
    }


}

