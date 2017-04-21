package me.cendre.locutusandroid.data;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;

import me.cendre.locutusandroid.R;

/**
 * Created by guillaumecendre on 28/11/2016.
 */

public class DialogModuleLongClickAdapter extends BaseAdapter {

    Context ctx;
    private LayoutInflater inflater;
    private HashMap<Option, View.OnClickListener> options;

    public DialogModuleLongClickAdapter(HashMap<Option, View.OnClickListener> opts, Context ctx) {
        this.options = opts;
        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        if (options != null) {
            return options.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_basic_list, null);
        }

        TextView optionName = (TextView) convertView.findViewById(R.id.item_basic_list_textview);
        ImageView optionIconImageView = (ImageView) convertView.findViewById(R.id.item_basic_list_imageview);

        Set<Option> opts = options.keySet();

        Option opt = (Option) (opts.toArray())[i];
        switch (opt) {

            case EDIT_TARGET:
                optionName.setText(ctx.getString(R.string.options_edit_target));
                optionIconImageView.setImageDrawable(ContextCompat.getDrawable(ctx, android.R.drawable.ic_menu_edit));
                break;
            case ADD_TARGET:
                optionName.setText(ctx.getString(R.string.options_add_target));
                optionIconImageView.setImageDrawable(ContextCompat.getDrawable(ctx, android.R.drawable.ic_menu_add));
                break;
            case REMOVE_TARGET:
                optionName.setText(ctx.getString(R.string.options_remove_target));
                optionIconImageView.setImageDrawable(ContextCompat.getDrawable(ctx, android.R.drawable.ic_menu_delete));
                break;
            case CHANGE_CONCEPT:
                optionName.setText(ctx.getString(R.string.options_change_concept));
                optionIconImageView.setImageDrawable(ContextCompat.getDrawable(ctx, android.R.drawable.ic_menu_rotate));
                break;
            case REMOVE_COMPONENT:
                optionName.setText(ctx.getString(R.string.options_remove_component));
                optionIconImageView.setImageDrawable(ContextCompat.getDrawable(ctx, android.R.drawable.ic_menu_delete));
                break;
            case REMOVE_SUBTREE:
                optionName.setText(ctx.getString(R.string.options_remove_subtree));
                optionIconImageView.setImageDrawable(ContextCompat.getDrawable(ctx, android.R.drawable.ic_menu_delete));
                break;
            case ADD_SUBTREE:
                optionName.setText(ctx.getString(R.string.options_add_subtree));
                optionIconImageView.setImageDrawable(ContextCompat.getDrawable(ctx, android.R.drawable.ic_menu_add));
                break;

        }

        convertView.setOnClickListener(options.get(opt));


        return convertView;

    }

    // - Edit target
    // - Edit concept
    // - Remove concept from level
    public enum Option {
        EDIT_TARGET, ADD_TARGET, CHANGE_CONCEPT, REMOVE_TARGET, REMOVE_COMPONENT, REMOVE_SUBTREE, ADD_SUBTREE
    }


}
