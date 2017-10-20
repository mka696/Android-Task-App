package com.example.mkafrin.firstapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import com.example.mkafrin.helper.ThemeColor;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(R.style.AppTheme_ListPreference, true);
        ThemeColor.setTheme(this);
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            Preference about = findPreference("about");
            ListPreference theme = (ListPreference) findPreference("theme_color");
            ThemeColor.setDefaultValue(getActivity(), theme);
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    /*Dialog dialog = new Dialog(getActivity());
                    dialog.setTitle("About");
                    dialog.setContentView(R.layout.about_dialog);
                    dialog.show();*/

                    new AlertDialog.Builder(getActivity())
                            .setTitle("About")
                            .setView(LayoutInflater.from(getActivity()).inflate(R.layout.about_dialog, null))
                            .show();

                    return true;
                }
            });
            theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String themeColor = o.toString();
                    SharedPreferences sharedPreferences = getActivity()
                            .getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("theme_color", themeColor);
                    editor.apply();
                    ThemeColor.setTheme(getActivity());
                    getActivity().recreate();
                    return true;
                }
            });
        }
    }
}
