package me.cendre.locutusandroid.data;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.ui.OnProfileRemoveListener;

import static java.lang.Math.round;

/**
 * me.cendre.locutusandroid.data
 * Created by guillaume on 16/09/2016.
 */
public class LocutusPreferencesAdapter extends BaseAdapter {

    public LocutusProfile tempProfile;
    private Context ctx;
    private LocutusUserPreferences preferences;
    private LayoutInflater inflater;
    private HashMap<Integer, LocutusPreferenceType> preferencesTypes;
    private LocutusProfile profile;
    private OnProfileRemoveListener onProfileRemoveListener = new OnProfileRemoveListener() {
        @Override
        public void shouldRemoveUser(String id) {

        }
    };

    public LocutusPreferencesAdapter(LocutusUserPreferences preferences, LocutusProfile profile, Context ctx) {

        this.ctx = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.profile = profile;
        this.tempProfile = profile;
        this.preferences = preferences;
        this.preferencesTypes = this.preferences.getPreferencesTypes();

    }

    public void setOnProfileRemoveListener(OnProfileRemoveListener onProfileRemoveListener) {
        this.onProfileRemoveListener = onProfileRemoveListener;
    }

    @Override
    public int getCount() {
        return preferencesTypes.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LocutusPreferenceType valueType = preferencesTypes.get(position);

            if (valueType != null) {
                switch (valueType) {


                    case FIRST_NAME:
                        convertView = inflater.inflate(R.layout.preferences_item_textedit, null);
                        final EditText firstNameField = (EditText) convertView.findViewById(R.id.preferences_item_textedit_widget);
                        firstNameField.setText(profile.getFirstName());
                        firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                tempProfile.setLastName(firstNameField.getText().toString());
                                return false;
                            }
                        });
                        final TextView fnTitleView = (TextView) convertView.findViewById(R.id.preferences_item_textedit_title);
                        fnTitleView.setText(R.string.first_name);
                        break;


                    case LAST_NAME:
                        convertView = inflater.inflate(R.layout.preferences_item_textedit, null);
                        final EditText lastNameField = (EditText) convertView.findViewById(R.id.preferences_item_textedit_widget);
                        lastNameField.setHint("Nom");
                        lastNameField.setText(profile.getLastName());
                        lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                tempProfile.setLastName(lastNameField.getText().toString());
                                return false;
                            }
                        });
                        final TextView lnTitleView = (TextView) convertView.findViewById(R.id.preferences_item_textedit_title);
                        lnTitleView.setText(R.string.last_name);
                        break;


                    case VOICE_TYPE:

                        convertView = inflater.inflate(R.layout.preferences_item_radiogroup, null);
                        TextView vtTitleView = (TextView) convertView.findViewById(R.id.preferences_item_radiogroup_title);
                        RadioGroup group = (RadioGroup) convertView.findViewById(R.id.preferences_item_radiogroup_widget);

                        if (SpeechManager.defaultManager.getAvailableSpeechTypes().size() > 0) {

                            vtTitleView.setText(R.string.voices);
                            int i = 1;
                            for (String speechType : SpeechManager.defaultManager.getAvailableSpeechTypes()) {

                                int buttonId = i + 433;
                                RadioButton button = new RadioButton(ctx);
                                button.setId(buttonId);
                                button.setText(speechType);
                                group.addView(button);
                                if (preferences.getVoiceType().equals(speechType))
                                    button.setChecked(true);
                                i++;

                            }
                            /*RadioButton noVoiceButton = new RadioButton(ctx);
                            noVoiceButton.setText(R.string.no_voice);
                            int noVoiceButtonId = 432;
                            noVoiceButton.setId(noVoiceButtonId);
                            group.addView(noVoiceButton);*/

                            /*if (preferences.getVoiceType().equals(""))
                            noVoiceButton.setChecked(true);*/

                        } else {

                            vtTitleView.setText("Voix (aucune disponible)");

                        }
                        break;


                    case IMAGES_PER_SCENE:
                        convertView = inflater.inflate(R.layout.preferences_item_seekbar, null);
                        final SeekBar seekbar = (SeekBar) convertView.findViewById(R.id.preferences_item_seekbar_widget);
                        seekbar.setMax(5);
                        seekbar.setProgress(profile.getPreferences().getImagesPerScene() - 1);

                        final TextView titleTextView = (TextView) convertView.findViewById(R.id.preferences_item_seekbar_title);
                        titleTextView.setText(ctx.getString(R.string.images_per_scene) + " (" + (seekbar.getProgress() + 1) + ")");

                        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                titleTextView.setText(ctx.getString(R.string.images_per_scene) + " (" + (seekbar.getProgress() + 1) + ")");
                                tempProfile.preferences.setIps(seekbar.getProgress() + 1);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                        break;


                    case SCROLL_SPEED:
                        convertView = inflater.inflate(R.layout.preferences_item_seekbar, null);
                        final SeekBar scrollSpeedSeekbar = (SeekBar) convertView.findViewById(R.id.preferences_item_seekbar_widget);
                        scrollSpeedSeekbar.setMax(200);
                        scrollSpeedSeekbar.setProgress((int) (((profile.getPreferences().getSpeed() - 2.0) / 10.0) * 200));

                        final TextView scrollSpeedTitleTextView = (TextView) convertView.findViewById(R.id.preferences_item_seekbar_title);
                        scrollSpeedTitleTextView.setText(ctx.getString(R.string.scroll_speed) + " (" + round(((scrollSpeedSeekbar.getProgress() / 200.0) * 10.0) + 2.0) + "s)");

                        scrollSpeedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                scrollSpeedTitleTextView.setText(ctx.getString(R.string.scroll_speed) + " (" + round(((scrollSpeedSeekbar.getProgress() / 200.0) * 10.0) + 2.0) + "s)");
                                tempProfile.preferences.setSpeed(round(((scrollSpeedSeekbar.getProgress() / 200.0) * 10.0) + 2.0));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                        break;
                    case REMOVE_PROFILE:

                        convertView = inflater.inflate(R.layout.preferences_item_remove, null);
                        Button removeButton = (Button) convertView.findViewById(R.id.preferences_item_remove_button);
                        removeButton.setText(ctx.getString(R.string.remove_profile));
                        removeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onProfileRemoveListener.shouldRemoveUser(profile.getId());
                            }
                        });

                        break;

                /*case PROFILE_PICTURE:
                    break;
                case FRAMES:

                    break;
                case SCROLL_METHOD:

                    break;*/

                    default:
                        Log.d("Locutus", "Inflated default cell in LocutusPreferencesAdapter (this is not supposed to happen)");
                        convertView = inflater.inflate(R.layout.item_basic_list, null);
                        break;
                }

            }

        }

        return convertView;

    }


}
