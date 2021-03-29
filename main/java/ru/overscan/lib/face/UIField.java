package ru.overscan.lib.face;

import android.view.View;
import android.widget.TextView;

import ru.overscan.lib.R;
import ru.overscan.lib.data.DataUtils;
import ru.overscan.lib.data.Field;

/**
 * Created by fizio on 02.05.2016.
 */
abstract public class UIField extends Field {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_CHANGED = 1;
    public static final int STATUS_NEW = 2;
    public static final int STATUS_NEED_CONFIRM = 3;
    public static final int STATUS_CONFIRMED = 4;

    View editView;
    TextView caption;
//    boolean auxField;
    public String initialValue;
    public FieldsObserver observer1;
    public int status;
//    private boolean visibility;


    public UIField(String name, View editView) {
        super(name);
        this.editView = editView;
//        auxField = false;
        status = STATUS_NONE;
//        visibility = true;
    }

//    public void setFieldAuxiliary(boolean auxiliary){
//        auxField = auxiliary;
//    }

    public void setInitialValue(String s) {
        initialValue = s;
        setValue(s);
    }

    @Override
    public void setValue(String s) {
        setValue(s, true);
//		if (edit != null) edit.setText(s);
//        if (observer != null) observer.notify(name);
    }

    public void setValue(String s, boolean observerNotify) {
        setValueToView(s);
        if (observer1 != null)
            if (observerNotify) observer1.notify(name);
            else observer1.changed(name);
    }

    @Override
    public String getValue() {
        return getValueFromView();
    }

    abstract protected void setValueToView(String s);

    abstract protected String getValueFromView();

    public boolean isChanged() {
        return DataUtils.stringsDiffer(initialValue, getValue());
    }

    public void setVisibility(boolean visible){
//        visibility = visible;
        if (visible) {
            if (caption != null) caption.setVisibility(View.VISIBLE);
            editView.setVisibility(View.VISIBLE);
        } else {
            if (caption != null) caption.setVisibility(View.GONE);
            editView.setVisibility(View.GONE);
        }

    }

    public boolean isVisible(){
//        return visibility;
        return editView.getVisibility() == View.VISIBLE;
    }


    public void setCaption(View parent, int resourceId){
        caption = (TextView) parent.findViewById(resourceId);
    }

    public void setStatus(int status){
        this.status = status;
        if (caption != null) {
            switch (status) {
                case STATUS_NONE:
                    caption.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, 0, 0);
                    break;
                case STATUS_CHANGED:
                    caption.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_mode_edit_black_24dp, 0, 0, 0);
                    break;
                case STATUS_CONFIRMED:
                    caption.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_check_circle_black_24dp, 0, 0, 0);
                    break;
                case STATUS_NEED_CONFIRM:
                    caption.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_help_black_24dp, 0, 0, 0);
                    break;
                case STATUS_NEW:
                    caption.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_add_circle_black_24dp, 0, 0, 0);
                    break;
//                case STATUS_:
//                    caption.setCompoundDrawablesWithIntrinsicBounds(
//                            R.drawable., 0, 0, 0);
//                    break;
            }
        }
    }

}
