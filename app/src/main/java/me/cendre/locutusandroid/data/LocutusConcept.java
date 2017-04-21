package me.cendre.locutusandroid.data;

import android.support.annotation.Nullable;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * me.cendre.locutusandroid.data
 * Created by guillaume on 08/09/2016.
 */
public class LocutusConcept {


    public String name;
    SparseArray<String> filenames = new SparseArray<>(); //paths for levels
    ArrayList<LocutusSpeech> speech;


    public LocutusConcept(String name) {
        this.name = name;
    }

    public LocutusConcept(String name, SparseArray<String> filenames) {
        this.name = name;
        this.filenames = filenames;
    }

    public LocutusConcept(String name, SparseArray<String> filenames, ArrayList<LocutusSpeech> speech) {
        this.name = name;
        this.filenames = filenames;
        this.speech = speech;
    }


    public LocutusConcept(JSONObject concept) {

        try {

            if (concept.has("name")) {
                setName(concept.getString("name"));
            }
            if (concept.has("filenames")) {

                JSONArray files = new JSONArray(concept.getString("filenames"));
                for (int j = 0; j < files.length(); j++) {

                    JSONObject file = files.getJSONObject(j);
                    setFilename(file.getString("filename"), file.getInt("level"));

                }

            }
            if (concept.has("speech")) {

                setJSONSpeech(new JSONArray(concept.getString("speech")));

            }


        } catch (JSONException e) {

            e.printStackTrace();

        }

    }


    static List<LocutusConcept> conceptListFromJson(String json) {

        try {

            ArrayList<LocutusConcept> conceptsList = new ArrayList<>();
            JSONArray concepts = new JSONArray(json);

            for (int i = 0; i < concepts.length(); i++) {

                LocutusConcept locutusConcept = ConceptManager.defaultManager.getLocutusConcept(concepts.getString(i));
                conceptsList.add(locutusConcept);

            }
            return conceptsList;

        } catch (JSONException e) {

            e.printStackTrace();

        }
        return new ArrayList<>();

    }

    static String conceptsListToJson(List<LocutusConcept> concepts) {

        JSONArray conceptsCoursArray = new JSONArray();
        for (LocutusConcept concept : concepts) {

            conceptsCoursArray.put(concept.getName());

        }

        return conceptsCoursArray.toString();

    }

    public String getName() {
        if (name != null) return name;
        return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename(int level) {
        return filenames.get(level);
    }

    public void setFilename(String filename, int level) {
        if (filenames == null) {
            this.filenames = new SparseArray<>();
        }
        this.filenames.put(level, filename);
    }

    void setFilenames(JSONObject json) {

        try {

            Iterator<?> keys = json.keys();

            while (keys.hasNext()) {
                Integer level = Integer.parseInt((String) keys.next()); //0 ou 2 par exemple
                String filename = json.getString("" + level);
                setFilename(filename, level);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    List<String> getRawFilenames() {

        if (filenames != null) {

            List<String> filenamesList = new ArrayList<>();
            for (int i = 0; i < filenames.size(); i++) {
                filenamesList.add(filenames.get(filenames.keyAt(i)));
            }
            return filenamesList;

        }

        return null;

    }

    List<String> getRawSpeechFilenames() {

        if (speech != null) {

            List<String> filenamesList = new ArrayList<>();
            for (LocutusSpeech s : speech) {
                filenamesList.add(s.getPath());
            }
            return filenamesList;

        }

        return null;

    }


    boolean hasFilenameForLevel(int level) {

        for (int i = 0; i < filenames.size(); i++) {
            if (filenames.keyAt(i) == level) {
                return true;
            }
        }
        return false;

    }

    String getJSONFilenames() {

        if (filenames != null) {
            try {
                JSONObject filenamesObject = new JSONObject();
                if (filenames.size() > 0) {

                    for (int i = 0; i < filenames.size(); i++) {
                        filenamesObject.put("" + filenames.keyAt(i), "" + filenames.get(filenames.keyAt(i)));
                    }
                    return filenamesObject.toString();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "{}"; // {} is the litteral empty JSONObject ( != JSONArray )

    }

    ArrayList<LocutusSpeech> getSpeech() {
        if (speech != null) {
            return speech;
        }
        return new ArrayList<>();
    }

    public void setSpeech(LocutusSpeech s) {

        if (speech == null) {
            speech = new ArrayList<>();
        }
        for (LocutusSpeech node : this.speech) {

            if (s.getType().equalsIgnoreCase(node.getType())) {

                this.speech.remove(node);

            }

        }
        this.speech.add(s);

    }

    @Nullable
    public LocutusSpeech getSpeech(String type) {

        if (speech != null) {
            for (LocutusSpeech s : speech) {
                if (s.getType().equalsIgnoreCase(type)) {
                    return s;
                }
            }
        }
        return null;

    }

    public String getJSONSpeech() {

        if (speech != null) {
            try {
                JSONArray voicesArray = new JSONArray();
                if (speech.size() > 0) {

                    for (int i = 0; i < speech.size(); i++) {
                        JSONObject speechElement = new JSONObject();
                        speechElement.put("name", speech.get(i).getName());
                        speechElement.put("filename", speech.get(i).getPath());
                        speechElement.put("type", speech.get(i).getType());
                        voicesArray.put(speechElement);
                    }
                    return voicesArray.toString();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "[]";

    }

    void setJSONSpeech(JSONArray speechArr) {

        try {
            for (int j = 0; j < speechArr.length(); j++) {

                JSONObject file = speechArr.getJSONObject(j);
                setSpeech(new LocutusSpeech(file.getString("name"), file.getString("type"), file.getString("filename")));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void removeFilenameForLevel(int level) {

        if (filenames != null) {

            filenames.remove(level);

        }

    }

    void removeSpeechReferenceForType(String type) {

        if (speech != null) {

            ArrayList<LocutusSpeech> newSpeech = new ArrayList<>();
            for (LocutusSpeech s : speech) {

                if (!s.getType().equalsIgnoreCase(type)) {
                    newSpeech.add(s);
                }

            }
            speech = newSpeech;

        }

    }


}
