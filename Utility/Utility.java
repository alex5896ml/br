package com.mobmedianet.trackergps.Project.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobmedianet.trackergps.R;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by Cesar on 3/27/2015.
 */
public class Utility {
    public static final String empty_fields = "HAY CAMPOS VACIOS O NO SELECCIONADOS";
    static AlertDialog aDialog;

    //hide keyboard
    public static void hideKeyboard(View view, Activity act) {
        InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //create custom progress dialog
    public static Dialog create_dialog(Context context, String message) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.customprogressdialog);
        TextView tv_message = (TextView) alertDialog.findViewById(R.id.tv_dialog_text);
        tv_message.setText(message);
        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(130);
        alertDialog.getWindow().setBackgroundDrawable(d);
        alertDialog.setCancelable(false);
        alertDialog.show();
        return alertDialog;

    }

    // show a info dialog
    public static void show_msg(Context context, String msg) {
        // Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout L1 = (LinearLayout) inflater.inflate(
                R.layout.dialog_custom_msg, null);

        TextView go = (TextView) L1.findViewById(R.id.tv_accept);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aDialog.dismiss();

            }
        });
        TextView text = (TextView) L1.findViewById(R.id.text);
        text.setText(msg);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(L1);
        aDialog = builder.create();
        aDialog.show();
        aDialog.setCanceledOnTouchOutside(false);
        aDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(aDialog.getWindow().getAttributes());
        // float scale = context.getResources().getDisplayMetrics().density;
        lp.height = Math.round(context.getResources().getDimension(R.dimen.dialog_height));
        lp.width = Math.round(context.getResources().getDimension(R.dimen.dialog_width));
        aDialog.getWindow().setAttributes(lp);
        aDialog.show();
    }

    //show dialog with fixed text
    public static void show_emptyfieldsmsg(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout L1 = (LinearLayout) inflater.inflate(
                R.layout.dialog_custom_msg, null);

        TextView go = (TextView) L1.findViewById(R.id.tv_accept);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aDialog.dismiss();
            }
        });

        TextView text = (TextView) L1.findViewById(R.id.text);
        text.setText(empty_fields);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(L1);
        aDialog = builder.create();
        aDialog.show();
        aDialog.setCanceledOnTouchOutside(false);
        aDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(aDialog.getWindow().getAttributes());
        // float scale = context.getResources().getDisplayMetrics().density;
        lp.height = Math.round(context.getResources().getDimension(R.dimen.dialog_height));
        lp.width = Math.round(context.getResources().getDimension(R.dimen.dialog_width));
        aDialog.getWindow().setAttributes(lp);
        aDialog.show();
    }

    //covert sp to pixels
    public static float sptopx(int sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    // flip date
    public static String flip_date(String s) {
        return s.substring(8, 10) + "-" + s.substring(5, 7) + "-" + s.substring(0, 4);
    }


    public static Bitmap decodeSampledBitmapFromResource(InputStream is1, InputStream is2) {

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is1, null, options);

        // Calculate inSampleSize
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(options, 800, 600);

        // Decode bitmap with inSampleSize set

        return BitmapFactory.decodeStream(is2, null, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void save_preferences(String name, String value, Context context) {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPreference1.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public static void save_preferences_int(String name, int value, Context context) {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPreference1.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    public static String get_preferences(String name, Context context) {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(context);
        return mSharedPreference1.getString(name, "");

    }

    public static int get_preferences_int(String name, Context context) {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(context);
        return mSharedPreference1.getInt(name, 0);

    }

    public static void insertfirstfield(ArrayList<String> data, ArrayList<String> ids) {
        data.add(0, "Seleccione");
        ids.add(0, "0");
    }

    public static String convertDate(String date_str) {
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat DesiredFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        // 'a' for AM/PM

        Date date = null;
        try {
            date = sourceFormat.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null && !date.toString().isEmpty())
            return DesiredFormat.format(date.getTime());
        else
            return date_str;
    }


    public static String invertDate(String date_str) {
        SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat DesiredFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        // 'a' for AM/PM

        Date date = null;
        try {
            date = sourceFormat.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null && !date.toString().isEmpty())
            return DesiredFormat.format(date.getTime());
        else
            return date_str;
    }

    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public boolean validEmailAddress(String strEmail) {
        return EMAIL_ADDRESS_PATTERN.matcher(strEmail).matches();
    }

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception ex) {
            ex.getMessage();
            return false;
        }
    }

    public static void showAlert(Context context, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setMessage(msg)
                .setCancelable(false);
        alertDialogBuilder.setNegativeButton((R.string.opt_close),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    public static String getDeviceId(Context context){

        return Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
    }




}
