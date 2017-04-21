package me.cendre.locutusandroid.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.LocutusProfile;
import me.cendre.locutusandroid.data.LocutusUserPreferences;
import me.cendre.locutusandroid.data.SpeechManager;

import static java.lang.Math.round;

/**
 * Created by guillaumecendre on 08/12/2016.
 */

public class NewUserDialog {

    private final Context ctx;
    private ListenerList<NewUserListener> newUserListenerList = new ListenerList<>();

    public NewUserDialog(Context ctx) {
        this.ctx = ctx;
    }

    public Dialog createNewUserDialog() {

        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        View newUserDialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_new_profile, null);
        //Make proper modifications with listeners
        final EditText firstNameField = (EditText) newUserDialogView.findViewById(R.id.dialog_new_profile_firstname_field);
        final EditText lastNameField = (EditText) newUserDialogView.findViewById(R.id.dialog_new_profile_lastname_field);


        final TextView ipsTextView = (TextView) newUserDialogView.findViewById(R.id.new_profile_ips_label);
        final SeekBar ipsSeekbar = (SeekBar) newUserDialogView.findViewById(R.id.new_profile_ips_seekbar);
        ipsSeekbar.setMax(5);


        final TextView speedTextView = (TextView) newUserDialogView.findViewById(R.id.new_profile_speed_label);
        final SeekBar speedSeekbar = (SeekBar) newUserDialogView.findViewById(R.id.new_profile_speed_seekbar);
        speedSeekbar.setMax(200);


        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar == ipsSeekbar) {
                    ipsTextView.setText("Nombre d'images par défilement (" + (progress + 1) + ")");
                } else if (seekBar == speedSeekbar) {
                    speedTextView.setText("Vitesse de défilement (" + round((((progress / 200.0) * 12.0) + 2.0)) + "s)"); //Seekbar has max of 200, min is 2.0s, max is 12.0s, thus this algorithm
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        ipsSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        speedSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);

        final RadioGroup voiceTypeRadioGroup = (RadioGroup) newUserDialogView.findViewById(R.id.new_profile_voice_type_radiogroup);
        final RadioButton[] rb = new RadioButton[SpeechManager.defaultManager.getAvailableSpeechTypes().size()];
        final TextView voiceTypeTextView = (TextView) newUserDialogView.findViewById(R.id.new_profile_voice_type_textview);

        if (SpeechManager.defaultManager.getAvailableSpeechTypes().size() > 0) {

            voiceTypeTextView.setText("Type de voix");
            int i = 0;
            for (String voiceType : SpeechManager.defaultManager.getAvailableSpeechTypes()) {

                rb[i] = new RadioButton(ctx);
                rb[i].setText(voiceType);
                rb[i].setId(i + 1205);
                if (i == 0) {
                    rb[i].setChecked(true);
                }
                voiceTypeRadioGroup.addView(rb[i]);

                i += 1;
            }

        } else {

            voiceTypeTextView.setText("Type de voix (aucun disponible)");


        }


        builder.setView(newUserDialogView)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (!firstNameField.getText().toString().isEmpty() && !lastNameField.getText().toString().isEmpty()) {

                            LocutusProfile profile = new LocutusProfile(null, firstNameField.getText().toString(), lastNameField.getText().toString(), null);
                            LocutusUserPreferences prefs = new LocutusUserPreferences();
                            prefs.setIps(ipsSeekbar.getProgress() + 1);
                            prefs.setSpeed(round(((speedSeekbar.getProgress() / 200.0) * 12.0) + 2.0));

                            if (SpeechManager.defaultManager.getAvailableSpeechTypes().size() > 0) {

                                List<String> types = SpeechManager.defaultManager.getAvailableSpeechTypes();

                                int radioButtonID = voiceTypeRadioGroup.getCheckedRadioButtonId();
                                View radioButton = voiceTypeRadioGroup.findViewById(radioButtonID);
                                int index = voiceTypeRadioGroup.indexOfChild(radioButton);
                                String voiceType = types.get(index);

                                prefs.setVoiceType(voiceType);
                            }

                            profile.setPreferences(prefs);

                            fireNewUserEvent(profile);

                        }

                    }
                });


        dialog = builder.show();
        return dialog;
    }

    public void addNewUserListener(NewUserListener listener) {
        newUserListenerList.add(listener);
    }




    /*private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }*/

    /**
     * Show file dialog
     */
    public void showDialog() {
        createNewUserDialog().show();
    }

    /*public void removeConceptListener(ConceptSelectedListener listener) {
        fileListenerList.remove(listener);
    }*/

    private void fireNewUserEvent(final LocutusProfile profile) {
        newUserListenerList.fireEvent(new ListenerList.FireHandler<NewUserListener>() {
            public void fireEvent(NewUserListener listener) {
                listener.shouldAddNewUser(profile);
            }
        });
    }

    public interface NewUserListener {
        void shouldAddNewUser(LocutusProfile profile);
    }


}

