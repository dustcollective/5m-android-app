package com.m5.android.avicola;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import com.m5.android.avicola.app.Constants;
import com.m5.android.avicola.model.Content;

public class SettingsActivity extends PreferenceActivity {

    ListPreference listPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        //territories
        listPreference = (ListPreference) findPreference("pref_key_territory");
        final String[] values = new String[Content.Territory.values().length];
        int i = 0;
        for (Content.Territory territory : Content.Territory.values()) {
            values[i] = territory.firstValue();
            i++;
        }

        listPreference.setEntries(values);
        listPreference.setEntryValues(values);
        listPreference.setDefaultValue(Content.Territory.ALL.firstValue());

        //save and exit button
        final Preference button = (Preference) findPreference("exit");
        if (button != null) {
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    exit();
                    return true;
                }
            });
        }
    }

    protected void exit() {
        String listValue = listPreference.getValue();
        Content.Territory territory = null;
        final Intent data = new Intent();
        if (!TextUtils.isEmpty(listValue)) {
            territory = Content.findTerritory(listValue);
            data.putExtra(Constants.EXTRAS_TERRITORY, territory);
        }

        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}
