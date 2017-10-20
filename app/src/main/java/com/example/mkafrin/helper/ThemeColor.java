package com.example.mkafrin.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.ListPreference;
import android.util.TypedValue;
import com.example.mkafrin.firstapp.R;

public class ThemeColor {
    public static void setTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("theme_color", "Rolling Green");
        if(value.equalsIgnoreCase("Rolling Green")) {
            context.getTheme().applyStyle(R.style.OverlayPrimaryColorTealGreen, true);
        } else if(value.equalsIgnoreCase("Sky Blue")) {
            context.getTheme().applyStyle(R.style.OverlayPrimaryColorSkyBlue, true);
        } else if(value.equalsIgnoreCase("Autumn Orange")) {
            context.getTheme().applyStyle(R.style.OverlayPrimaryColorAutumnOrange, true);
        } else if(value.equalsIgnoreCase("Racing Red")) {
            context.getTheme().applyStyle(R.style.OverlayPrimaryColorRacingRed, true);
        } else if(value.equalsIgnoreCase("Orchid Purple")) {
            context.getTheme().applyStyle(R.style.OverlayPrimaryColorOrchidPurple, true);
        } else if(value.equalsIgnoreCase("Royal Blue")) {
            context.getTheme().applyStyle(R.style.OverlayPrimaryColorRoyalBlue, true);
        } else {
            context.getTheme().applyStyle(R.style.OverlayPrimaryColorTealGreen, true);
        }
    }

    public static void setDefaultValue(Context context, ListPreference listPreference) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        CharSequence[] entries = listPreference.getEntries();
        String value = sharedPreferences.getString("theme_color", "Rolling Green");
        for (int i = 0; i < entries.length; i++) {
            String temp = entries[i].toString();
            if(temp.equalsIgnoreCase(value)) {
                listPreference.setDefaultValue(i);
                break;
            }
        }
    }
}
