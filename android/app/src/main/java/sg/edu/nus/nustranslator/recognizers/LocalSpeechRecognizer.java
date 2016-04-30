package sg.edu.nus.nustranslator.recognizers;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import sg.edu.nus.nustranslator.Configurations;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;


public class LocalSpeechRecognizer implements ISpeechRecognizer, RecognitionListener {
    public static final String ACTIVATE_PHRASE = "Translation Start";
    public static final String DEACTIVATE_PHRASE = "Translation End";

    private SpeechRecognizer mRecognizer;
    private IRecognitionUpdateListener mListener;
    private String mState;

    public LocalSpeechRecognizer(IRecognitionUpdateListener listener) {
        mListener = listener;
        mState = Configurations.SPHINX_NOT_ACTIVATED;
    }

    @Override
    public void setInputLanguage(String language, Context context) {
        setupRecognizer(context, language);
    }

    @Override
    public void startListen() {
        mRecognizer.startListening(mState);
    }

    @Override
    public void stopListen() {
        // use mRecognizer cancel rather than stop, it responds faster but does not do a "onResult"
        mRecognizer.cancel();
    }
    @Override
    public void cancelListen() {
        mRecognizer.cancel();
    }

    @Override
    public void reset() {
        stopListen();
        startListen();
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    /**
     * Executed whenever something is heard
     * @param hypothesis
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text =  hypothesis.getHypstr().toLowerCase();

            //if not yet activatereceivedeved activate command
            if (mState.equals(Configurations.SPHINX_NOT_ACTIVATED) && text.contains(ACTIVATE_PHRASE.toLowerCase())) {
                changeState(Configurations.SPHINX_ACTIVATED);
                text = ACTIVATE_PHRASE;
            }
            else if (mState.equals(Configurations.SPHINX_ACTIVATED) && text.contains(DEACTIVATE_PHRASE.toLowerCase())) {
                changeState(Configurations.SPHINX_NOT_ACTIVATED);
                text = DEACTIVATE_PHRASE;
            }
            mListener.onRecognitionResult(text, mState);
        }
    }

    /**
     * Exectued only when recognizer.stop is called
     * @param hypothesis
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
    }

    private void changeState(String stateName) {
        mRecognizer.stop();
        mState =  stateName;
        mRecognizer.startListening(stateName);
    }

    private void setupRecognizer(Context context, String language) {
        try {
            language = language.toLowerCase();

            Assets assets = new Assets(context);
            File assetDir = assets.syncAssets();
            File modelsDir = new File(assetDir, Configurations.Sphinx_models_dir);
            File internalDir =  new File(assetDir,"lb_with_200");
            //File dictionaryFile = context.getResources().getAssets().open( language + Configurations.Data_fileName_dict_ext);

            mRecognizer = defaultSetup()
                    .setAcousticModel(new File(modelsDir, Configurations.Sphinx_acousticModel_dir + language))
                    .setDictionary(new File(internalDir, language + Configurations.Data_fileName_dict_ext))
                    .setBoolean("-remove_noise", true)
                    .setKeywordThreshold(Configurations.Sphinx_keywordThreshold)
                    .getRecognizer();
            mRecognizer.addListener(this);

            // a model used just for the trigger command to increase accuracy
            File triggerModel = new File(internalDir, "triggerCommand.lm");
            mRecognizer.addNgramSearch(Configurations.SPHINX_NOT_ACTIVATED, triggerModel);

            // model to use after trigger is called
            File languageModel = new File(internalDir, language + Configurations.Data_fileName_languageModel_ext);
            mRecognizer.addNgramSearch(Configurations.SPHINX_ACTIVATED, languageModel);

        } catch (IOException e) {
            Log.e(LocalSpeechRecognizer.class.getSimpleName(), e.getMessage());
        }
    }

}
