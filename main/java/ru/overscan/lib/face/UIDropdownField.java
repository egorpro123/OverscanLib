package ru.overscan.lib.face;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by fizio on 26.04.2016.
 */
public class UIDropdownField extends UIField {
    static final String TAG = "UIDropdownField";

    Spinner spinner;
    private HashMap<String,String> values;
//    private HashMap<String,String> keys;
    ArrayList<String> showedValues;
    boolean firstSelection;

    public UIDropdownField(String name, View parent, int id,
            String[][] list, String initialValue) {
        super(name, parent.findViewById(id));
        spinner = (Spinner) editView;
        //
        values = new HashMap<>();
//        keys = new HashMap<>();
        for (int i = 0; i < list.length; i++) {
            values.put(list[i][0], list[i][1]);
//            keys.put(list[i][1], list[i][0]);
        }

        showedValues = new ArrayList<>(values.values());
        Collections.sort(showedValues);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                parent.getContext(), android.R.layout.simple_spinner_dropdown_item,
                parent.getContext(), android.R.layout.simple_spinner_item,
                showedValues.toArray(new String[0]));
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (initialValue != null) {
            setInitialValue(initialValue);
        }

        firstSelection = true;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstSelection) {
                    if (observer1 != null)
                        observer1.notify(UIDropdownField.this.name);
//                    Log.d(TAG, "getSelectedItem() " + (String) spinner.getSelectedItem());
//                    Log.d(TAG, "getItemAtPosition() "+
//                            spinner.getItemAtPosition(position).toString());
                }
                firstSelection = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

//    @Override
//    public void setView(View parent, int resourceId) {
//        spinner = (Spinner) parent.findViewById(resourceId);
//    }

//    @Override
//    public void setValue(String s) {
//        this.setValue(s, true);
//    }

    @Override
    public void setValueToView(String s) {
        if (!values.containsKey(s)) return;
        spinner.setSelection(Collections.binarySearch(showedValues, values.get(s)));
//        if (observer1 != null)
//            if (observerNotify) observer1.notify(name);
//            else observer1.changed(name);
    }

    @Override
    public String getValueFromView() {
        String selected = getShownValue();
        if (selected != null) {
            for (String s : values.keySet()) {
                if (values.get(s).equals(selected)) return s;
            }
        }
        return null;
    }

    public String getShownValue() {
        Object o = spinner.getSelectedItem();
        if (o == null) return null;
        else return o.toString();
    }

}















