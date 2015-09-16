package sg.edu.nus.nustranslator.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Vector;

import sg.edu.nus.nustranslator.R;
import sg.edu.nus.nustranslator.controllers.MainController;
import sg.edu.nus.nustranslator.models.States;
import sg.edu.nus.nustranslator.ultis.Configurations;

public class MainActivity extends Activity {

    //attributes
    private MainController controller;
    private View loadingView;
    private Spinner originalLanguageSpinner;
    private Spinner destinationLanguageSpinner;

    //events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.loadingView = findViewById(R.id.main_loading);
        this.originalLanguageSpinner = (Spinner) findViewById(R.id.originalLanguages_spinner);
        this.originalLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadingView.setVisibility(View.VISIBLE);
                controller.setOriginalLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                loadingView.setVisibility(View.VISIBLE);
                controller.setOriginalLanguage(-1);
            }

        });
        this.destinationLanguageSpinner = (Spinner) findViewById(R.id.destinationLanguages_spinner);
        this.destinationLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                controller.setDestinationLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                controller.setDestinationLanguage(-1);
            }

        });
        controller = new MainController(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_updateData:
                controller.updateData();
                this.loadingView.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_viewAllSentences:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                //show about
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * functions for mode: instant translation
     *
     */

    public void onSessionButtonClick(View view) {
        States currentAppState = controller.changeState();
        setResultView(currentAppState);
        setSessionButtonText(currentAppState);
    }
    private void setResultView(States appState) {
        View languageSelection = findViewById(R.id.languageSelection);
        View session = findViewById(R.id.sessionView);
        View sessionViewRecorded = findViewById(R.id.sessionViewRecorded);

        if (appState == States.ACTIVE) {
            TextView topResult = (TextView) findViewById(R.id.firstResult);
            topResult.setText("");
            TextView otherResult = (TextView) findViewById(R.id.otherResults);
            otherResult.setText("");
            TextView translatedResult = (TextView) findViewById(R.id.resultText);
            translatedResult.setText("");

            languageSelection.setVisibility(View.GONE);
            session.setVisibility(View.VISIBLE);
            sessionViewRecorded.setVisibility(View.GONE);
        } else {
            languageSelection.setVisibility(View.VISIBLE);
            session.setVisibility(View.GONE);
            sessionViewRecorded.setVisibility(View.GONE);
        }
    }

    /*
     * functions for mode: translation for recorded one
     * YM
     * Update the part for the recorded audio
     *
     */
    public void onSessionButtonUsingRecord(View view){
        States currentAppState = controller.changeStateRecordedOne();
        setResultViewRecordedOne(currentAppState);
        setSessionButtonText(currentAppState);
    }

    private void setResultViewRecordedOne(States appState) {
        View languageSelection = findViewById(R.id.languageSelection);
        View session = findViewById(R.id.sessionView);
        View sessionViewRecorded = findViewById(R.id.sessionViewRecorded);

        if (appState == States.ACTIVE) {
            TextView topResult = (TextView) findViewById(R.id.firstResult);
            topResult.setText("");
            TextView otherResult = (TextView) findViewById(R.id.otherResults);
            otherResult.setText("");
            TextView translatedResult = (TextView) findViewById(R.id.resultText);
            translatedResult.setText("");

            languageSelection.setVisibility(View.GONE);
            session.setVisibility(View.GONE);
            sessionViewRecorded.setVisibility(View.VISIBLE);
        } else {
            languageSelection.setVisibility(View.VISIBLE);
            session.setVisibility(View.GONE);
            sessionViewRecorded.setVisibility(View.GONE);
        }
    }

    /*
     * shared functions for both two modes: translation for recorded one and instant translation
     *
     */
    public void onFinishLoading() {
        this.loadingView.setVisibility(View.GONE);
    }

    public void updateSpeechRecognitionResult(Vector<String> results, String translatedResult) {
        if (results == null) {
            return;
        }
        //Set speech recognition result
        TextView topResult = (TextView) findViewById(R.id.firstResult);
        TextView similarResults = (TextView) findViewById(R.id.otherResults);
        if (results.size() == 0) {
            topResult.setText("");
            similarResults.setText("");
            return;
        }
        topResult.setText(results.get(0));
        String similarResultText = "";
        for (int i = 1; i < results.size(); i++) {
            similarResultText += results.get(i) + Configurations.Newline;
        }
        similarResults.setText(similarResultText);

        //set translation result
        TextView translationTextView = (TextView) findViewById(R.id.resultText);
        translationTextView.setText(translatedResult);
    }

    public void updateLanguageChoices(Vector<String> languages) {
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

        Spinner spinner = (Spinner) findViewById(R.id.originalLanguages_spinner);
        spinner.setAdapter(adapter);
        spinner = (Spinner) findViewById(R.id.destinationLanguages_spinner);
        spinner.setAdapter(adapter);
    }




    private void setSessionButtonText(States appState) {
        if (appState == States.ACTIVE) {
            Button button = (Button) findViewById(R.id.sessionButton);
            button.setText(R.string.button_session_active);
            Button button1 = (Button) findViewById(R.id.button_using_record);
            button1.setVisibility(View.INVISIBLE);
        } else {
            Button button = (Button) findViewById(R.id.sessionButton);
            button.setText(R.string.button_session_inactive);
            Button button1 = (Button) findViewById(R.id.button_using_record);
            button1.setVisibility(View.VISIBLE);
        }
    }



}
