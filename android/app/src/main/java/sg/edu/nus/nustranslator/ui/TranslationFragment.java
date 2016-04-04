package sg.edu.nus.nustranslator.ui;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;
import java.util.Vector;

import sg.edu.nus.nustranslator.R;
import sg.edu.nus.nustranslator.models.AppModel;
import sg.edu.nus.nustranslator.recognizers.ISpeechRecognizer;
import sg.edu.nus.nustranslator.recognizers.LocalSpeechRecognizer;


public class TranslationFragment extends Fragment implements TextToSpeech.OnUtteranceCompletedListener{
    private static final String ORIGINAL_LANGUAGE = "TranslationFragment.OriginalLanguage";
    private static final String TRANSLATION_LANGUAGE = "TranslationFragment.TranslationLanguage";

    private AppModel mAppModel;
    private Vector<String> mEnglishWordsWithTranslations;
    private String mBestResult = "";
    private String mSimilarResultText = "";
    private String mTranslatedResult = "";
    private String mOriginalLanguage;
    private String mTranslationLanguage;
    private MediaPlayer mMediaPlayer;

    public ISpeechRecognizer mSpeechRecognizer;
    private String lastRecognitionUpdate;
    private TextToSpeech mTextToSpeech;

    private View mLoadingView;
    private TextView mTopResult;
    private TextView mSimilarResults;
    private TextView mTranslationTextView;


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
        mMediaPlayer = new MediaPlayer();
        mAppModel = AppModel.getInstance();
        mEnglishWordsWithTranslations = mAppModel.getSentencesByLanguageName("English");

        mSpeechRecognizer = new LocalSpeechRecognizer(this);
        mSpeechRecognizer.setInputLanguage(mOriginalLanguage, getActivity().getApplicationContext());
        this.mTextToSpeech = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(Locale.US);
                }
            }
        });
        this.mTextToSpeech.setOnUtteranceCompletedListener(this);
        mSpeechRecognizer.initListen();
        mSpeechRecognizer.startListen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_translation, container, false);
        Button translationPlaybackButton = (Button)v.findViewById(R.id.translationPlaybackButton);
        Button originalPlaybackButton = (Button) v.findViewById(R.id.originalLanguagePlaybackButton);
        mTopResult = (TextView) v.findViewById(R.id.firstResult);
        mSimilarResults = (TextView) v.findViewById(R.id.otherResults);
        mTranslationTextView = (TextView) v.findViewById(R.id.resultText);
        mSimilarResults.setText("\n" + "speak 'translation start' to trigger app\n" + "\n");

        translationPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMp3(mTranslationLanguage);
            }
        });
        originalPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMp3(mOriginalLanguage);
            }
        });

        return v;
    }


    @Override
    public void onUtteranceCompleted(String utteranceId) {
        if (utteranceId.equals(mTranslatedResult)|| mTranslationLanguage.toLowerCase().equals("mandarin")) {
            resetSpeechRecognizer();
        }
    }

    public void onSpeechRecognitionResultUpdate(String input, String state) {
        if (this.lastRecognitionUpdate != null && this.lastRecognitionUpdate.equals(input)) {
            return;
        }

        Log.d(this.getClass().getSimpleName(), input);

        this.lastRecognitionUpdate = input;

        //get results
        String result = isMatch(input);
        Vector<String> topResults = new Vector<>();
        topResults.add(result);
        topResults.add(input);
        if (result.equals("")) {
            if(input.split(" ").length > 14){
                resetSpeechRecognizer();
            }
            mTranslatedResult = "";
        }else{
            mTranslatedResult = getTranslation(topResults);
        }

        updateSpeechRecognitionResult(topResults, mTranslatedResult, state);
    }

    private void updateSpeechRecognitionResult(Vector<String> results, String translatedResultTemp,String State) {
        String Mode="\nspeak 'translation start' to trigger app\n\n";
        if(State.equals("search")){
            Mode = "\nSpeak words from word list that you want to translate\n\n";
        }
        if (results.get(0).equals("")) {
            mTopResult.setText(mBestResult);
            mSimilarResults.setText(Mode+"Words detected: "+results.get(1));
        }

        else {
            mBestResult = results.get(0);

            playMp3(mTranslationLanguage);

            mTopResult.setText(mBestResult);
            mSimilarResults.setText(Mode+"Words detected: "+mBestResult+results.get(1));
            mTranslationTextView.setText(translatedResultTemp);
        }

    }

    private void resetSpeechRecognizer() {
        mSpeechRecognizer.stopListen();
        mSpeechRecognizer.startListen();
    }

    private void playMp3(String translateTo)  {
        mSpeechRecognizer.stopListen();
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
        if (!mBestResult.equals("") && translateTo.toLowerCase().equals("mandarin")){
            if(mEnglishWordsWithTranslations.contains(mBestResult)){

                String musicName = "m"+ mBestResult.toLowerCase().replaceAll(" ", "")+".mp3";

                mMediaPlayer = new MediaPlayer();
                try {

                    AssetFileDescriptor descriptor = getActivity().getAssets().openFd(musicName);
                    mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();

                    mMediaPlayer.prepare();
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            resetSpeechRecognizer();
                        }
                    });
//                    mMediaPlayer.prepareAsync();
                    mMediaPlayer.setVolume(10f, 10f);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String isMatch(String sentence) {

        Log.d(this.getClass().getSimpleName(), sentence);
        sentence = sentence.toLowerCase();
        Vector<String> sentences = mAppModel.getSentencesByLanguageName(mOriginalLanguage);
        if (sentence.toLowerCase().contains("translation start")){
            return "Translation Start";
        }else if (sentence.toLowerCase().contains("translation end")){
            return "Translation End";
        }
        for(int i =0;i<sentences.size();i++) {
            if (sentence.toLowerCase().contains(sentences.elementAt(i).toLowerCase())) {
                return sentences.elementAt(i);
            }
        }
        if(sentence.toLowerCase().contains("biting")){
            return "Biting surface";
        }else if (sentence.toLowerCase().contains("great")){
            return "Bridge";
        }else if (sentence.toLowerCase().contains("implants")){
            return "Dental implants";
        }else if (sentence.toLowerCase().contains("outer")){
            return "Outer surface";
        }else if (sentence.toLowerCase().contains("inner")){
            return "Inner surface";
        }else if (sentence.toLowerCase().contains("mandible")){
            return "Protrude mandible";
        }else if (sentence.toLowerCase().contains("canal")||sentence.toLowerCase().contains("rail")){
            return "Root Canal";
        }else if (sentence.toLowerCase().contains("wife them")){
            return "Wisdom Tooth";
        }else if (sentence.toLowerCase().contains("life them")){
            return "Wisdom Tooth";
        }else if (sentence.toLowerCase().contains("life gum")){
            return "Wisdom Tooth";
        }else if (sentence.toLowerCase().contains("had eight horse")){
            return "Halitosis";
        }else if (sentence.toLowerCase().contains("team fat men")){
            return "Inflammation";
        }else if (sentence.toLowerCase().contains("team men")){
            return "Inflammation";
        }else if (sentence.toLowerCase().contains("enough")&&(sentence.toLowerCase().contains("suffix")
                ||sentence.toLowerCase().contains("sentence"))){
            return "Inner surface";
        }else if (sentence.toLowerCase().contains("OUT HAS")){
            return "Outer surface";
        }else if (sentence.toLowerCase().contains("canal") && sentence.toLowerCase().contains(("treatment"))) {
            return "Root Canal Treatment";
        }else if (sentence.toLowerCase().contains("back area")){
            return "Bacteria";
        }else if (sentence.toLowerCase().contains("back carry")){
            return "Bacteria";
        }else if (sentence.toLowerCase().contains("bat hear")){
            return "Bacteria";
        }else if (sentence.toLowerCase().contains("feed eat")){
            return "Filling";
        }else if (sentence.toLowerCase().contains("feed mean")){
            return "Filling";
        }else if (sentence.toLowerCase().contains("fear")){
            return "Filling";
        }else if (sentence.toLowerCase().contains("noun")&&(sentence.toLowerCase().contains("base"))){
            return "Filling";
        }else if (sentence.toLowerCase().contains("fear")){
            return "Gum Disease";
        }else if (sentence.toLowerCase().contains("high")&&sentence.toLowerCase().contains("cause")){
            return "Halitosis";
        }else if (sentence.toLowerCase().contains("decay")){
            return "Tooth Decay";
        }else if (sentence.toLowerCase().contains("how")){
            return "Pulp";
        }else if(sentence.toLowerCase().contains("pound")&&sentence.toLowerCase().contains("these")){
            return "Gum Disease";
        }else if (sentence.toLowerCase().contains("tall")&&sentence.toLowerCase().contains("sense")){
            return "Halitosis";
        }else if (sentence.toLowerCase().contains("suffix")&&sentence.toLowerCase().contains("eye")){
            return "Biting surface";
        }else if (sentence.toLowerCase().contains("sat")&& sentence.toLowerCase().contains("eye")){
            return "Biting surface";
        }else if (sentence.toLowerCase().contains("sat") && sentence.toLowerCase().contains("eye")){
            return "Biting surface";
        }else if (sentence.toLowerCase().contains("sat")&&sentence.toLowerCase().contains("eye")){
            return "Biting surface";
        }
        return "";
    }

    private String getTranslation(Vector<String> inputs) {
        if (inputs == null || inputs.size() == 0) {
            return "";
        }
        return mAppModel.getTranslation(inputs.firstElement(), mOriginalLanguage, mTranslationLanguage);
    }



}
