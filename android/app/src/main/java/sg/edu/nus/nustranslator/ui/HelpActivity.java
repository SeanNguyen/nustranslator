package sg.edu.nus.nustranslator.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Vector;

import sg.edu.nus.nustranslator.R;
import sg.edu.nus.nustranslator.controllers.HelpController;

public class HelpActivity extends Activity {

    private HelpController controller;
    private Spinner languageSpinner;
    private ListView sentenceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        this.languageSpinner = (Spinner) findViewById(R.id.help_languageSpinner);
        this.languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                changeLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                changeLanguage(-1);
            }

        });
        this.sentenceList = (ListView) findViewById(R.id.help_sentenceList);

        this.controller = new HelpController(this);
    }

    //public methods
    public void updateLanguageList(Vector<String> languages) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, languages) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(16);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.languageSpinner.setAdapter(adapter);
    }

    public void updateSentenceList(Vector<String> sentences) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sentence_item_layout, sentences) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                return v;
            }
        };

        this.sentenceList.setAdapter(adapter);
    }

    private void changeLanguage(int index) {
        this.controller.changeLanguage(index);
    }
}
