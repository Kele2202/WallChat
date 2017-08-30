package com.ferit.kele.wallchat;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.Locale;


public class SettingsActivity extends AppCompatActivity {

    Spinner spinner;
    int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.setUpUI();
    }

    private void setUpUI() {
        this.spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt(getString(R.string.settAct_ChooseLang));

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        final SharedPreferences.Editor editor = pref.edit();

        if(pref.getInt("position",0)>0)
        {
            spinner.setSelection(pref.getInt("position", 0));
            flag = pref.getInt("position", 0);
        }
        else{
            spinner.setSelection(0);
            flag = 0;
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration config = new Configuration();
                Locale locale = null;
                editor.putInt("position", position);
                editor.commit();
                switch(position){
                    case 0:
                        config.locale = locale.ENGLISH;
                        break;
                    case 1:
                        locale = new Locale("hr");
                        config.locale = locale;
                        break;
                    default:
                        config.locale = Locale.ENGLISH;
                        break;
                }
                getResources().updateConfiguration(config, null);
                if (flag != pref.getInt("position",0)) {
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
