package sg.edu.nus.nustranslator.presentation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Vector;

import sg.edu.nus.nustranslator.Configurations;
import sg.edu.nus.nustranslator.R;
import sg.edu.nus.nustranslator.business.MainBusiness;
import sg.edu.nus.nustranslator.model.States;

public class MainActivity extends Activity {

    //attributes
    MainBusiness mainBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainBusiness = new MainBusiness(this);
        addItemsToSpinners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //events
    public void onSessionButtonClick(View view) {
        States currentAppState = mainBusiness.changeState();
        setResultView(currentAppState);
        setSessionButtonText(currentAppState);
    }

    public void updateSpeechRecognitionResult(Vector<String> results) {
        if(results == null) {
            return;
        }
        TextView topResult = (TextView) findViewById(R.id.firstResult);
        TextView similarResults = (TextView) findViewById(R.id.otherResults);
        if (results.size() == 0) {
            topResult.setText("");
            similarResults.setText("");
        }
        topResult.setText(results.get(0));
        String similarResultText = "";
        for (int i = 1; i < results.size(); i++) {
            similarResultText += results.get(i) + Configurations.newline;
        }
        similarResults.setText(similarResultText);
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
