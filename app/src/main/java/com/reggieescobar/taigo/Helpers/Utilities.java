package com.reggieescobar.taigo.Helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

//import livemoments.io.test.Objects.Moment;

/**
 * Created by rescobar on 02/05/2016.
 */
public class Utilities {
    public static String DateFormatString = "MM/dd/yyyy";
    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context,Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static String formatIntegerToString(double value) {
        int power;
        String suffix = " kmbt";
        String formattedNumber = "";

        NumberFormat formatter = new DecimalFormat("#,###.#");
        power = (int)StrictMath.log10(value);
        value = value/(Math.pow(10,(power/3)*3));
        formattedNumber=formatter.format(value);
        formattedNumber = formattedNumber + suffix.charAt(power/3);
        return formattedNumber.length()>4 ?  formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
    }


    public static byte[] convertImageToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static String convertImageToBase64(Bitmap bitmap){
        String base64String = Base64.encodeToString(convertImageToByte(bitmap),
                Base64.NO_WRAP);
        return  base64String;
    }

    public static Bitmap resizeBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();


        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            if(width > maxSize){
                width = maxSize;
                height = (int) (width / bitmapRatio);
            }
        } else {
            if(height > maxSize) {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
        }

        //Bitmap.createBitmap()
       return Bitmap.createScaledBitmap(image, width, height, false);

        //return Bitmap.createBitmap(image,image.getWidth(),0,width,height);
    }

    public static int getScreenWidth(Activity activity){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;

    }


 /*   public static ArrayList<Moment>reverseMomentList(ArrayList<Moment> list){
        ArrayList<Moment> newList = new ArrayList<>();
        for(int i = (list.size() - 1); i >= 0; i--){
            newList.add(list.get(i));
        }

        return newList;

    } */

    /** DAIR NOTE:
     * converts time (in milliseconds) to human-readable format
     *  "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String millisToLongDHMS(long duration) {
        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                duration -= temp * ONE_DAY;
                res.append(temp).append(" day").append(temp > 1 ? "s" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_HOUR;
            if (temp > 0) {
                duration -= temp * ONE_HOUR;
                res.append(temp).append(" hour").append(temp > 1 ? "s" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
            }

            if (!res.toString().equals("") && duration >= ONE_SECOND) {
                res.append(" and ");
            }

            temp = duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp).append(" second").append(temp > 1 ? "s" : "");
            }
            return res.toString();
        } else {
            return "0 second";
        }
    }


    public static int convertDpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int convertPxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String getNewUUID(){
        return UUID.randomUUID().toString() + "-" +  (new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date()));
    }

    public static long getTimeUnix(){
        return System.currentTimeMillis() / 1000L;
    }

    public static String ConvertDateToString(Date date){
        DateFormat dateFormat = new SimpleDateFormat(Utilities.DateFormatString);
        return dateFormat.format(date);
    }

    public static Date ConvertStringToDate(String date){
        DateFormat dateFormat = new SimpleDateFormat(Utilities.DateFormatString);
        Date dt = null;
        try {
            dt = dateFormat.parse(date);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return dt;
    }

    public static Date getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat(Utilities.DateFormatString);
        return ConvertStringToDate(dateFormat.format(new Date()));
    }




    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(Context context, View view) {
        if(context !=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public static void showSoftKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }


    public static ArrayList<String> noCapitalizedWords = new ArrayList<>();
    //public static String[] list = {"and", "but", "or", "for", "nor", "a", "an", "on", "at", "to", "from", "by"};
    public static String[] list = {};
    public static String convertToTileCase(String text){
        String title = "";
        if(!text.trim().equals("")) {
            noCapitalizedWords.clear();
            for (String s : list) {
                noCapitalizedWords.add(s);
            }

            String[] words = capitalizeForTitle(text.toLowerCase().trim().split(" "));


            for (String word : words) {
                title += (word + " ");
            }
        }
        return title.trim();
    }

    private static String[] capitalizeForTitle(String[] words){
        for(int a = 0; a < words.length; a++){
            if(a == 0){
                if(words[a].length() == 1){
                    words[a] = words[a].toUpperCase();
                }else{
                    words[a] = (words[a].substring(0, 1).toUpperCase() + words[a].substring(1).toLowerCase());
                }

            }else {
                if (!noCapitalizedWords.contains(words[a])) {
                    words[a] = (words[a].substring(0, 1).toUpperCase() + words[a].substring(1).toLowerCase());
                }
            }
        }
        return words;
    }


    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
