package sg.edu.nus.nustranslator.controllers;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import net.java.frej.fuzzy.Fuzzy;

import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import sg.edu.nus.nustranslator.activities.MainActivity;
import sg.edu.nus.nustranslator.data.DataController;
import sg.edu.nus.nustranslator.models.AppModel;
import sg.edu.nus.nustranslator.models.States;
import sg.edu.nus.nustranslator.recognizers.ISpeechRecognizer;
import sg.edu.nus.nustranslator.recognizers.LocalSpeechRecognizer;
import sg.edu.nus.nustranslator.ultis.Configurations;

/**
 * Created by Storm on 3/5/2015.
 */
public class MainController implements TextToSpeech.OnUtteranceCompletedListener {

    //attributes
    private AppModel appModel = AppModel.getInstance();
    private ISpeechRecognizer speechRecognizer;
    private DataController dataController = new DataController();
    private MainActivity mainActivity;

    private String lastRecognitionUpdate;
    private Timer resetTimer = new Timer();
    private TimerTask resetTimerTask;

    private TextToSpeech textToSpeech;
    private String translatedResult;

    //constructor
    public MainController(MainActivity context) {
        this.mainActivity = context;
        this.speechRecognizer = new LocalSpeechRecognizer(context, this);
        this.dataController.deserializeData(appModel, context);
        mainActivity.onFinishLoading();
        mainActivity.updateLanguageChoices(appModel.getAllLanguages());

        this.textToSpeech = new TextToSpeech(this.mainActivity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
        this.textToSpeech.setOnUtteranceCompletedListener(this);
    }

    //Public interface methods
    @Override
    public void onUtteranceCompleted(String utteranceId) {
        if (utteranceId.equals(this.translatedResult)) {
            this.resetSpeechRecognizer();
        }
    }

    //Public methods
    public States changeState() {
        States state = appModel.getAppState();
        if (state == States.ACTIVE) {
            appModel.setAppState(States.INACTIVE);
            this.speechRecognizer.stopListen();
        } else {
            appModel.setAppState(States.ACTIVE);
            speechRecognizer.startListen();
        }
        return appModel.getAppState();
    }

    public void onSpeechRecognitionResultUpdate(String input) {
        if (this.lastRecognitionUpdate != null && this.lastRecognitionUpdate.equals(input)) {
            return;
        }
        Log.e("Speech Partial Result", input);

        //reset timer
        resetTimer();
        this.lastRecognitionUpdate = input;

        //get results
        if (!isMatch(input)) {
            //return;
        }
        Vector<String> topResults = getTopResults(input);
        this.translatedResult = getTranslation(topResults);

        //update UI
        mainActivity.updateSpeechRecognitionResult(topResults, translatedResult);
    }

    public void updateData() {
        AsyncTask<Void, Void, Void> updateTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                dataController.updateData(appModel, mainActivity);
                mainActivity.updateLanguageChoices(appModel.getAllLanguages());
                return null;
            }

            @Override
            protected void onPostExecute(Void args) {
                mainActivity.onFinishLoading();
            }
        };
        updateTask.execute();
    }

    public void setOriginalLanguage(int index) {
        if (index < 0 || index >= this.appModel.getNumberOfLanguage()) {
            this.appModel.setOriginalLanguage(Configurations.Empty);
        } else {
            String language = this.appModel.getAllLanguages().get(index);
            this.appModel.setOriginalLanguage(language);
        }
    }

    public void setDestinationLanguage(int index) {
        if (index < 0 || index >= this.appModel.getNumberOfLanguage()) {
            this.appModel.setDestinationLanguage(Configurations.Empty);
        } else {
            String language = this.appModel.getAllLanguages().get(index);
            this.appModel.setDestinationLanguage(language);
        }
    }

    //Private Helper Methods
    private boolean isMatch(String sentence) {
        sentence = sentence.toLowerCase();
        String originalLanguage = appModel.getOriginalLanguage();
        Vector<String> sentences = appModel.getSentencesByLanguageName(originalLanguage);
        if (sentences.indexOf(sentence) > -1) {
            return true;
        }
        return false;
    }

    private Vector<String> getTopResults(String input) {
        String originalLanguage = appModel.getOriginalLanguage();
        Vector<String> sentences = appModel.getSentencesByLanguageName(originalLanguage);
        Vector<String> topResult = new Vector<String>();
        if (sentences == null) {
            return topResult;
        }
        for (int i = 0; i < sentences.size(); i++) {
            if (topResult.size() == 0) {
                topResult.add(sentences.get(i));
            } else {
                input = input.toLowerCase();
                String lowerCasedSentence = sentences.get(i).toLowerCase();
                double similarity = Fuzzy.similarity(input, lowerCasedSentence);
                for (int j = topResult.size() - 1; j > -1; j--) {
                    //note: the closer to 0, the more similar
                    double topResultSimilarity = Fuzzy.similarity(input, topResult.get(j));
                    boolean isMoreSimilar = similarity < topResultSimilarity;
                    if (isMoreSimilar && j == 0) {
                        topResult.insertElementAt(sentences.get(i), j);
                        break;
                    } else if (!isMoreSimilar && j < 4) {
                        topResult.insertElementAt(sentences.get(i), j + 1);
                        break;
                    }
                }
            }
            if (topResult.size() > 5) {
                topResult.removeElementAt(4);
            }
        }
        return topResult;
    }

    private void resetSpeechRecognizer() {
        speechRecognizer.stopListen();
        speechRecognizer.startListen();
    }

    private void resetTimer() {
        this.resetTimer.cancel();
        this.resetTimerTask = new TimerTask() {
            @Override
            public void run() {
                speechRecognizer.stopListen();
                HashMap<String, String> text2SpeechParas = new HashMap<>();
                text2SpeechParas.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, translatedResult);
                textToSpeech.speak(translatedResult, TextToSpeech.QUEUE_FLUSH, text2SpeechParas);
            }
        };
        this.resetTimer = new Timer();
        this.resetTimer.schedule(resetTimerTask, Configurations.UX_resetTime);
    }

    private String getTranslation(Vector<String> inputs) {
        if (inputs == null || inputs.size() == 0) {
            return "";
        }
        String result;
        String destinationLanguage = this.appModel.getDestinationLanguage();
        return appModel.getTranslation(inputs.firstElement());
    }

}
