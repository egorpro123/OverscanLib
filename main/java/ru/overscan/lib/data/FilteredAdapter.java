package ru.overscan.lib.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import ru.overscan.lib.data.StringSelectionPattern;

public class FilteredAdapter<T> extends BaseAdapter implements Filterable {
	
	Context context;
	ArrayList<T> values, originalValues;
	LayoutInflater inflater;
	int resourceId, fieldResId;
	InnerFilter filter;
	
    private final Object lockObj = new Object();
    
    public FilteredAdapter(Context context, ArrayList<T> values, int resourceId) {
    	this(context, values, resourceId, 0);
//    	this.context = context;
//    	this.values = values;
//    	inflater = LayoutInflater.from(context);
//    	inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    } 

    public FilteredAdapter(Context context, ArrayList<T> values, int resourceId,
    		int fieldResId) {
    	this.context = context;
    	this.values = values;
    	this.resourceId = resourceId;
    	this.fieldResId = fieldResId;
    	inflater = LayoutInflater.from(context);
//    	inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    } 
    
	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public T getItem(int position) {
		//
		return values.get(position);
	}

	@Override
	public long getItemId(int position) {
		//
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//    private View createViewFromResource(int position, View convertView, ViewGroup parent,
//            int resource) {
        View view;
        TextView text;

        if (convertView == null) {
            view = inflater.inflate(resourceId, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (fieldResId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(fieldResId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        T item = getItem(position);
        if (item instanceof CharSequence) {
            text.setText((CharSequence)item);
        } else {
            text.setText(item.toString());
        }

        return view;
    }

//	@Override
//	public Filter getFilter() {
//		//
//		return null;
//	}

	@Override
	public Filter getFilter() {
	  if (filter == null) {
	      filter = new InnerFilter();
	  }
	  return filter;
	}

/**
* <p>An array filter constrains the content of the array adapter with
* a prefix. Each item that does not start with the supplied prefix
* is removed from the list.</p>
*/
	private class InnerFilter extends Filter {
		
	  @Override
	  protected FilterResults performFiltering(CharSequence prefix) {
	      FilterResults results = new FilterResults();
	
	      if (originalValues == null) {
	          synchronized (lockObj) {
	        	  originalValues = values;
	          }
	      }
	
	      if (prefix == null || prefix.length() == 0) {
	          results.values = originalValues;
	          results.count = originalValues.size();
	      } else {
//	          String prefixString = prefix.toString().toLowerCase();Щ
	          StringSelectionPattern pattern = new StringSelectionPattern(prefix.toString());
	
	          final int count = originalValues.size();
	          final ArrayList<T> vals = new ArrayList<T>();
	          T value;
	
	          for (int i = 0; i < count; i++) {
	              value = originalValues.get(i);	              
	              if (pattern.fit(value.toString())) vals.add(value);
	          }
	
	          results.values = vals;
	          results.count = vals.size();
	      }
	
	      return results;
	  }
	  
      @SuppressWarnings("unchecked")
      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
          //noinspection unchecked
          values = (ArrayList<T>) results.values;
          notifyDataSetChanged();
//          if (results.count > 0) {
//              notifyDataSetChanged();
//          } else {
//              notifyDataSetInvalidated();
//          }
      }
      
	}
	
	
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		//
//		return null;
//	}
	
	

}

