package sg.edu.nus.nustranslator.recognizers;

import android.content.Context;

import java.io.File;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import sg.edu.nus.nustranslator.controllers.MainController;
import sg.edu.nus.nustranslator.ultis.Configurations;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

/**
 * Created by Storm on 3/10/2015.
 */
public class LocalSpeechRecognizer implements ISpeechRecognizer, RecognitionListener {

    private SpeechRecognizer recognizer;
    private Context context;
    private MainController parent;

    public LocalSpeechRecognizer(final Context context, MainController parent) {
        this.context = context;
        this.parent = parent;
    }

    @Override
    public void setInputLanguage(String language, Context context) {
        setupRecognizer(context, language);
    }

    @Override
    public void startListen() {
        this.recognizer.startListening(Configurations.Sphinx_keyword_search);
    }

    @Override
    public void stopListen() {
        this.recognizer.stop();
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            this.parent.onSpeechRecognitionResultUpdate(text);
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
    }

    //Private Helper Methods
    private void setupRecognizer(Context context, String language) {
        try {
            language = language.toLowerCase();

            Assets assets = new Assets(context);
            File assetDir = assets.syncAssets();
            File modelsDir = new File(assetDir, Configurations.Sphinx_models_dir);
            File internalPath = context.getFilesDir();
            this.recognizer = defaultSetup()
                    .setAcousticModel(new File(modelsDir, Configurations.Sphinx_acousticModel_dir + language))
                    .setDictionary(new File(internalPath, language + Configurations.Data_fileName_dict_ext))
                    .setBoolean("-remove_noise", true)
                    .setKeywordThreshold(Configurations.Sphinx_keywordThreshold)
                    .getRecognizer();
            this.recognizer.addListener(this);

            // Create language model search.
            File languageModel = new File(internalPath, language + Configurations.Data_fileName_languageModel_ext);
            recognizer.addNgramSearch(Configurations.Sphinx_keyword_search, languageModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
