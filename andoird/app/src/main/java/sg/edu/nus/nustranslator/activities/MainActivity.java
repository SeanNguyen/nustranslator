package sg.edu.nus.nustranslator.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Vector;

import sg.edu.nus.nustranslator.ultis.Configurations;
import sg.edu.nus.nustranslator.R;
import sg.edu.nus.nustranslator.controllers.MainController;
import sg.edu.nus.nustranslator.models.States;

public class MainActivity extends Activity {

    //attributes
    MainController mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainController = new MainController(this);
        addItemsToSpinners();
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
                mainController.updateData();
                return true;
            case R.id.action_about:
                //show about
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //events
    public void onSessionButtonClick(View view) {
        States currentAppState = mainController.changeState();
        setResultView(currentAppState);
        setSessionButtonText(currentAppState);
    }

    public void updateSpeechRecognitionResult(Vector<String> results, String translatedResult) {
        if(results == null) {
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

    //private helper methods
    private void addItemsToSpinners() {
        String[] languages = {"English", "Mandarin", "Vietnamese", "Thai"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, languages) {

            public View getView(int position, View convertView,ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(16);
                return v;
            }

            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
        };

        Spinner spinner = (Spinner) findViewById(R.id.originalLanguages_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner = (Spinner) findViewById(R.id.destinationLanguages_spinner);
        spinner.setAdapter(adapter);
    }

    private void setResultView(States appState) {
        View languageSelection = findViewById(R.id.languageSelection);
        View session = findViewById(R.id.sessionView);

        if (appState == States.ACTIVE) {
            TextView topResult = (TextView) findViewById(R.id.firstResult);
            topResult.setText("");
            TextView otherResult = (TextView) findViewById(R.id.otherResults);
            otherResult.setText("");
            TextView translatedResult = (TextView) findViewById(R.id.resultText);
            translatedResult.setText("");

            languageSelection.setVisibility(View.GONE);
            session.setVisibility(View.VISIBLE);
        } else {
            languageSelection.setVisibility(View.VISIBLE);
            session.setVisibility(View.GONE);
        }
    }

    private void setSessionButtonText(States appState) {
        if (appState == States.ACTIVE) {
            Button button = (Button) findViewById(R.id.sessionButton);
            button.setText(R.string.button_session_active);
        } else {
            Button button = (Button) findViewById(R.id.sessionButton);
            button.setText(R.string.button_session_inactive);
        }
    }

}
