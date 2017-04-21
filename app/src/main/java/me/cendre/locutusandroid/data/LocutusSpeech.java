package me.cendre.locutusandroid.data;

import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by guillaume on 14/11/2016.
 */

public class LocutusSpeech {

    static SoundPool soundPool;
    String type, path, name;

    public LocutusSpeech(String name, String genre, String path) {
        this.name = name;
        this.type = genre;
        this.path = path;
    }


    public static void initSoundPool() {

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

    }

    public static void playFile(String path) {


        final int id = soundPool.load(path, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                soundPool.play(id, 1, 1, 0, 0, 1);
            }
        });


    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return (path != null ? path : ""); //path or "" if null
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
