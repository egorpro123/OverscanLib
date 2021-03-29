package ru.overscan.lib.face;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import ru.overscan.lib.R;

public class YesNoDialogDepr implements DialogInterface.OnClickListener {
	
	ClickListener yesListener;
	ClickListener noListener;
	AlertDialog dialog;

	public YesNoDialogDepr(Context context, String question, ClickListener yesLisnerer) {
		this(context, null, question, yesLisnerer, null);
	}
	
	public YesNoDialogDepr(Context context, String title, String question,
			ClickListener yesLisnerer) {
		this(context, title, question, yesLisnerer, null);
	}
	
	public YesNoDialogDepr(Context context, String title, String question,
			ClickListener yesListener, ClickListener noListener) {
		
		this.yesListener = yesListener;
		this.noListener = noListener;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		if (title != null) builder.setTitle(title);
		builder.setMessage(question);
		builder.setPositiveButton(
				context.getResources().getString(R.string.yes_button), this); //i18n
		builder.setNegativeButton(
				context.getResources().getString(R.string.no_button), this);
		dialog = builder.create();
	}
	
	abstract public static class ClickListener {
		abstract public void OnClick();
	}
	
	public void show() {
		dialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			if (yesListener != null) yesListener.OnClick(); 
		}
		if (which == DialogInterface.BUTTON_NEGATIVE) {
			if (noListener != null) noListener.OnClick(); 
		}
	}
	
}
