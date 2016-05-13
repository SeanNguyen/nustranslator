package sg.edu.nus.nustranslator.ui;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import sg.edu.nus.nustranslator.R;
import sg.edu.nus.nustranslator.AppModel;
import sg.edu.nus.nustranslator.recognizers.IRecognitionUpdateListener;
import sg.edu.nus.nustranslator.recognizers.ISpeechRecognizer;
import sg.edu.nus.nustranslator.recognizers.LocalSpeechRecognizer;
import sg.edu.nus.nustranslator.Configurations;


public class TranslationFragment extends Fragment implements IRecognitionUpdateListener {
    private static final String ORIGINAL_LANGUAGE = "TranslationFragment.OriginalLanguage";
    private static final String TRANSLATION_LANGUAGE = "TranslationFragment.TranslationLanguage";

    private AppModel mAppModel;
    private String mOriginalLanguage;
    private String mTranslationLanguage;
    private MediaPlayer mMediaPlayer;
    private boolean mNowPlaying = false;
    private boolean mInitComplete = false;

    public ISpeechRecognizer mSpeechRecognizer;

    private View mLoadingView;
    private TextView mBestGuessView;
    private TextView mDetectedWordsView;
    private TextView mTranslatedTextView;
    private TextView mTranslationPrompt;

    private String mBestGuess;
    private String mLastRecognitionResult;

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

        mBestGuess = "";
        mLastRecognitionResult = "";
        if (getArguments() != null) {
            mOriginalLanguage = getArguments().getString(ORIGINAL_LANGUAGE);
            mTranslationLanguage = getArguments().getString(TRANSLATION_LANGUAGE);
        }
        mAppModel = AppModel.getInstance(getContext().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_translation, container, false);
        mLoadingView = v.findViewById(R.id.loading_view);
        mTranslationPrompt = (TextView) v.findViewById(R.id.translation_prompt);
        mBestGuessView = (TextView) v.findViewById(R.id.best_guess);
        mDetectedWordsView = (TextView) v.findViewById(R.id.detected_words);
        mTranslatedTextView = (TextView) v.findViewById(R.id.translation);
        Button stopButton = (Button) v.findViewById(R.id.stop_translation_button);
        final Button clearButton = (Button) v.findViewById(R.id.clear_button);
        Button translationPlaybackButton = (Button)v.findViewById(R.id.translationPlaybackButton);
        Button originalPlaybackButton = (Button) v.findViewById(R.id.originalLanguagePlaybackButton);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpeechRecognizer.stopListen();
                getFragmentManager().popBackStack();
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearButton.setEnabled(false);
                mSpeechRecognizer.reset();
                resetTranslationDisplay();
                clearButton.setEnabled(true);
            }
        });

        originalPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        translationPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMp3(mBestGuess);
            }
        });

        resetTranslationDisplay();
        initializeRecognizer();

        return v;
    }

    private void initializeRecognizer() {
        // recognizer is initialized in separate thread to allow loading of resources
        // while still allowing app navigation
        new InitRecognizerAsyncTask().execute(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mInitComplete) {
            mSpeechRecognizer.stopListen();
        }

        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mNowPlaying = false;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mInitComplete) {
            mSpeechRecognizer.startListen();
        }
    }

    public void onRecognitionResult(String wordsDetected, String state) {
        if (mLastRecognitionResult.equals(wordsDetected)) {
            return;
        }

        mLastRecognitionResult = wordsDetected;

        //find the best match from the list of words we know if one exists
        String bestGuess = mAppModel.matchAgainstKnownWords(wordsDetected, mOriginalLanguage);

        String translatedGuess = mAppModel.getTranslation(bestGuess, mOriginalLanguage, mTranslationLanguage);
        displayRecognitionResult(bestGuess, translatedGuess, wordsDetected, state);

        if(wordsDetected.split(" ").length >= 6){
            mSpeechRecognizer.reset();
            resetTranslationDisplay();
        }
    }

    private void resetTranslationDisplay() {
        mBestGuess = "";
        mBestGuessView.setText(String.format(getString(R.string.best_guess_prefix), ""));
        mDetectedWordsView.setText(String.format(getString(R.string.detected_words_prefix),""));
        mTranslatedTextView.setText("");
    }

    private void displayRecognitionResult(String bestGuess, String translatedGuess, String wordsDetected, String state) {
        if(state.equals(Configurations.SPHINX_ACTIVATED)) {
            mTranslationPrompt.setText(R.string.translator_activated_prompt);
        } else {
            mTranslationPrompt.setText(R.string.translator_not_activated_prompt);
        }

        if(!bestGuess.equals("")) {
            mBestGuess = bestGuess;
            playMp3(mBestGuess);
            mTranslatedTextView.setText(translatedGuess);
        }

        mBestGuessView.setText(String.format(getString(R.string.best_guess_prefix), mBestGuess));
        mDetectedWordsView.setText(String.format(getString(R.string.detected_words_prefix),
                                    wordsDetected));
    }

    private void playMp3(String word) {
        if(word != null && !word.equals("") && !mNowPlaying) {
            mNowPlaying = true;
            mSpeechRecognizer.stopListen();
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
                        mSpeechRecognizer.startListen();
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

    private class InitRecognizerAsyncTask extends AsyncTask<TranslationFragment, Void, Void> {
        @Override
        protected Void doInBackground(TranslationFragment... params) {
            TranslationFragment fragment = params[0];
            mSpeechRecognizer = new LocalSpeechRecognizer(fragment);
            mSpeechRecognizer.setInputLanguage(mOriginalLanguage, getActivity().getApplicationContext());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mInitComplete = true;
            mLoadingView.setVisibility(View.GONE);
            mSpeechRecognizer.startListen();
        }
    }
}
