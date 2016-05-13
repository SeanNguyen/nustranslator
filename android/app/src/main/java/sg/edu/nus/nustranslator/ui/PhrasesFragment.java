package sg.edu.nus.nustranslator.ui;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import sg.edu.nus.nustranslator.AppModel;
import sg.edu.nus.nustranslator.R;


public class PhrasesFragment extends Fragment {
    private Spinner mLanguageSpinner;
    private ListView mSentenceList;
    private AppModel mAppModel;
    private MediaPlayer mMediaPlayer;
    private boolean mNowPlaying;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_phrases, container, false);

        mLanguageSpinner = (Spinner) v.findViewById(R.id.phrase_languageSpinner);
        mLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                changeLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        mSentenceList = (ListView) v.findViewById(R.id.phrase_sentenceList);
        mSentenceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view;
                playMp3(tv.getText().toString());
            }
        });
        mAppModel = AppModel.getInstance(getContext());
        ArrayList<String> languages = mAppModel.getAllLanguages();
        updateLanguageList(languages);

        return v;
    }

    private void changeLanguage(int index) {
        ArrayList<String> sentences = mAppModel.getSentencesByLanguageIndex(index);
        updateSentenceList(sentences);
    }

    private void updateLanguageList(ArrayList<String> languages) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_layout, languages);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        mLanguageSpinner.setAdapter(adapter);
    }

    private void updateSentenceList(ArrayList<String> sentences) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.sentence_item_layout, sentences);
        this.mSentenceList.setAdapter(adapter);
    }

    private void playMp3(String word) {
        if(word != null && !word.equals("") && !mNowPlaying) {
            mNowPlaying = true;
            if(mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }

            try {
                String audioFileName = "m" + word.toLowerCase().replaceAll(" ", "") + ".mp3";
                Log.d(TranslationFragment.class.getSimpleName(), audioFileName);
                AssetFileDescriptor descriptor = getActivity().getAssets().openFd(audioFileName);
                Log.d(TranslationFragment.class.getSimpleName(), "Null? " + (descriptor == null));
                mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                mMediaPlayer.prepare();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mMediaPlayer.reset();
                        mNowPlaying = false;
                    }
                });
                mMediaPlayer.setVolume(10f, 10f);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.start();
                descriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
