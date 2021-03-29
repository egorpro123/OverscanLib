package ru.overscan.lib.face;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ru.overscan.lib.face.UIField;

/**
 * Created by fizio on 29.04.2016.
 */
public class FieldsObserver {
//    static final String FIELD =
//            FieldsObserver.class.getName().replaceAll("\\.", "_") +
//                    "_field";

    public interface OnBasicFieldChangedListener{
        // в случае изменения поля previousFields должен быть передан через
        // notify(String name, ArrayList<String> previousFields)
//        void onBasicFieldChanged(ArrayList<String> previousFields);
        void onBasicFieldChanged();
    }

//    public interface VisibilityChecker{
//        // в случае изменения поля previousFields должен быть передан через
//        // notify(String name, ArrayList<String> previousFields)
//        boolean mustBeVisible();
//    }

    class FieldChangedHandler {
        UIField field;
        OnBasicFieldChangedListener listener;
        public FieldChangedHandler(UIField field, OnBasicFieldChangedListener listener) {
            this.field = field;
            this.listener = listener;
        }
    }

    HashMap<String, ArrayList<FieldChangedHandler>> observableFields;
    HashMap<String, UIField> fields;
    HashMap<String, Boolean> changedFields;
    ArrayList<String> previousFields;

    public FieldsObserver(){
        observableFields = new HashMap<>();
        fields = new HashMap<>();
        changedFields = new HashMap<>();
        previousFields = null;
    }

    public void initFields(){
        for(String fld: fields.keySet()){
//            if (fields.get(fld).isVisible())
                notify(fld);
        }
    }

    public void addField(UIField f){
        addField((String[]) null, f, null);
    }

    public void addField(String dependent, UIField f, OnBasicFieldChangedListener l){
        addField(new String[] { dependent }, f, l);
    }

    public void addField(String[] dependents, UIField f, OnBasicFieldChangedListener l){
//        f.onBasicFieldChangedListener = l;
//        addField(dependents, f);
        if (dependents != null) {
            FieldChangedHandler handler = new FieldChangedHandler(f, l);
            for (int i = 0; i < dependents.length; i++) {
                if (observableFields.containsKey(dependents[i])) {
                    observableFields.get(dependents[i]).add(handler);
//                    if (!a.contains(f)) a.add(f);
                } else {
                    ArrayList<FieldChangedHandler> a = new ArrayList<>();
                    a.add(handler);
                    observableFields.put(dependents[i], a);
                }
            }
        }
        fields.put(f.name, f);
        f.observer1 = this;
    }

//    public void addField(String dependent, UIField f){
//        addField(new String[] { dependent }, f);
//    }
//
//    public void addField(String[] dependents, UIField f){
//        if (dependents != null)
//            for (int i = 0; i < dependents.length; i++) {
//                if (observableFields.containsKey(dependents[i])) {
//                    ArrayList<UIField> a = observableFields.get(dependents[i]);
//                    if (!a.contains(f)) a.add(f);
//                } else {
//                    ArrayList<UIField> a = new ArrayList<>();
//                    a.add(f);
//                    observableFields.put(dependents[i], a);
//                }
//            }
//        fields.put(f.name, f);
//        f.observer = this;
//    }

    synchronized public void removeField(UIField f){

        Iterator<String> it = observableFields.keySet().iterator();
        while (it.hasNext())
        {
            String s = it.next();
            boolean was = false;
            for (FieldChangedHandler h: observableFields.get(s))
                if (h.field == f) {
                    was = true;
                    break;
                }
            if (was) {
                ArrayList<FieldChangedHandler> novi = new ArrayList<>();
                for (FieldChangedHandler h: observableFields.get(s)) {
                    if (h.field != f) novi.add(h);
                }
                if (novi.size() > 0) observableFields.put(s, novi);
                else it.remove();
            }
        }
        if (observableFields.containsKey(f.name)) observableFields.remove(f.name);
        if (fields.containsKey(f.name)) fields.remove(f.name);


//        for (String s: observableFields.keySet()) {
////            ArrayList<FieldChangedHandler> novi =
////                    ArrayList<FieldChangedHandler>
////            ArrayList<FieldChangedHandler> a = observableFields.get(s);
//            boolean was = false;
//            for (FieldChangedHandler h: observableFields.get(s))
//                if (h.field == f) {
//                    was = true;
//                    break;
//                }
//            if (was) {
//                ArrayList<FieldChangedHandler> novi = new ArrayList<>();
//                for (FieldChangedHandler h: observableFields.get(s)) {
//                    if (h.field != f) novi.add(h);
//                }
//                if (novi.size() > 0) observableFields.put(s, novi);
//            }
//        }
    }

    public void changed(String name){
        changedFields.put(name, true);
    }

    public void notifyChanged(){
        ArrayList<FieldChangedHandler> notified = new ArrayList<>();
        for(String s: changedFields.keySet()){
            if (changedFields.get(s)) {
                changedFields.put(s, false);
                for (FieldChangedHandler h: observableFields.get(s))
                    if (!notified.contains(h)) notified.add(h);
            }
        }
        if (notified.size() > 0)
            for (FieldChangedHandler h: notified) {
                ArrayList<String> previousFields = new ArrayList<>();
                previousFields.add(h.field.name);
//                if (fields.get(h.field.name).isVisible())
                h.listener.onBasicFieldChanged();
//                h.listener.onBasicFieldChanged(previousFields);
            }
    }

//    public void notify(String name){
//        notify(name, null);
//    }

    public void notify(String name){
        if (observableFields.containsKey(name) &&
                (previousFields == null || !previousFields.contains(name))) {
            boolean firstNotify;

            if (previousFields == null) {
                previousFields = new ArrayList<>();
                firstNotify = true;
            }
            else firstNotify = false;
            try {
                previousFields.add(name);
                for (FieldChangedHandler h : observableFields.get(name))
                    if (!previousFields.contains(h.field.name))
                        //                            fields.get(h.field.name).isVisible())
                        h.listener.onBasicFieldChanged();
//                        h.listener.onBasicFieldChanged(previousFields);
            } finally {
                if (firstNotify) previousFields = null;
            }
        }
    }

//    public void notify(String name){
//        notify(name, null);
//    }
//
//    public void notify(String name, ArrayList<String> previousFields){
//        if (observableFields.containsKey(name)) {
//            if (previousFields == null) previousFields = new ArrayList<>();
//            previousFields.add(name);
//            for (FieldChangedHandler h: observableFields.get(name))
//                if (!previousFields.contains(h.field.name))
//                    h.listener.onBasicFieldChanged(previousFields);
////            ArrayList<FieldChangedHandler> a = observableFields.get(name);
////            for (int i = 0; i < a.size(); i++) {
////                UIField f = a.get(i);
////                if (!previousFields.contains(f.name)) {
////                    if (f.onBasicFieldChangedListener != null)
////                        f.onBasicFieldChangedListener.onBasicFieldChanged(previousFields);
////                    else if (f instanceof OnBasicFieldChangedListener)
////                    ((OnBasicFieldChangedListener) f).onBasicFieldChanged(previousFields);
////                }
//////                    f.dependentChanged(previous);
////            }
//        }
//    }

    public UIField getField(String name) {
        if (fields.containsKey(name)) return fields.get(name);
        else return null;
    }

    public String getValue(String fieldName){
        if (fields.containsKey(fieldName))
//                fields.get(fieldName).isVisible())
            return fields.get(fieldName).getValue();
        else return null;
    }

    public void setValue(String fieldName, String value){
        if (fields.containsKey(fieldName)) {
            fields.get(fieldName).setValue(value);
            changedFields.put(fieldName, true);
        }
    }

    class ObserverTextWatcher implements TextWatcher {
        String fieldName;
        String prevValue;

        public ObserverTextWatcher(String fieldName) {
            this.fieldName = fieldName;
            prevValue = null;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().trim().equals(prevValue)) {
                prevValue = s.toString();
                FieldsObserver.this.notify(fieldName);
            }
        }
    }

    public void addChangeListener(UITextField edit){
        edit.edit.addTextChangedListener(new ObserverTextWatcher(edit.name));
    }


//    public void addChangeListener(final UITextField edit){
//        edit.edit.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                FieldsObserver.this.notify(edit.name);
//            }
//        });
//    }


//    public void addVisibilityChecker(String dependent, final UITextField field,
//                                     final VisibilityChecker checker){
//        addVisibilityChecker(new String[] {dependent}, field, checker);
//    }
//
//    public void addVisibilityChecker(String[] dependents, final UITextField field,
//                                     final VisibilityChecker checker){
//        addField(dependents, field, new OnBasicFieldChangedListener() {
//            @Override
//            public void onBasicFieldChanged(ArrayList<String> previousFields) {
//                field.setVisibility(checker.mustBeVisible());
//            }
//        });
//    }

//    class ChangedHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            fields.get(msg.getData().getString(FIELD)).dependentChanged();
//        }
//
//    }

}













