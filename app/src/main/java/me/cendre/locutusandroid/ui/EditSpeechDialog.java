package me.cendre.locutusandroid.ui;

import android.app.Dialog;
import android.content.Context;
import android.widget.ListView;

import java.util.ArrayList;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.LocutusConcept;
import me.cendre.locutusandroid.data.LocutusSpeech;
import me.cendre.locutusandroid.data.SpeechAdapter;


public class EditSpeechDialog extends Dialog {

    ArrayList<OnSpeechEditListener> listeners = new ArrayList<>();

    SpeechAdapter adapter;

    public EditSpeechDialog(Context context, LocutusConcept concept) {
        super(context);

        setContentView(R.layout.dialog_edit_speech);
        final ListView speechListView = (ListView) findViewById(R.id.edit_speech_dialog_listview);
        adapter = new SpeechAdapter(context, concept);
        adapter.addOnSpeechEditListener(new OnSpeechEditListener() {

            @Override
            public void shouldRemoveSpeechForType(String type) {
            }

            @Override
            public void shouldSetSpeech(LocutusSpeech speech) { /*Do nothing, SpeechAdapter handles that*/ }

            @Override
            public void needsDataSetUpdate() {
                //speechAdapter.notifyDataSetChanged(); //Supposed to be done already in SpeechAdapter
                //speechListView.invalidate();
                dismiss();
            }
        });
        speechListView.setAdapter(adapter);

        setTitle("Enregistrements de \"" + concept.getName() + "\"");

    }

    public EditSpeechDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected EditSpeechDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void addOnSpeechEditListener(OnSpeechEditListener listener) {

        listeners.add(listener);
        adapter.addOnSpeechEditListener(listener);

    }

}
