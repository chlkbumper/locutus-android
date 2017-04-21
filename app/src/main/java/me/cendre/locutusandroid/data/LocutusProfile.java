package me.cendre.locutusandroid.data;

import android.support.annotation.Nullable;
import android.util.Log;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import org.json.JSONException;

import java.util.List;

public class LocutusProfile {

    static final Configuration configuration = Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();
    public String id, firstName, lastName, imagePath;
    public List<LocutusConcept> conceptsCours;  // Module cours : liste des concepts à apprendre
    public LocutusConceptTree conceptsPratique; // Module pratique : Arborescence pratique
    public LocutusUserPreferences preferences;


    public LocutusProfile(@Nullable String id, String firstName, String lastName, String imagePath) {

        if (id != null) {
            this.id = id;
        } else {
            this.id = "" + randomId();
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.imagePath = imagePath;

    }

    public LocutusProfile() {
    }


    public String getId() {

        return id;

    }

    public void setId(String id) {

        //should check that id doesn't already exist
        this.id = id;

    }

    public String getFirstName() {

        if (firstName == null) {
            return "";
        }
        return firstName;

    }

    public void setFirstName(String firstName) {

        if (!(firstName.isEmpty())) {
            this.firstName = firstName;
        }

    }

    public String getLastName() {

        if (lastName == null) {
            return "";
        }
        return lastName;

    }

    public void setLastName(String lastName) {

        if (!(lastName.isEmpty())) {
            this.lastName = lastName;
        }

    }

    public String getImagePath() {

        if (this.imagePath != null) {
            return imagePath;
        }
        return "";

    }

    public void setImagePath(String imagePath) {

        if (imagePath != null) {
            this.imagePath = imagePath;
        } else {
            this.imagePath = "";
        }

    }

    public LocutusConceptTree getConceptsPratique() {

        if (conceptsPratique == null) {

            return ProfileManager.defaultManager.getPracticeConceptTreeForUser(id);

        }

        return conceptsPratique;

    }

    public List<LocutusConcept> getConceptsCours() {

        if (conceptsCours != null) {

            return conceptsCours;

        } else {

            return ProfileManager.defaultManager.getLearningConceptsForUser(id);

        }

    }


    public LocutusUserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(LocutusUserPreferences preferences) {
        this.preferences = preferences;
    }

    private int randomId() {

        return (int) Math.round(Math.random() * 1000);

    }


    public void updateConceptPratiqueAtPath(String path, LocutusConceptTree.LocutusConceptTreeComponent component) {

        String jsonConceptPratiques = this.getConceptsPratique().toJSONString().replace("\\", "");
        //Log.d("Locutus", "LocutusProfile.getConceptsPratique().toJSONString() = " + jsonConceptPratiques);

        ParseContext jsonConfig = JsonPath.using(configuration);
        String jsonPath = "$.[?(@.conceptName == \"" + path.replace("/", "\")].children[?(@.conceptName == \"") + "\")]";
        //Log.d("Locutus", "JSON path for updateConceptPratiqueAtPath(" + path + ") = " + jsonPath + " with concepts JSON = " + component.toJSONString());
        DocumentContext dctx = jsonConfig.parse(jsonConceptPratiques);

        if (component.hasTarget()) {
            dctx.put(jsonPath, "target", component.getTarget());
        }
        if (component.hasChildren()) {
            dctx.put(jsonPath, "children", LocutusConceptTree.componentsToPracticeArray(component.children));
        }
        dctx.put(jsonPath, "conceptName", component.concept.getName());

        String newJsonConceptPratiques = Utils.unescape(dctx.jsonString());


        try {

            //Log.d("Locutus", "newJsonConceptPratiques = \n" + newJsonConceptPratiques + ", LocutusConceptTree.getComponentsFromJSON(newJsonConceptPratiques).size() = " + LocutusConceptTree.getComponentsFromJSON(newJsonConceptPratiques).size());
            this.conceptsPratique = new LocutusConceptTree(LocutusConceptTree.getComponentsFromJSON(newJsonConceptPratiques));

        } catch (JSONException e) {

            Log.wtf("Locutus", "JSON Exception caught in setConceptsPratiqueAtPath");
            e.printStackTrace();

        }


        ProfileManager.defaultManager.updateLocutusProfile(this);


    }

    public void setConceptsPratiqueAtPath(String path, List<LocutusConceptTree.LocutusConceptTreeComponent> components) {

        //path = "bonjour" ou "sentiments/colere"

        if (path.isEmpty()) {

            this.conceptsPratique = new LocutusConceptTree(components);

        } else { //Focused on a subtree


            String jsonConceptPratiques = this.getConceptsPratique().toJSONString().replace("\\", "");

            ParseContext jsonConfig = JsonPath.using(configuration);
            String jsonPath = "$.[?(@.conceptName == \"" + path.replace("/", "\")].children[?(@.conceptName == \"") + "\")]";
            DocumentContext dctx = jsonConfig.parse(jsonConceptPratiques);

            String newJsonConceptPratiques = Utils.unescape(dctx.put(jsonPath, "children", LocutusConceptTree.componentsToPracticeArray(components)).jsonString());


            try {

                this.conceptsPratique = new LocutusConceptTree(LocutusConceptTree.getComponentsFromJSON(newJsonConceptPratiques));

            } catch (JSONException e) {

                Log.wtf("Locutus", "JSON Exception caught in setConceptsPratiqueAtPath");
                e.printStackTrace();

            }

        }

        ProfileManager.defaultManager.updateLocutusProfile(this);


    }


}
