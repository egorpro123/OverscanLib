package ru.overscan.lib.face;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtils {
	
	// рассчитывает количество обычных пикселов по dp 
	public static int densityPixels2pixels(Context c, int dp) {
		float density = c.getResources().getDisplayMetrics().density;
		return (int)(dp * density);
	}		
	
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static Point getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenResolution = new Point();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            display.getSize(screenResolution);
        } else {
            screenResolution.set(display.getWidth(), display.getHeight());
        }
        return screenResolution;
    }

	public static Point getScreenResolutionByMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Point p = new Point(dm.widthPixels, dm.heightPixels);
        return p;
    }    
    
    @SuppressWarnings("deprecation")
	public static int getScreenOrientation(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(display.getWidth()==display.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(display.getWidth() < display.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    // !!! не проверено
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation
            == Configuration.ORIENTATION_LANDSCAPE;
    }
}
