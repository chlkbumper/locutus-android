package me.cendre.locutusandroid.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import me.cendre.locutusandroid.R;
import me.cendre.locutusandroid.data.LocutusApplication;


/**
 * Created by guillaumecendre on 01/12/2016.
 */

public class ConceptPicker extends LinearLayout {

    public int level;
    public Bitmap resource;
    public String filename;
    private Context ctx;
    private boolean hasResource = false;
    private TextView levelName;
    private TextView filenameTextView;
    private ImageView image;
    private ImageButton pickButton;
    private ImageButton removeButton;

    private OnResourcePickedListener resourcePickedListener;


    public ConceptPicker(Context context) {
        super(context);
        this.ctx = context;
        init();
    }

    public ConceptPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        applyAssets(attrs);
        init();
    }

    public ConceptPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.ctx = context;
        applyAssets(attrs);
        init();
    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
        init();
    }

    private void init() {

        inflate(getContext(), R.layout.component_concept_picker, this);

        levelName = (TextView) findViewById(R.id.concept_picker_level_name);
        setLevel(level);

        filenameTextView = (TextView) findViewById(R.id.concept_picker_filename);
        image = (ImageView) findViewById(R.id.concept_picker_image_container);
        pickButton = (ImageButton) findViewById(R.id.concept_picker_pick_button);
        pickButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getContext() != null) {
                    File mPath = new File(LocutusApplication.getBasePath(getContext()));
                    FileDialog fileDialog = new FileDialog(ctx, mPath);
                    fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                        public void fileSelected(File file) {
                            setFilename(file.getPath());
                            if (resourcePickedListener != null) {
                                resourcePickedListener.onResourcePicked(true, file.getPath());
                            } else {
                            }
                        }
                    });

                    fileDialog.showDialog();
                } else {

                    Log.e("Locutus", "ERROR : Context for ConceptPicker was null !");

                }
            }
        });

        removeButton = (ImageButton) findViewById(R.id.concept_picker_remove_button);
        removeButton.setEnabled(false);

    }

    public void setResource(Bitmap bmp) {

        this.resource = bmp;
        if (image != null) {

            image.setImageBitmap(bmp);
            removeButton.setEnabled(true);

        }
        if (bmp == null) {
            removeButton.setEnabled(false);
        }

    }

    public void setOnResourcePickedListener(OnResourcePickedListener l) {

        resourcePickedListener = l;

    }

    public void setRemoveOnClickListner(OnClickListener l) {
        if (removeButton != null) {
            removeButton.setOnClickListener(l);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int lvl) {

        this.level = lvl;
        if (levelName != null) {
            String levelNameText;
            switch (level) {
                case 0:
                    levelNameText = ctx.getString(R.string.concept_level_photo);
                    break;
                case 1:
                    levelNameText = ctx.getString(R.string.concept_level_image);
                    break;
                case 2:
                    levelNameText = ctx.getString(R.string.concept_level_picto);
                    break;
                default:
                    levelNameText = "";
                    break; //only for null safety
            }
            levelName.setText(levelNameText);
        }

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String f) {

        if (filename != null) {

            filenameTextView.setText(f);
            this.hasResource = true; //True if filename isn't null
            setResource(BitmapFactory.decodeFile(filename));

        } else {

            this.hasResource = false;

        }

    }

    public boolean hasResource() {

        return this.hasResource;

    }


    private void applyAssets(AttributeSet attrs) {

        TypedArray attributes = ctx.obtainStyledAttributes(attrs, R.styleable.ConceptPickerAttrs);
        level = attributes.getInteger(R.styleable.ConceptPickerAttrs_level, 0);

    }

}

