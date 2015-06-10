package itrellis.com.todolist.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import itrellis.com.todolist.R;

/**
 * Created by JoshuaWilliams on 6/9/15.
 */
public class Utils {
    private static final String LOG_TAG = "Utils";
    private static Context context;




    /**
     * Retrieves a string from the strings.xml.
     * Mainly for classes that do not have context.
     *
     * @param stringResource - the resource of the desired string.
     * @return - the string from the resource.
     */
    public static String getString(int stringResource){
        return getContext().getString(stringResource);
    }

    /**
     * Stores a string inside the user's shared preferences.
     *
     * @param code - the code of the string.
     * @param content - the string being saved.
     */
    public static void saveToSharedPrefsInt(String code, int content) {
        Log.i(LOG_TAG, "Saving -- Code -- " + code + " || Content -- " + content);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.package_name) + code, content);
        editor.apply();
    }

    /**
     * Retrieves a color from the resources
     *
     * @param color - the resource color id
     * @return - the actual color value.
     */
    public static int getColor(int color) {
        return getContext().getResources().getColor(color);
    }


    /**
     * Retrieves a string from the shared preferences.
     *
     * @param code - code of the string to retrieve from the shared prefs.
     * @return - the appropriate string.
     */
    public static int getIntFromSharedPrefs(String code) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.i(LOG_TAG, "Retrieving From SharedPrefs => ----- Key -- " + code + " || Value --- " + preferences.getInt(getString(R.string.package_name) + code, 0));
        return preferences.getInt(getString(R.string.package_name) + code, 0);
    }


    /**
     * Getter and setter for the class' context.
     * @param mContext - the passed context;
     */
    public static void setContext(Context mContext){context = mContext;}
    public static Context getContext(){return context;}

    /**
     * Method that calculates the status bar height for the specific device.
     *
     * @return - the height of the status bar.
     */
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(LOG_TAG, "Status Bar Height - " + result);
        return result;
    }

    /**
     * For Android-L
     * Generic method to turn the status bar the color of the resousrce color passed.
     *
     * @param window - The window of the activity.
     * @param resourceColor - The color resource.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setUpStatusBar(Window window, int resourceColor) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(context.getResources().getColor(resourceColor));
    }

    /**
     * Shows a toast with the string message passed.
     *
     * @param string - the message to be displayed on the toast.
     */
    public static void showToast(String string) {
        Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }

    /**
     * Formats the time in milliseconds to a readable string.
     *
     * @param timeInMillis - the time in milliseconds.
     * @return - the readable date.
     */
    public static String getReadableTime(long timeInMillis){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timeInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy   h:mm a");
        return dateFormat.format(cal1.getTime());
    }
}
