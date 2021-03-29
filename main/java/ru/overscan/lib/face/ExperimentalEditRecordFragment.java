package ru.overscan.lib.face;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.overscan.lib.R;
import ru.overscan.lib.data.Record;
import ru.overscan.lib.db.DBField;
import ru.overscan.lib.db.DBTable;
import ru.overscan.lib.db.Database;

public class ExperimentalEditRecordFragment extends Fragment {
	
	public static final String TABLE_NAME = 
			ExperimentalEditRecordFragment.class.getName() + ".tableName";
	public static final String ACTION = 
			ExperimentalEditRecordFragment.class.getName() + ".action";
	public static final String RECORD_KEY = 
			ExperimentalEditRecordFragment.class.getName() + ".recordKey";
	
	Controls controls;
	DBTable table;
	String tableName;
	String action;
	String[] recordKey;
	Record originRec;
	
	OnDBChangeListener changeListener;
	
	public interface OnDBChangeListener {
	    public void onDBChanged();
	}
	
	public static ExperimentalEditRecordFragment newInstance(String tableName,
			String action, String[] recordKey) {
	    Bundle args = new Bundle();
//	    args.putSerializable(TABLE_NAME, tableName);
	    args.putString(TABLE_NAME, tableName);
	    args.putString(ACTION, action);
	    args.putStringArray(RECORD_KEY, recordKey);
	    ExperimentalEditRecordFragment fragment = new ExperimentalEditRecordFragment();
	    fragment.setArguments(args);
	    return fragment;
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	changeListener = (OnDBChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDBChangeListener");
        }
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, 
            Bundle savedInstanceState) {
//    	return InterfaceFab.makeFieldViewWithLabel(getActivity(), "El nobre: ", 
//    			"La bomba");
    	
    	Bundle args = getArguments();
    	tableName = args.getString(TABLE_NAME);
    	action = args.getString(ACTION);
    	recordKey = args.getStringArray(RECORD_KEY);
    	
    	table = Database.getInstance().getTable(tableName);
//    	Record r = null;
    	if (action.equals("edit")) originRec = table.get(recordKey);
    	else originRec = table.initRec();
    	
    	controls = new Controls(getActivity());
    	controls.setConteinerType(Controls.CONTAINER_SAVE);
    	DataControl c;
    	for (DBField f: table.getFieldsWithoutPrimaryKey()) {
    		c = new DataControl(f.caption, f.name);
    		if (action.equals("edit") || originRec != null) 
    			c.setValue(originRec.get(f.name));
    		controls.addControl(c);
    	}
    	controls.setButtonClickListener(new EditButtonClickListener(this));
    	
//    	controls.addDataControl("El nombre");
//    	Controls.DataControl c = new Controls.DataControl("El familia", "surname");
//    	c.setValue("no impotanto");
//    	controls.addControl(c);
//    	Controls.DropdownControl dc = 
//    			new Controls.DropdownControl("el sex",
//    					new String[]{"senior", "seniorita"}, "senior");
//    	controls.addControl(dc);
//    	for (int i = 0; i < 20; i++) controls.addDataControl("field" + i);
//    	layoutAndDataViews = controls.getLayoutAndDataViews();
    	
//    	this.getActivity().getSupportFragmentManager();
    	
    	return controls.getControlsView();
    	
    	
/*    	LinearLayout nameContainer;
        nameContainer = new LinearLayout(getActivity());
        nameContainer.setLayoutParams(new LayoutParams(
                                                 LayoutParams.FILL_PARENT,
                                                 LayoutParams.WRAP_CONTENT));
        nameContainer.setOrientation(LinearLayout.HORIZONTAL);
        TextView nameLbl = new TextView(getActivity());
        nameLbl.setText("Name: ");
        EditText et = new EditText(getActivity());
        et.setText("Hello Fragment!");
//        TextView nameValue = new TextView(getActivity());
//        nameValue.setText("John Doe");
        nameContainer.addView(nameLbl);
        nameContainer.addView(et);
        return nameContainer;
*/        
//        EditText v = new EditText(getActivity());
//        v.setText("Hello Fragment!");
//        return v;
    }
    
    static class EditButtonClickListener extends Controls.ButtonClickListener {
    	ExperimentalEditRecordFragment fragment;
//    	DBTable table;
//    	Controls controls;
//    	String[] recordKey;
    	
    	public EditButtonClickListener(ExperimentalEditRecordFragment f){
    		fragment = f;
//    		table = t;
//    		controls = c;
//    		this.recordKey = recordKey;
    	}
    	
    	public static class ExitWithoutSavingListener implements YesNoDialog.OnYesNoClickListener {
    		Activity activity;
    		public ExitWithoutSavingListener(Activity a) {
    			activity = a;
    		}

//			@Override
//			public void OnClick() {
//				activity.finish();
//			}

			@Override
			public void onYesNoClick(int whichDialog, int which) {
				if (which == YesNoDialog.YES)  {
					activity.finish();
				}
			}
		}

//		public static class ExitWithoutSavingListener extends YesNoDialog.ClickListener {
//			Activity activity;
//			public ExitWithoutSavingListener(Activity a) {
//				activity = a;
//			}
//
//			@Override
//			public void OnClick() {
//				activity.finish();
//			}
//		}

		@Override
		public void onClick(String tag) {
			if (tag.equals("control:save")) {
				if (fragment.controls.datasDiffer(fragment.originRec)) {
					Record r = fragment.table.getBufferRecord();
					fragment.controls.getDatas(r);
					if (fragment.action.equals("edit")) {
						fragment.table.set(fragment.recordKey, r);
						fragment.changeListener.onDBChanged();
					}
					else if (fragment.action.equals("new")) {
						fragment.table.insert(r);
						fragment.changeListener.onDBChanged();
					}
//					fragment.getActivity().setResult(Activity.RESULT_OK);
				}
				fragment.getActivity().finish();
			}
			else if (tag.equals("control:cancel")) {
				Activity a = fragment.getActivity();
//				public YesNoDialog(Context context, String question, ClickListener yesLisnerer) {
				if (fragment.controls.datasDiffer(fragment.originRec)) {
					YesNoDialog.show(fragment, 0, null, a.getResources().
							getString(R.string.exit_without_saving_question));
//							(new YesNoDialog(a, a.getResources().
//									getString(R.string.exit_without_saving_question),
//									new ExitWithoutSavingListener(a))).show();
				}
				else a.finish();
			}
			
		}
    	
    }

}
