package sg.edu.nus.nustranslator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

enum States {
    ACTIVE,
    INACTIVE
};

public class MainActivity extends Activity {

    //attributes
    States appState = States.INACTIVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addItemsToSpinners();
        setResultView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //events
    public void onSessionButtonClick(View view) {
        if (this.appState == States.ACTIVE) {
            this.appState = States.INACTIVE;
        } else {
            this.appState = States.ACTIVE;
        }
        setResultView();
        setSessionButtonText();
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
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.languages, R.layout.spinner_item_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner = (Spinner) findViewById(R.id.destinationLanguages_spinner);
        spinner.setAdapter(adapter);
    }

    private void setResultView() {
        ViewGroup resultView = (ViewGroup) findViewById(R.id.resultView);
        resultView.removeAllViews();
        View content;
        if (this.appState == States.ACTIVE) {
            content = LayoutInflater.from(this).inflate(R.layout.resultview_active, resultView, false);
        } else {
            content = LayoutInflater.from(this).inflate(R.layout.resultview_inactive, resultView, false);
        }
        resultView.addView(content);
    }

    private void setSessionButtonText() {
        if (this.appState == States.ACTIVE) {
            Button button = (Button) findViewById(R.id.sessionButton);
            button.setText(R.string.button_session_active);
        } else {
            Button button = (Button) findViewById(R.id.sessionButton);
            button.setText(R.string.button_session_inactive);
        }
    }

}
