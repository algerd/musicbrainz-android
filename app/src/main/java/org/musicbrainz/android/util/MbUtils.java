package org.musicbrainz.android.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A set of fairly general Android utility methods.
 */
public class MbUtils {

    public static String formatTime(Long millis) {
        if (millis == null) {
            return "";
        }
        long allsec = millis / 1000;
        long sec = allsec % 60;
        String secStr = sec < 10 ? "0"+sec : ""+sec;
        return allsec / 60 + ":" + secStr;
    }

    public static Intent shareIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND).setType("text/plain");
        return intent.putExtra(Intent.EXTRA_TEXT, text);
    }
    
    public static Intent emailIntent(String recipient, String subject) {
        Uri uri = Uri.parse("mailto:" + recipient);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        return intent;
    }
    
    public static Intent urlIntent(String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }
    
    public static String stringFromAsset(Context context, String asset) {
        try {
            InputStream input = context.getResources().getAssets().open(asset);
            byte[] buffer = new byte[input.available()];  
            input.read(buffer);  
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(buffer);  
            output.close();  
            input.close(); 
            return output.toString();
        } catch (IOException e) {
            Log.e("MusicBrain", "Error reading text file from assets folder.");
            return "";
        }
    }

    public static int getNumberDate(String date) {
        String[] dateArr = {"0000", "00", "00"};
        if (!TextUtils.isEmpty(date)) {
            String[] arr = date.split("-");
            for (int i = 0; i < arr.length; ++i) {
                dateArr[i] = arr[i];
            }
        }
        return Integer.valueOf(dateArr[0]) * 10000 + Integer.valueOf(dateArr[1]) * 100 + Integer.valueOf(dateArr[2]);
    }

}
