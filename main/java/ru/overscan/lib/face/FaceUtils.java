package ru.overscan.lib.face;

import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ru.overscan.lib.R;
import ru.overscan.lib.sys.OverscanApplication;

public class FaceUtils {
    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    public static class FieldGroup{
        public LinearLayout layout;
        public TextView text;
        public EditText edit;
        public String getValue(){
            return edit.getEditableText().toString();
        }
        public void setValue(String s){
            edit.setText(s);
        }
    }

    public static FieldGroup createFieldGroup(Context context, String name){
        FieldGroup field = new FieldGroup();
        field.layout = FaceUtils.createLinearLayout(context, FaceUtils.MATCH_PARENT,
                FaceUtils.WRAP_CONTENT, LinearLayout.HORIZONTAL, 0);
        field.text = new TextView(context);
        field.text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimension(R.dimen.main_text_size));
        field.text.setText(name);
        field.layout.addView(field.text, new LinearLayout.LayoutParams(
                FaceUtils.WRAP_CONTENT, FaceUtils.WRAP_CONTENT));
        field.edit = new EditText(context);
        field.layout.addView(field.edit, new LinearLayout.LayoutParams(
                0, FaceUtils.WRAP_CONTENT, 1.0f));
        return field;
    }

	public static void setEditTextDisable(EditText e) {
		e.setInputType(InputType.TYPE_NULL);
		e.setFocusable(false);
	}

    public static void createScrolledLinearLayout(Context context,
                                                  ViewGroup root, LinearLayout layout){
        ScrollView scroll = new ScrollView(context);
        scroll.setLayoutParams(new ScrollView.LayoutParams(
                FaceUtils.MATCH_PARENT, FaceUtils.MATCH_PARENT));
        scroll.setFillViewport(true);
        layout = createLinearLayout(context,
                FaceUtils.MATCH_PARENT, FaceUtils.MATCH_PARENT,
                LinearLayout.VERTICAL,0);
        int h = context.getResources().getDimensionPixelSize(
                R.dimen.activity_horizontal_margin);
        int v = context.getResources().getDimensionPixelSize(
                R.dimen.activity_vertical_margin);
        layout.setPadding(h, v, h, v);
        scroll.addView(layout);
        root = scroll;
    }


    public static class ScrolledLinearLayout{
        public ViewGroup root;
        public LinearLayout layout;
    }

    public static ScrolledLinearLayout createScrolledLinearLayout(
            Context context) {
//    static RootViews getRootViews(Context context) {
        FrameLayout root0 = new FrameLayout(context);
        root0.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        int h = context.getResources().getDimensionPixelSize(
                ru.overscan.lib.R.dimen.activity_horizontal_margin);
        int v = context.getResources().getDimensionPixelSize(
                ru.overscan.lib.R.dimen.activity_vertical_margin);
        root0.setPadding(h, v, h, v);

//        int p = DisplayUtils.densityPixels2pixels(context, 16);
//        root0.setPadding(p, p, p, 0);

        ScrollView scroll = new ScrollView(context);
        // без нижних 2х строки не позиционируется
        scroll.setLayoutParams(FaceUtils.createLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        scroll.setFillViewport(true);
        root0.addView(scroll);

        LinearLayout root = FaceUtils.createLinearLayout(context, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.VERTICAL, 0);
        scroll.addView(root);
        ScrolledLinearLayout r = new ScrolledLinearLayout();
        r.root = root0;
        r.layout = root;
        return r;
    }


    public static class ScrolledLinearLayoutWithBottom{
        public LinearLayout root;
        public LinearLayout layout;
        public LinearLayout bottom;
    }

//    public static ScrolledLinearLayoutWithBottom createScrolledLinearLayoutWithBottom(
//            Context context){
//        ScrolledLinearLayoutWithBottom layouts = new ScrolledLinearLayoutWithBottom();
//        layouts.root = createLinearLayout(context,
//                FaceUtils.MATCH_PARENT, FaceUtils.MATCH_PARENT,
//                LinearLayout.VERTICAL, 0);
//        int h = context.getResources().getDimensionPixelSize(
//                R.dimen.activity_horizontal_margin);
//        int v = context.getResources().getDimensionPixelSize(
//                R.dimen.activity_vertical_margin);
//        layouts.root.setPadding(h, v, h, v);
//        layouts.layout = layouts.root;
//        return layouts;
//    }

    public static ScrolledLinearLayoutWithBottom createScrolledLinearLayoutWithBottom(
            Context context){
        ScrolledLinearLayoutWithBottom layouts = new ScrolledLinearLayoutWithBottom();
        layouts.root = createLinearLayout(context,
                FaceUtils.MATCH_PARENT, FaceUtils.MATCH_PARENT,
                LinearLayout.VERTICAL, 0);
        int h = context.getResources().getDimensionPixelSize(
                R.dimen.activity_horizontal_margin);
        int v = context.getResources().getDimensionPixelSize(
                R.dimen.activity_vertical_margin);
        layouts.root.setPadding(h, v, h, v);
        ScrollView scroll = new ScrollView(context);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                FaceUtils.MATCH_PARENT, FaceUtils.MATCH_PARENT, 1.0F));
//        scroll.setLayoutParams(new LinearLayout.LayoutParams(
//                FaceUtils.MATCH_PARENT, FaceUtils.MATCH_PARENT, 1.0F));
        scroll.setFillViewport(true);
        layouts.layout = createLinearLayout(context, FaceUtils.MATCH_PARENT,
                FaceUtils.MATCH_PARENT, LinearLayout.VERTICAL, 0);
        scroll.addView(layouts.layout);
        layouts.root.addView(scroll);
        layouts.bottom = createLinearLayout(context, FaceUtils.MATCH_PARENT,
                FaceUtils.WRAP_CONTENT, LinearLayout.HORIZONTAL, 0);
        layouts.bottom.setGravity(Gravity.RIGHT);
        layouts.root.addView(layouts.bottom);
        return layouts;
    }

    public static FrameLayout createFrameLayoutMatchParent(Context context){
        FrameLayout frame = new FrameLayout(context);
        frame.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
//        reviewsViewId = FaceUtils.generateViewId();
        frame.setId(FaceUtils.generateViewId());
        return frame;
    }

    public static FrameLayout createFrameLayoutMatchWidth(Context context){
        FrameLayout frame = new FrameLayout(context);
        frame.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
//        reviewsViewId = FaceUtils.generateViewId();
        frame.setId(FaceUtils.generateViewId());
        return frame;
    }

    public static LayoutParams createLayoutParams(int widthParam,
                                                  int heightParam) {
        return new LayoutParams(widthParam, heightParam);
    }

	public static LayoutParams createLayoutParams(int widthParam,
			int heightParam, float weight) {
		if (weight > 1e-3) 
			return new LayoutParams(widthParam, heightParam, weight);
        else return new LayoutParams(widthParam, heightParam);
	}
	
	public static LinearLayout createLinearLayout(Context context, int widthParam,
			int heightParam, int orientationParam, float weight) {
		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(createLayoutParams(widthParam,
				heightParam, weight));
        layout.setOrientation(orientationParam);
        return layout;
	}
	
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @SuppressLint("NewApi")
    public static int generateViewId() {

        if (Build.VERSION.SDK_INT < 17) {
            for (;;) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }

    }
    
    public static ProgressDialog showDownloadWindow(Context context) {
    	ProgressDialog pd = new ProgressDialog(context);
//		pd.setTitle("Processing...");
//		pd.setMessage("Загрузка...");
    	
		pd.setMessage(context.getResources().getString(R.string.load_datas));
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.show();
		return pd;
    }
    
    public static void showInfoToUser(String info) {
    	Toast.makeText(OverscanApplication.getInstance(),
    		      info, Toast.LENGTH_LONG).show();
    }

    public static void showErrorToUser(String err) {
    	Toast.makeText(OverscanApplication.getInstance(),
    		      err, Toast.LENGTH_LONG).show();
    }

    public static String makeProsAndConsString(int pros, int cons) {
        return "За: " + Integer.toString(pros) + " Против: " + Integer.toString(cons);
    }

    public static void showToast(Context context, String text){
        Toast.makeText(context.getApplicationContext(),
                text, Toast.LENGTH_SHORT).show();
    }
}
