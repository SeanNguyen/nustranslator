package sg.edu.nus.nustranslator.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Vector;

import sg.edu.nus.nustranslator.AppModel;
import sg.edu.nus.nustranslator.R;


public class HelpFragment extends Fragment {
    private Spinner mLanguageSpinner;
    private ListView mSentenceList;
    private AppModel mAppModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);

        mLanguageSpinner = (Spinner) v.findViewById(R.id.help_languageSpinner);
        mLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                changeLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        mSentenceList = (ListView) v.findViewById(R.id.help_sentenceList);

        mAppModel = AppModel.getInstance(getContext());
        Vector<String> languages = mAppModel.getAllLanguages();
        updateLanguageList(languages);

        return v;
    }

    private void changeLanguage(int index) {
        Vector<String> sentences = mAppModel.getSentencesByLanguageIndex(index);
        updateSentenceList(sentences);
    }

    private void updateLanguageList(Vector<String> languages) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_layout, languages) {
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
        mLanguageSpinner.setAdapter(adapter);
    }

    private void updateSentenceList(Vector<String> sentences) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.sentence_item_layout, sentences) {
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }
        };
        this.mSentenceList.setAdapter(adapter);
    }



}
