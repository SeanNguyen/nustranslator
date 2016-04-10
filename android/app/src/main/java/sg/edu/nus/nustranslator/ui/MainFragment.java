package sg.edu.nus.nustranslator.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Vector;

import sg.edu.nus.nustranslator.R;
import sg.edu.nus.nustranslator.models.AppModel;

public class MainFragment extends Fragment {

    private Spinner mOriginalLanguageSpinner;
    private Spinner mTranslationLanguageSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        Vector<String> languages = AppModel.getInstance(getActivity().getApplicationContext()).getAllLanguages();
        if(languages.size() == 0) {
            Log.e(this.getClass().getSimpleName(), "no languages loaded yet!");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_layout, languages) {
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

        mOriginalLanguageSpinner = (Spinner) v.findViewById(R.id.originalLanguages_spinner);
        mOriginalLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //controller.setOriginalLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //controller.setOriginalLanguage(-1);
            }

        });
        mTranslationLanguageSpinner = (Spinner) v.findViewById(R.id.destination_languages_spinner);
        mTranslationLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //controller.setDestinationLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //controller.setDestinationLanguage(-1);
//                currentDestinationLanguage="english";

            }

        });
        mOriginalLanguageSpinner.setAdapter(adapter);
        mTranslationLanguageSpinner.setAdapter(adapter);


        Button startTranslationButton = (Button) v.findViewById(R.id.translation_button);
        startTranslationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String originalLanguage = (String) mOriginalLanguageSpinner.getSelectedItem();
                String translationLanguage = (String) mTranslationLanguageSpinner.getSelectedItem();
                Fragment translationFragment = TranslationFragment.newInstance(originalLanguage, translationLanguage);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer, translationFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }




}
