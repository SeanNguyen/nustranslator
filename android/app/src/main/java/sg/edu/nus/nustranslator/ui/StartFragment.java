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
import sg.edu.nus.nustranslator.AppModel;

public class StartFragment extends Fragment {

    private Spinner mOriginalLanguageSpinner;
    private Spinner mTranslationLanguageSpinner;
    private AppModel mModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        mModel = AppModel.getInstance(getActivity().getApplicationContext());

        final Vector<String> allLanguages = mModel.getAllLanguages();
        final Vector<String> mainLangauges = mModel.getMainLanguages();

        if(allLanguages.size() == 0) {
            Log.e(this.getClass().getSimpleName(), "no languages loaded yet!");
        }

        final String firstRemovedLanguage = mainLangauges.firstElement();
        allLanguages.remove(firstRemovedLanguage);


        final ArrayAdapter<String> originalLanguageAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_layout, mainLangauges) {
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        TextView v = (TextView) super.getDropDownView(position, convertView, parent);
                        v.setGravity(Gravity.CENTER);
                        return v;
                    }
                };
        final ArrayAdapter<String> translationLanguageAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_layout, allLanguages) {
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getDropDownView(position, convertView, parent);
                v.setGravity(Gravity.CENTER);
                return v;
            }
        };


        translationLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        originalLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mOriginalLanguageSpinner = (Spinner) v.findViewById(R.id.originalLanguages_spinner);
        mTranslationLanguageSpinner = (Spinner) v.findViewById(R.id.destination_languages_spinner);
        mOriginalLanguageSpinner.setAdapter(originalLanguageAdapter);
        mTranslationLanguageSpinner.setAdapter(translationLanguageAdapter);
        mOriginalLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private String removedLanguage = firstRemovedLanguage;
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                allLanguages.add(removedLanguage);
                removedLanguage = originalLanguageAdapter.getItem(position);
                allLanguages.remove(removedLanguage);
                mTranslationLanguageSpinner.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        Button startTranslationButton = (Button) v.findViewById(R.id.stop_translation_button);
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
