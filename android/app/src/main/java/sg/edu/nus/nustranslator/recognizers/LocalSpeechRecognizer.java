package sg.edu.nus.nustranslator.recognizers;

import android.content.Context;

import java.io.File;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import sg.edu.nus.nustranslator.controllers.MainController;
import sg.edu.nus.nustranslator.utils.Configurations;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

/**
 * Created by Storm on 3/10/2015.
 */
public class LocalSpeechRecognizer implements ISpeechRecognizer, RecognitionListener {

    private SpeechRecognizer recognizer;
    private Context context;
    private MainController parent;
    private static long preTime;

    private static final String KEYPHRASE = "Translation Start";
    private static final String KEYPHRASEEND = "Translation End";
    public static String CurrentState = Configurations.Sphinx_keyword_trigger_start;

    public LocalSpeechRecognizer(final Context context, MainController parent) {
        this.context = context;
        this.parent = parent;
        preTime = System.currentTimeMillis();
    }

    @Override
    public void setInputLanguage(String language, Context context) {
        setupRecognizer(context, language);
    }

    public void initListen(){
        CurrentState = Configurations.Sphinx_keyword_trigger_start;
        this.parent.onSpeechRecognitionResultUpdate("",CurrentState);
    }

    @Override
    public void startListen() {
        this.recognizer.startListening(CurrentState);
    }

    @Override
    public void stopListen() {
        this.recognizer.stop();
    }
    @Override
    public void cancelListen() {
        this.recognizer.cancel();
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
            String text =  hypothesis.getHypstr();
            if (CurrentState.equals(Configurations.Sphinx_keyword_trigger_start) && text.toLowerCase().equals(KEYPHRASE.toLowerCase())) {
                switchSearch(Configurations.Sphinx_keyword_search);
//                CurrentState = Configurations.Sphinx_keyword_search;
                text = KEYPHRASE;
            }
            else if (!CurrentState.equals(Configurations.Sphinx_keyword_trigger_start) && text.toLowerCase().contains(KEYPHRASEEND.toLowerCase())) {
                switchSearch(Configurations.Sphinx_keyword_trigger_start);
//                CurrentState = Configurations.Sphinx_keyword_trigger_start;
                text = KEYPHRASEEND;
            }
            this.parent.onSpeechRecognitionResultUpdate(text,CurrentState);
        }
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        CurrentState =  searchName;
        recognizer.startListening(searchName);
    }
//    if ((System.currentTimeMillis()-preTime) >500) {
//        preTime = System.currentTimeMillis();
//        if (hypothesis != null) {
//            String text = hypothesis.getHypstr();
//            this.parent.onSpeechRecognitionResultUpdate(text);
//
//        }
//    }
    @Override
    public void onResult(Hypothesis hypothesis) {
//        if (hypothesis != null) {
//            String text = hypothesis.getHypstr();
//            this.parent.onSpeechRecognitionResultUpdate(text);
//        }
    }


    //Private Helper Methods
    private void setupRecognizer(Context context, String language) {
        try {
            language = language.toLowerCase();

            Assets assets = new Assets(context);
            File assetDir = assets.syncAssets();
            File modelsDir = new File(assetDir, Configurations.Sphinx_models_dir);
            //File internalPath = context.getFilesDir();
            File internalDir =  new File(assetDir,"lb_with_200");
            //File dictionaryFile = context.getResources().getAssets().open( language + Configurations.Data_fileName_dict_ext);

            this.recognizer = defaultSetup()
                    .setAcousticModel(new File(modelsDir, Configurations.Sphinx_acousticModel_dir + language))

                    .setDictionary(new File(internalDir, language + Configurations.Data_fileName_dict_ext))
//                    .setDictionary(new File(assetDir, "cmudict-en-us.dict"))
                    .setBoolean("-remove_noise", true)
                    .setKeywordThreshold(Configurations.Sphinx_keywordThreshold)
                    .getRecognizer();
            this.recognizer.addListener(this);


            File languageModel1 = new File(internalDir, "triggerCommand.lm");

//            recognizer.addKeyphraseSearch(Configurations.Sphinx_keyword_trigger_start, KEYPHRASE);
            recognizer.addNgramSearch(Configurations.Sphinx_keyword_trigger_start, languageModel1);
            // Create language model search.
            File languageModel = new File(internalDir, language + Configurations.Data_fileName_languageModel_ext);
            recognizer.addNgramSearch(Configurations.Sphinx_keyword_search, languageModel);
            this.parent.onSpeechRecognitionResultUpdate("",CurrentState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
