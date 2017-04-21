package me.cendre.locutusandroid.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.DialogModuleLongClickAdapter;
import me.cendre.locutusandroid.data.LocutusApplication;
import me.cendre.locutusandroid.data.LocutusConceptTree;
import me.cendre.locutusandroid.data.ProfileManager;

/**
 * Created by guillaume on 02/11/2016.
 */

public class ModulePratiqueActivity extends LocutusModuleActivity {


    public void setComponents(final List<LocutusConceptTree.LocutusConceptTreeComponent> c) {

        this.practiceComponents = c;

        if (c.size() <= currentProfile.preferences.getImagesPerScene()) {
            int i = 0;
            for (final LocutusConceptTree.LocutusConceptTreeComponent component : c) {

                ImageView containerImageView = (ImageView) findViewById(containersIds.get(i));

                if (component.concept.getFilename(2) != null) {

                    String filePath = LocutusApplication.getBasePath(this) + component.concept.getFilename(2);

                    Bitmap bmp = BitmapFactory.decodeFile(filePath);
                    containerImageView.setImageBitmap(bmp);

                    if (practiceIsEditing) {

                        containerImageView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {

                                final Dialog dialog = new Dialog(ctx);
                                dialog.setContentView(R.layout.dialog_module_longclick);
                                dialog.setTitle(getString(R.string.module_activity_longclick_dialog_title));


                                HashMap<DialogModuleLongClickAdapter.Option, View.OnClickListener> options = new HashMap<>();

                                if (component.hasChildren()) {
                                    options.put(DialogModuleLongClickAdapter.Option.REMOVE_SUBTREE, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            LocutusDialog.showAreYouSureDialog(ctx, ctx.getString(R.string.remove_subtree_warning), new BooleanListener() {
                                                @Override
                                                public void didChoose(boolean b) {
                                                    if (b) {
                                                        if (overridesComponents) {
                                                        } else {
                                                        }
                                                        component.children = null;
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    if (!component.hasTarget()) {

                                        options.put(DialogModuleLongClickAdapter.Option.ADD_SUBTREE, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                showSubtree(new ArrayList<LocutusConceptTree.LocutusConceptTreeComponent>(), component);
                                            }
                                        });

                                    }
                                }


                                View.OnClickListener targetUpdateListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        File mPath = new File(LocutusApplication.getBasePath(ctx));
                                        FileDialog fileDialog = new FileDialog(ctx, mPath);
                                        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                                            public void fileSelected(File file) {
                                                component.setTarget(file.getPath());
                                                String path = "";
                                                if (overridesComponents) {
                                                    path = overridenComponentsPath + "/";
                                                }
                                                path = path + component.concept.getName();

                                                currentProfile.updateConceptPratiqueAtPath(path, component);

                                            }
                                        });
                                        fileDialog.showDialog();

                                        dialog.dismiss();

                                    }
                                };

                                if (component.hasTarget()) {
                                    options.put(DialogModuleLongClickAdapter.Option.EDIT_TARGET, targetUpdateListener);
                                    options.put(DialogModuleLongClickAdapter.Option.REMOVE_TARGET, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            component.setTarget(null);
                                            String path = "";
                                            if (overridesComponents) {
                                                path = overridenComponentsPath + "/";
                                            }
                                            path = path + component.concept.getName();

                                            currentProfile.updateConceptPratiqueAtPath(path, component);

                                            dialog.dismiss();

                                        }
                                    });
                                } else {
                                    if (!component.hasChildren()) {
                                        options.put(DialogModuleLongClickAdapter.Option.ADD_TARGET, targetUpdateListener);
                                    }
                                }

                                options.put(DialogModuleLongClickAdapter.Option.REMOVE_COMPONENT, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        c.remove(component);
                                        String path = "";
                                        if (overridesComponents) {
                                            path = overridenComponentsPath;
                                        }
                                        currentProfile.setConceptsPratiqueAtPath(path, c);
                                        dialog.dismiss();
                                        setComponents(c);


                                    }
                                });

                                ListView dialogListView = (ListView) dialog.findViewById(R.id.dialog_module_longclick_listview);
                                dialogListView.setAdapter(new DialogModuleLongClickAdapter(options, ctx));


                                dialog.show();

                                return false;

                            }
                        });

                    }


                } else {

                    Log.d("Locutus", "Couldn't find practice file for concept");

                    containerImageView.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.file_not_found));

                }

                i += 1;

            }

            //The add button
            if (c.size() < currentProfile.preferences.getImagesPerScene() && practiceIsEditing) {

                ImageView containerImageView = (ImageView) findViewById(containersIds.get(i));
                containerImageView.setMinimumWidth(150);

                containerImageView.setImageResource(R.drawable.add_icon);

                containerImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LocutusDialog.showPracticeNewComponentDialog(ctx, new NewPracticeComponentDialog.AddComponentListener() {
                            @Override
                            public void shouldAddComponent(LocutusConceptTree.LocutusConceptTreeComponent component) {

                                String path = "";
                                if (practiceComponents == null)
                                    practiceComponents = new ArrayList<>();
                                practiceComponents.add(component); //Adds the new component to the temp tree
                                setComponents(practiceComponents); //Refreshes UI for new component

                                if (overridesComponents) {

                                    path = overridenComponentsPath;

                                }


                                currentProfile.setConceptsPratiqueAtPath(path, practiceComponents);


                            }
                        });

                    }
                });

                i += 1;

            }

            if (i < currentProfile.getPreferences().getImagesPerScene()) {

                for (int j = i; j < currentProfile.getPreferences().getImagesPerScene(); j++) {

                    ImageView containerImageView = (ImageView) findViewById(containersIds.get(j));
                    containerImageView.setBackground(null);
                    containerImageView.setImageDrawable(null);
                    containerImageView.setOnClickListener(null);

                }

            }


        } else {

            Log.d("Locutus", "Module Pratique : too many concepts to display (>6)");
            //Error : too much concepts to display

        }


    }

    public void selectCurrentComponent() {

        if (practiceComponents.size() > focusedComponent) {
            if (practiceComponents.get(focusedComponent).hasChildren()) {

                showSubtree(practiceComponents.get(focusedComponent).children, practiceComponents.get(focusedComponent));

            } else {

                if (practiceComponents.get(focusedComponent).hasTarget()) {
                    launchTarget(practiceComponents.get(focusedComponent).target);
                }

            }

            speakOutConcept(practiceComponents.get(focusedComponent).concept);
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {


        if (event.getSource() == InputDevice.SOURCE_MOUSE) {

            selectCurrentComponent();

        }


        return super.onGenericMotionEvent(event);
    }

    @Override
    public int getComponentsCount() {
        if (practiceComponents != null) {
            return practiceComponents.size();
        }
        return 0;
    }

    @Override
    public void setupModule() {
        super.setupModule();

        LinearLayout parent = (LinearLayout) findViewById(R.id.activity_module_container);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCurrentComponent();
            }
        });

        if (!overridesComponents) {
            practiceComponents = ProfileManager.defaultManager.getPracticeConceptTreeForUser(currentProfile.getId()).roots;
        }


        if (practiceComponents == null) {
            practiceComponents = currentProfile.getConceptsPratique().roots;
        }
        setComponents(practiceComponents);


    }

}
