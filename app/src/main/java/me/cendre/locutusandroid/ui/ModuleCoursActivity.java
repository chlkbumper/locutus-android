package me.cendre.locutusandroid.ui;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.util.List;

import me.cendre.locutusandroid.data.LocutusApplication;
import me.cendre.locutusandroid.data.LocutusConcept;

/**
 * Created by guillaume on 03/10/2016.
 */

public class ModuleCoursActivity extends LocutusModuleActivity {

    //learningLevel is set in superclass init
    int pageOffset;

    @Override
    public void setupModule() {

        Bundle extras = getIntent().getExtras();
        List<LocutusConcept> conceptList = currentProfile.getConceptsCours(); //profileManager.getLearningConceptsForUser(profileId);


        if (extras.containsKey("pageOffset")) {
            pageOffset = extras.getInt("pageOffset");
            conceptList = conceptList.subList(pageOffset * 6, pageOffset * 6 + 6);
        } else {
            if (conceptList.size() > 6) {
                conceptList = conceptList.subList(0, 6);
            }
        }

        setConcepts(conceptList);


        //ViewPager pictogramsPager = (ViewPager)findViewById(R.id.activity_module_pictograms_pager);
        //pictogramsPager.setAdapter(new PictogramsPagerAdapter(getSupportFragmentManager(), conceptList, currentProfile.getPreferences().getImagesPerScene()));

    }

    @Override
    public int getComponentsCount() {
        if (learningConcepts != null) {
            return learningConcepts.size();
        }
        return 0;
    }

    private void setConcepts(List<LocutusConcept> c) {

        this.learningConcepts = c;
        if (learningConcepts.size() <= 6) {
            int i = 0;
            for (final LocutusConcept concept : learningConcepts) {

                ImageView containerImageView = (ImageView) findViewById(containersIds.get(i));
                containerImageView.setPadding(20, 0, 20, 0);

                String filePath = LocutusApplication.getBasePath(this) + concept.getFilename(learningLevel);
                //Log.d("Locutus fragment", "Displaying bmp at filepath '" + filePath + "', for level " + learningLevel + ", concept.filenames = " + concept.getRawFilenames());

                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                containerImageView.setImageBitmap(bmp);

                i += 1;
            }
        } else {

            Log.d("Locutus", "Error: to many concepts to display");

        }

    }


}
