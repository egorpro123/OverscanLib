package ru.overscan.lib.face;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import ru.overscan.lib.R;
import ru.overscan.lib.data.DataUtils;

/**
 * Created by fizio on 13.04.2016.
 */
public class YesNoDialog extends DialogFragment
        implements DialogInterface.OnClickListener {

    static final String TITLE =
            YesNoDialog.class.getName().replaceAll("\\.", "_") + "_title";
    static final String QUESTION =
    YesNoDialog.class.getName().replaceAll("\\.", "_") + "_question";
    static final String WHICH =
    YesNoDialog.class.getName().replaceAll("\\.", "_") + "_which";


    public static final int YES = DialogInterface.BUTTON_POSITIVE;
    public static final int NO = DialogInterface.BUTTON_NEGATIVE;

    int whichDialog;

    public static YesNoDialog getInstance(int whichDialog, String title, String question) {
        Bundle b = new Bundle();
        if (!DataUtils.emptyString(title)) b.putString(TITLE, title);
        b.putString(QUESTION, question);
        b.putInt(WHICH, whichDialog);
        YesNoDialog f = new YesNoDialog();
        f.setArguments(b);
        return f;
    }

    public static void show(FragmentTransaction transaction, int whichDialog,
                            String title, String question) {
        YesNoDialog d = getInstance(whichDialog, title, question);
        transaction.addToBackStack(null);
        d.show(transaction, "yes_no_dialog");
    }

    public static void show(FragmentActivity act, int whichDialog,
                            String title, String question) {
        show(act.getSupportFragmentManager().beginTransaction(), whichDialog,
        title, question);
    }

    public static void show(Fragment frag, int whichDialog, String title, String question) {
//        if (frag.getParentFragment() == null)
//            show(frag.getFragmentManager().beginTransaction(),
//                    whichDialog, title, question);
//        else
          show(frag.getChildFragmentManager().beginTransaction(),
                whichDialog, title, question);
    }

    OnYesNoClickListener onYesNoClickListener;
    public interface OnYesNoClickListener {
        void onYesNoClick(int whichDialog, int whichButton);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        if (context == null) return null;
        Bundle args = getArguments();
        findClickListener();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (args.containsKey(TITLE))
            builder.setTitle(args.getString(TITLE));
        if (args.containsKey(QUESTION))
            builder.setMessage(args.getString(QUESTION));
        whichDialog = args.getInt(WHICH);

        builder.setPositiveButton(
                context.getResources().getString(R.string.yes_button), this);
        builder.setNegativeButton(
                context.getResources().getString(R.string.no_button), this);
        return builder.create();
    }

    private void findClickListener() {
        Fragment parent = getParentFragment();
        onYesNoClickListener = null;
        if (parent == null) {
            Context context = getActivity();
            if (context instanceof OnYesNoClickListener)
                onYesNoClickListener = (OnYesNoClickListener) context;
        } else {
            if (parent instanceof OnYesNoClickListener)
                onYesNoClickListener = (OnYesNoClickListener) parent;
        }

    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (onYesNoClickListener != null)
            onYesNoClickListener.onYesNoClick(whichDialog, which);
    }
}
