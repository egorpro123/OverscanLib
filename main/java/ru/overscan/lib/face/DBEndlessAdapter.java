package ru.overscan.lib.face;

import java.util.Arrays;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ru.overscan.lib.R;
import ru.overscan.lib.data.CachedByPagesTable;
import ru.overscan.lib.data.Record;
import ru.overscan.lib.db.DBField;
import ru.overscan.lib.db.DBTable;

public class DBEndlessAdapter extends BaseAdapter {
	final static String TAG = "DBEndlessAdapter";
	
	final Context context;
	Controls controls;
	LayoutInflater inflater;
	int totalCount;
	boolean loadingInProgress;
	CachedByPagesTable table;
	ProgressDialog dialog;
	public DBTable dbTable;
	
//	View loadingView; 
	ListView listView;
	Record buffer;

	Runtime runtime;
	

	public DBEndlessAdapter(Context context, ListView listView, DBTable dbTable) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.listView = listView;
		this.dbTable = dbTable;
				
		runtime = Runtime.getRuntime();
		
		totalCount = 50;
		loadingInProgress = false;
		
    	controls = new Controls(context);
    	controls.makeNotEditable();
		table = new CachedByPagesTable();
		table.primaryKey = dbTable.getPrimaryKey();
    	for (DBField f: dbTable.getFields()) {
    		controls.addDataControl(f.caption, f.name);
    		table.addField(f.name);
    	}
    	buffer = table.getFieldsBuffer();
		
		listView.setAdapter(this);
		listView.setOnScrollListener(this.new EndlessOnScrollListener());
		
//		listView.setOnItemClickListener(this.new AdapterItemClick(context));
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				   int position, long id) {
				DBEndlessAdapter adapter = (DBEndlessAdapter) 
						((ListView) parent).getAdapter();
			   
				Intent i = new Intent(view.getContext(), EditRecordActitivity.class);
			    i.putExtra(ExperimentalEditRecordFragment.TABLE_NAME, 
			    		adapter.dbTable.name);
			    i.putExtra(ExperimentalEditRecordFragment.ACTION, "edit");
			    i.putExtra(ExperimentalEditRecordFragment.RECORD_KEY, 
			    		adapter.table.getRecPrimaryKey(position));
			    view.getContext().startActivity(i);
//			    context.startActivity(i);			   
		   }
		});
		loadMoreDatas(0, table.recsInPage - 1);
	}
	
//	class AdapterItemClick implements OnItemClickListener {
//		Context context;
//		public AdapterItemClick(Context context) {
//			this.context = context;
//		}
//		
//		public void onItemClick(AdapterView<?> parent, View view,
//				   int position, long id) {
////			   Log.d(TAG, "onItemClick pos " + position + ", id " + id);
//			    // When clicked, show a toast with the TextView text
////			    Toast.makeText(this.context,
////			      country.getCode(), Toast.LENGTH_SHORT).show();
//				
////				showItem((String[]) parent.getItemAtPosition(position));
//				
//				DBEndlessAdapter adapter = (DBEndlessAdapter) 
//						((ListView) parent).getAdapter();
//			   
//				Intent i = new Intent(view.getContext(), EditRecordActitivity.class);
//			    i.putExtra(ExperimentalEditRecordFragment.TABLE_NAME, 
//			    		adapter.dbTable.name);
//			    i.putExtra(ExperimentalEditRecordFragment.ACTION, "edit");
//			    i.putExtra(ExperimentalEditRecordFragment.RECORD_KEY, 
//			    		adapter.table.getRecPrimaryKey(position));
////			    view.getContext().
//			    context.startActivity(i);			   
//		   }
//	}
	
	
	public void showItem(String[] item) {
	    Toast.makeText(this.context,
			      Arrays.toString(item), Toast.LENGTH_SHORT).show();
	}

	
	public class EndlessOnScrollListener implements OnScrollListener{
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}
		 
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
		    int visibleItemCount, int totalItemCount) {
		 
		    int totalInScreen = firstVisibleItem + visibleItemCount;		   
//			Log.d(TAG, "onScroll " + firstVisibleItem + ", " +
//				     visibleItemCount + ", " + totalItemCount + ", InScreen " + totalInScreen);
			CachedByPagesTable.NotCachedPages missed = 
					table.notCachedPagesForRecs(firstVisibleItem, totalInScreen);			
		   if (missed.needed != null && !loadingInProgress){ 
//			   Log.d(TAG, "needed pages " + 
//					   Arrays.toString(missed.needed.toArray(new Integer[0])));
			   loadMoreDatas(table.getFirstRecIndexOnPage(missed.needed.getFirst()), 
					   table.getLastRecIndexOnPage(missed.needed.getLast()));
		   }
		}	
	}
	
	public String getMemoryUsage(){
		long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
		long maxHeapSizeInMB=runtime.maxMemory() / 1048576L;		
		return Long.toString(usedMemInMB) + " / "+
			Long.toString(maxHeapSizeInMB);
	}

	
	public void loadMoreDatas(int firstRec, int lastRec) {
		loadingInProgress = true;
		showLoadDialog();		
		(new LoadTask(this)).execute(firstRec, lastRec);		
	}
	
	
	private class LoadTask extends AsyncTask<Integer, Void, String> {
		DBEndlessAdapter adapter;
		
		public LoadTask(DBEndlessAdapter a){
			adapter = a;
		}
		
		protected void onPostExecute(String s) {
			hideLoadDialog();

			adapter.notifyDataSetChanged();
			adapter.totalCount +=100;
			adapter.loadingInProgress = false;
		}
				
		@Override
		protected String doInBackground(Integer... recs) {
			try {
				adapter.dbTable.getMany(adapter.table, recs[0], recs[1] - recs[0] + 1);				
//				
//				TimeUnit.SECONDS.sleep(2);
//				for(int i = recs[0]; i <= recs[1]; i++) {
//					adapter.table.put("element", "rec " + i + ", " + getMemoryUsage());
//					adapter.table.setRec(i);	
//				}
			} catch (Exception e) {
				//
				e.printStackTrace();
			}
			return null;
		}
	}
	
	@Override
	public int getCount() {
//		Log.d(TAG, "getCount() " + table.size());
		//
		return table.size();
//		return totalCount+100;
	}

	@Override
	public Object getItem(int position) {
//		Log.d(TAG, "!!!getItem() " + position);
		//
		return table.getRecAsArray(position);
//		return "position number " + position;
	}

	@Override
	public long getItemId(int position) {
//		Log.d(TAG, "getItemId() " + position);
		//
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		String s = "elem " + Integer.toString(position) +
//				" " + getMemoryUsage();
		ViewGroup host;
//		Log.d(TAG, "getView() " + position + " " + convertView);
		Record r = table.getDatas(position);
		table.put("dep_id", getMemoryUsage());
		
		if (convertView == null) {
			host = (ViewGroup) inflater.inflate(
					R.layout.experimental_lv_layout, parent, false);
			Controls.LayoutAndDataViews lav = controls.createNewLayoutAndDataViews();
			controls.setDatas(lav.views, r);
			host.setTag(lav.views);
	        host.addView(lav.layout);
		}
		else {
			host = (ViewGroup) convertView;
//			Record r = new Record();
//			table.chooseRec(position);
//			r.addField("element", table.getFieldAt("element", position));
			controls.setDatas((HashMap<String,View>) host.getTag(), r);
//			controls.setDatas((HashMap<String,View>) host.getTag(), 
//					table.getDatas(position));
		}
        return host;
//        return inflater.inflate(
//				R.layout.load_datas_view, parent, false);
	}
	
	private void showLoadDialog(){
        dialog = new ProgressDialog(context); 
        // Set progress dialog title
        
//        dialog.setTitle("ListView Load More Tutorial");
        // Set progress dialog message
        dialog.setMessage(context.getResources().getString(R.string.load_datas));
        dialog.setIndeterminate(false);
        // Show progress dialog
        dialog.show(); 		
	}
	
	private void hideLoadDialog() {
		dialog.dismiss();
		dialog = null;
	}

}
