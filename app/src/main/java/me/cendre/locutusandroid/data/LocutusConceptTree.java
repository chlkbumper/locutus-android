package me.cendre.locutusandroid.data;

import android.util.Log;

import com.jayway.jsonpath.internal.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * me.cendre.locutusandroid.data
 * Created by guillaume on 21/09/2016.
 */
public class LocutusConceptTree {

    public List<LocutusConceptTreeComponent> roots; //Can be a picto or a group of pictos, or a group of group of pictos, etc


    public LocutusConceptTree() {

        roots = new ArrayList<>();

    }

    public LocutusConceptTree(List<LocutusConceptTreeComponent> root) {

        this.roots = root;

    }


    public static String componentsToJSON(List<LocutusConceptTreeComponent> subtree) {

        JSONArray arr = new JSONArray();
        for (LocutusConceptTreeComponent component : subtree) {

            arr.put(component.toJSONObject());

        }

        return arr.toString();

    }


    public static ArrayList<HashMap<String, String>> componentsToPracticeArray(List<LocutusConceptTreeComponent> subtree) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for (LocutusConceptTreeComponent component : subtree) {

            HashMap<String, String> newHashMap = new HashMap<>();
            newHashMap.put("conceptName", component.concept.getName());
            list.add(newHashMap);

        }
        return list;

    }


    /**
     * @param json
     * @return List of LocutusConceptTreeComponent
     */
    public static List<LocutusConceptTreeComponent> getComponentsFromJSON(String json) throws JSONException {

        json = Utils.unescape(json);

        if (json != null) {

            JSONArray roots = new JSONArray(json);
            List<LocutusConceptTreeComponent> components = new ArrayList<>();


            for (int i = 0; i < roots.length(); i++) {


                JSONObject jsonComponent = roots.getJSONObject(i);

                LocutusConcept concept = ConceptManager.defaultManager.getLocutusConcept(jsonComponent.getString("conceptName"));
                LocutusConceptTreeComponent component = new LocutusConceptTreeComponent(concept);

                if (jsonComponent.has("target")) {
                    component.setTarget(jsonComponent.getString("target"));
                }

                if (jsonComponent.has("children")) {

                    JSONArray children = jsonComponent.getJSONArray("children");
                    for (int j = 0; j < children.length(); j++) {

                        component.addChild(new LocutusConceptTreeComponent(children.getJSONObject(j)));

                    }


                }

                components.add(component);

            }

            return components;

        }

        return new ArrayList<>();

    }

    public String toJSONString() {

        JSONArray jsonRoot = new JSONArray();
        for (LocutusConceptTreeComponent component : roots) { // 6 max

            jsonRoot.put(component.toJSONObject());

        }
        return jsonRoot.toString();

    }


    public static class LocutusConceptTreeComponent {

        public LocutusConcept concept;
        public List<LocutusConceptTreeComponent> children;
        public String target;


        public LocutusConceptTreeComponent(LocutusConcept concept) {
            this.concept = concept;
        }


        public LocutusConceptTreeComponent(JSONObject object) {

            try {

                if (object.has("conceptName")) {
                    this.concept = ConceptManager.defaultManager.getLocutusConcept(object.getString("conceptName"));
                }
                if (object.has("children")) {

                    JSONArray childrenArray = object.getJSONArray("children");
                    for (int i = 0; i < childrenArray.length(); i++) {

                        addChild(new LocutusConceptTreeComponent(childrenArray.getJSONObject(i)));

                    }

                }
                if (object.has("target")) {
                    this.target = object.getString("target");
                }


            } catch (JSONException e) {

                Log.d("Locutus", "Error in LocutusConceptTreeComponent(JSONObject object) constructor : printing stack track");

            }

        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public boolean hasChildren() {

            return children != null;

        }


        public void addChild(LocutusConceptTreeComponent component) {

            if (this.children == null) {
                this.children = new ArrayList<>();
            }
            this.children.add(component);

        }


        public String toJSONString() {

            JSONObject jsonComponent = new JSONObject();
            try {
                if (concept != null) {
                    jsonComponent.put("conceptName", concept.getName());
                }
                if (children != null) {
                    JSONArray jsonChildren = new JSONArray();
                    for (LocutusConceptTreeComponent component : children) {

                        jsonChildren.put(component.toJSONString());

                    }
                    jsonComponent.put("children", jsonChildren.toString());
                }
                if (target != null) {
                    if (!target.isEmpty())
                        jsonComponent.put("target", target);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonComponent.toString();

        }


        public JSONObject toJSONObject() {

            JSONObject jsonComponent = new JSONObject();
            try {
                if (concept != null) {
                    jsonComponent.put("conceptName", concept.getName());
                }
                if (children != null) {
                    JSONArray jsonChildren = new JSONArray();
                    for (LocutusConceptTreeComponent component : children) {

                        jsonChildren.put(component.toJSONObject());

                    }
                    jsonComponent.put("children", jsonChildren);
                }
                if (target != null) {
                    if (!target.isEmpty())
                        jsonComponent.put("target", target);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonComponent;

        }


        public boolean hasTarget() {
            if (target != null) {
                return !target.isEmpty();
            }
            return false;
        }


    }

}
