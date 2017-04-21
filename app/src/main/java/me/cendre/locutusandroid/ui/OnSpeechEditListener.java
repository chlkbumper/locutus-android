package me.cendre.locutusandroid.ui;

import me.cendre.locutusandroid.data.LocutusSpeech;

/**
 * Created by guillaumecendre on 21/01/2017.
 */
public interface OnSpeechEditListener {

    void shouldRemoveSpeechForType(String type);

    void shouldSetSpeech(LocutusSpeech speech);

    void needsDataSetUpdate();
}
