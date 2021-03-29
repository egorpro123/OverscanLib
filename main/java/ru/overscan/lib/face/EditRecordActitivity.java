package ru.overscan.lib.face;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import ru.overscan.lib.R;

public class EditRecordActitivity extends FragmentActivity 
	implements ExperimentalEditRecordFragment.OnDBChangeListener {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ScrollView view = new ScrollView(this);
//		view.setId(R.id.edit_record_window);
//		setContentView(view);

		FrameLayout frame = new FrameLayout(this);
		frame.setId(R.id.edit_record_window);
		setContentView(frame, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		Bundle b = getIntent().getExtras();
		
//		TextView tv = new TextView(this);
//		tv.setText(b.getString(ExperimentalEditRecordFragment.TABLE_NAME));
//		view.addView(tv);
				
		FragmentManager fm = getSupportFragmentManager();
		ExperimentalEditRecordFragment frag = 
				ExperimentalEditRecordFragment.newInstance(
						b.getString(ExperimentalEditRecordFragment.TABLE_NAME),
						b.getString(ExperimentalEditRecordFragment.ACTION), 
						b.getStringArray(ExperimentalEditRecordFragment.RECORD_KEY));
		fm.beginTransaction()
			.add(R.id.edit_record_window, frag)
			.commit();
	}

	@Override
	public void onDBChanged() {
		setResult(Activity.RESULT_OK);
		//
		
	}
	
}
