package sg.edu.nus.nustranslator.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import sg.edu.nus.nustranslator.R;

public class PhrasesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrases);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.help_container);

        if(fragment == null) {
            fragment = new PhrasesFragment();
            fm.beginTransaction()
                    .add(R.id.help_container, fragment)
                    .commit();
        }
    }

}
