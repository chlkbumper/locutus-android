package me.cendre.locutusandroid.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import me.cendre.locutusandroid.R;

public class LocutusDialog extends Dialog {


    public LocutusDialog(Context context) {
        super(context);
    }


    public static void showAreYouSureDialog(Context ctx, String message, final BooleanListener listener) {


        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setTitle(ctx.getString(R.string.are_you_sure));
        builder.setMessage(message);
        builder.setPositiveButton(ctx.getString(R.string.yes), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.didChoose(true);
            }
        });
        builder.setNegativeButton(ctx.getString(R.string.no), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.didChoose(false);
            }
        });


        builder.show();


    }


    static void showPracticeNewComponentDialog(Context ctx, NewPracticeComponentDialog.AddComponentListener listener) {

        NewPracticeComponentDialog dialog = new NewPracticeComponentDialog(ctx);
        dialog.addAddComponentListener(listener);
        dialog.showDialog();
        //Should show dialog with an autocomplete concept name field
        //And an horizontal radio group with the title "What happens on concept click ?" -> Subtree, action, or nothing (just for display)


    }


    static void showConceptSelectionDialog(Context ctx, ConceptDialog.ConceptSelectedListener listener) {

        ConceptDialog dialog = new ConceptDialog(ctx);
        dialog.addConceptListener(listener);
        dialog.showDialog();

    }


    static void showNewUserDialog(Context ctx, NewUserDialog.NewUserListener listener) {

        NewUserDialog dialog = new NewUserDialog(ctx);
        dialog.addNewUserListener(listener);
        dialog.showDialog();

    }

    public static void showAlert(Context ctx, String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton("Ok", null);


        builder.show();

    }


}
