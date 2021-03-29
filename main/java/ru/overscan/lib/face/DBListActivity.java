package ru.overscan.lib.face;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import ru.overscan.lib.R;
import ru.overscan.lib.db.Database;

public class DBListActivity extends Activity {
	String tableName;
	DBEndlessAdapter adapter;
	
	public DBListActivity(String tableName) {
		this.tableName = tableName; 
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_list);
		Button addButton = (Button) findViewById(R.id.db_list_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	DBListActivity a = (DBListActivity) v.getContext();
				Intent i = new Intent(a, EditRecordActitivity.class);
			    i.putExtra(ExperimentalEditRecordFragment.TABLE_NAME, 
			    		a.tableName);
			    i.putExtra(ExperimentalEditRecordFragment.ACTION, "new");
//			    i.putExtra(ExperimentalEditRecordFragment.RECORD_KEY, 
//			    		null);
			    a.startActivity(i);
            }
        });

		adapter = new DBEndlessAdapter(this, 
				(ListView) findViewById(R.id.db_list_listview), 
				Database.getInstance().getTable(tableName));
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
        	adapter.notifyDataSetChanged();
            // Обработка результата
        }
    }
	
//	LinearLayout main = new LinearLayout(this);
//	main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
//			LayoutParams.MATCH_PARENT));
//	main.setOrientation(LinearLayout.VERTICAL);
//	
//	ListView list = new ListView(this);
//	list.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
//			LayoutParams.WRAP_CONTENT, 1.0f));
//	
//	LinearLayout bottom = new LinearLayout(this);
//	bottom.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
//			LayoutParams.WRAP_CONTENT));
//	bottom.setOrientation(LinearLayout.HORIZONTAL);
//	
//	setContentView(main);

}
