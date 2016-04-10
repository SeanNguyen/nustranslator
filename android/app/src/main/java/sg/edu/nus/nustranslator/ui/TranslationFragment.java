package sg.edu.nus.nustranslator.ui;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import sg.edu.nus.nustranslator.R;
import sg.edu.nus.nustranslator.AppModel;
import sg.edu.nus.nustranslator.recognizers.ISpeechRecognizer;
import sg.edu.nus.nustranslator.recognizers.LocalSpeechRecognizer;
import sg.edu.nus.nustranslator.Configurations;


public class TranslationFragment extends Fragment implements TextToSpeech.OnUtteranceCompletedListener{
    private static final String ORIGINAL_LANGUAGE = "TranslationFragment.OriginalLanguage";
    private static final String TRANSLATION_LANGUAGE = "TranslationFragment.TranslationLanguage";

    private AppModel mAppModel;
    private String mBestResult = "";
    private String mTranslatedResult = "";
    private String mOriginalLanguage;
    private String mTranslationLanguage;
    private MediaPlayer mMediaPlayer;

    public ISpeechRecognizer mSpeechRecognizer;
    private String lastRecognitionResult;
    private TextToSpeech mTextToSpeech;

    private View mLoadingView;
    private TextView mTopResultView;
    private TextView mSimilarResultsView;
    private TextView mTranslationTextView;
    private TextView mTranslationPrompt;


    public static TranslationFragment newInstance(String param1, String param2) {
        TranslationFragment fragment = new TranslationFragment();
        Bundle args = new Bundle();
        args.putString(ORIGINAL_LANGUAGE, param1);
        args.putString(TRANSLATION_LANGUAGE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOriginalLanguage = getArguments().getString(ORIGINAL_LANGUAGE);
            mTranslationLanguage = getArguments().getString(TRANSLATION_LANGUAGE);
        }
        mAppModel = AppModel.getInstance(getContext().getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_translation, container, false);
        mLoadingView = v.findViewById(R.id.loading_view);
        Button translationPlaybackButton = (Button)v.findViewById(R.id.translationPlaybackButton);
        Button originalPlaybackButton = (Button) v.findViewById(R.id.originalLanguagePlaybackButton);
        mTranslationPrompt = (TextView) v.findViewById(R.id.translation_prompt);
        mTopResultView = (TextView) v.findViewById(R.id.first_result);
        mSimilarResultsView = (TextView) v.findViewById(R.id.other_results);
        mTranslationTextView = (TextView) v.findViewById(R.id.translation);
        Button stopButton = (Button) v.findViewById(R.id.translation_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpeechRecognizer.stopListen();
                getFragmentManager().popBackStack();
            }
        });

        translationPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMp3(mTranslationLanguage, mBestResult);
            }
        });
        originalPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMp3(mOriginalLanguage, mBestResult);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void onStart() {
        super.onStart();
        new LoaderAsyncTask().execute(this);
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        if (utteranceId.equals(mTranslatedResult)|| mTranslationLanguage.toLowerCase().equals("mandarin")) {
            resetSpeechRecognizer();
        }
    }

    public void onSpeechRecognitionResultUpdate(String recognitionResult, String state) {
        if (this.lastRecognitionResult != null && this.lastRecognitionResult.equals(recognitionResult)) {
            return;
        }

        lastRecognitionResult = recognitionResult;

        //find the best match from the list of words we know (if one exists)
        String bestResult = matchWithKnownWords(recognitionResult);
        ArrayList<String> displayResults = new ArrayList<>();
        displayResults.add(bestResult);
        displayResults.add(recognitionResult);

        mTranslatedResult = getTranslation(bestResult);
        updateSpeechRecognitionResult(displayResults, mTranslatedResult, state);

        if(recognitionResult.split(" ").length >= 5){
            resetSpeechRecognizer();
        }
    }

    private void updateSpeechRecognitionResult(ArrayList<String> results, String translatedResultTemp, String state) {
        // TODO: remove hardcoded strings

        if(state.equals(Configurations.SPHINX_ACTIVATED)){
            mTranslationPrompt.setText(R.string.translator_activated_prompt);
        } else {
            mTranslationPrompt.setText(R.string.translator_not_activated_prompt);
        }

        if (results.get(0).equals("")) {
            mTopResultView.setText(mBestResult);
            mSimilarResultsView.setText("Words detected: " + results.get(1));
        } else {
            mBestResult = results.get(0);

            playMp3(mTranslationLanguage, mBestResult);

            mTopResultView.setText(mBestResult);
            mSimilarResultsView.setText("Words detected: " + mBestResult+ " " + results.get(1));
            mTranslationTextView.setText(translatedResultTemp);
        }
    }

    private void resetSpeechRecognizer() {
        mSpeechRecognizer.stopListen();
        mSpeechRecognizer.startListen();
    }

    private void playMp3(String translateTo, String text) {
        if(text != null && !text.equals("") && translateTo.toLowerCase().equals("mandarin")
                && mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListen();

            if(mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }

            try {
                String musicName = "m" + text.toLowerCase().replaceAll(" ", "") + ".mp3";
                AssetFileDescriptor descriptor = getActivity().getAssets().openFd(musicName);
                mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                mMediaPlayer.prepare();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        resetSpeechRecognizer();
                        mMediaPlayer.reset();
                    }
                });
                mMediaPlayer.setVolume(10f, 10f);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.start();
                descriptor.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mSpeechRecognizer.startListen();
            }
        }
    }

    private String matchWithKnownWords(String sentence) {
        sentence = sentence.toLowerCase();
        Vector<String> knownSentences = mAppModel.getSentencesByLanguageName(mOriginalLanguage);
        if (sentence.contains(LocalSpeechRecognizer.ACTIVATE_PHRASE.toLowerCase())){
            return LocalSpeechRecognizer.ACTIVATE_PHRASE;
        }else if (sentence.contains(LocalSpeechRecognizer.DEACTIVATE_PHRASE.toLowerCase())){
            return LocalSpeechRecognizer.DEACTIVATE_PHRASE.toLowerCase();
        }

        for(int i = 0; i < knownSentences.size(); i++) {
            if (sentence.contains(knownSentences.elementAt(i).toLowerCase())) {
                return knownSentences.elementAt(i);
            }
        }

        if(sentence.contains("biting")){
            return "Biting surface";
        }else if (sentence.contains("great")){
            return "Bridge";
        }else if (sentence.contains("implants")){
            return "Dental implants";
        }else if (sentence.contains("outer")){
            return "Outer surface";
        }else if (sentence.contains("inner")){
            return "Inner surface";
        }else if (sentence.contains("mandible")){
            return "Protrude mandible";
        }else if (sentence.contains("canal") || sentence.contains("rail")){
            return "Root Canal";
        }else if (sentence.contains("wife them")){
            return "Wisdom Tooth";
        }else if (sentence.contains("life them")){
            return "Wisdom Tooth";
        }else if (sentence.contains("life gum")){
            return "Wisdom Tooth";
        }else if (sentence.contains("had eight horse")){
            return "Halitosis";
        }else if (sentence.contains("team fat men")){
            return "Inflammation";
        }else if (sentence.contains("team men")){
            return "Inflammation";
        }else if ( (sentence.contains("enough") && (sentence.contains("suffix"))
                    || sentence.contains("sentence")) ){
            return "Inner surface";
        }else if (sentence.contains("out has")){
            return "Outer surface";
        }else if (sentence.contains("canal") && sentence.contains(("treatment"))) {
            return "Root Canal Treatment";
        }else if (sentence.contains("back area")){
            return "Bacteria";
        }else if (sentence.contains("back carry")){
            return "Bacteria";
        }else if (sentence.contains("bat hear")){
            return "Bacteria";
        }else if (sentence.contains("feed eat")){
            return "Filling";
        }else if (sentence.contains("feed mean")){
            return "Filling";
        }else if (sentence.contains("fear")){
            return "Filling";
        }else if (sentence.contains("noun") && (sentence.contains("base"))){
            return "Filling";
        }else if (sentence.contains("fear")){
            return "Gum Disease";
        }else if (sentence.contains("high") && sentence.contains("cause")){
            return "Halitosis";
        }else if (sentence.contains("decay")){
            return "Tooth Decay";
        }else if (sentence.contains("how")){
            return "Pulp";
        }else if(sentence.contains("pound") && sentence.contains("these")){
            return "Gum Disease";
        }else if (sentence.contains("tall") && sentence.contains("sense")){
            return "Halitosis";
        }else if (sentence.contains("suffix") && sentence.contains("eye")){
            return "Biting surface";
        }else if (sentence.contains("sat")&& sentence.contains("eye")){
            return "Biting surface";
        }else if (sentence.contains("sat") && sentence.contains("eye")){
            return "Biting surface";
        }else if (sentence.contains("sat") && sentence.contains("eye")){
            return "Biting surface";
        } else {
            return "";
        }
    }

    private String getTranslation(String input) {
        if (input.equals("")) {
            return "";
        }
        return mAppModel.getTranslation(input, mOriginalLanguage, mTranslationLanguage);
    }


    private class LoaderAsyncTask extends AsyncTask<TranslationFragment, Void, Void> {
        @Override
        protected Void doInBackground(TranslationFragment... params) {
            TranslationFragment fragment = params[0];
            mSpeechRecognizer = new LocalSpeechRecognizer(fragment);
            mSpeechRecognizer.setInputLanguage(mOriginalLanguage, getActivity().getApplicationContext());
            fragment.mTextToSpeech = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        mTextToSpeech.setLanguage(Locale.US);
                    }
                }
            });
            fragment.mTextToSpeech.setOnUtteranceCompletedListener(fragment);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mLoadingView.setVisibility(View.GONE);
            mSpeechRecognizer.initListen();
            mSpeechRecognizer.startListen();
        }
    }


}
