package sg.edu.nus.nustranslator.business;

import android.util.Log;

import net.java.frej.fuzzy.Fuzzy;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import sg.edu.nus.nustranslator.Configurations;
import sg.edu.nus.nustranslator.data.DataController;
import sg.edu.nus.nustranslator.model.AppModel;
import sg.edu.nus.nustranslator.model.States;
import sg.edu.nus.nustranslator.presentation.MainActivity;

/**
 * Created by Storm on 3/5/2015.
 */
public class MainBusiness {

    //attributes
    private AppModel appModel = new AppModel();
    private ISpeechRecognizer speechRecognizer;
    private DataController dataController = new DataController();
    private MainActivity mainActivity;

    private String lastRecognitionUpdate;
    private Timer resetTimer = new Timer();
    private TimerTask resetTimerTask;

    //constructor
    public MainBusiness(MainActivity context) {
        this.mainActivity = context;
        this.speechRecognizer = new LocalSpeechRecognizer(context, this);
        //this.dataController.serializeData(appModel, context);
        //this.dataController.deserializeData(appModel, context);
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
            Log.e("IGNORED", input);
            return;
        }
        Log.e("TIMER", input);
        resetTimer();
        Vector<String> topResults = getTopResults(input);
        mainActivity.updateSpeechRecognitionResult(topResults);
        this.lastRecognitionUpdate = input;
    }

    public void updateData() {
        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dataController.updateData(appModel, mainActivity);
            }
        });
        updateThread.start();
    }

    //Private Helper Methods
    private Vector<String> getTopResults(String input) {
        String originalLanguage = appModel.getOriginalLanguage();
        Vector<String> sentences = appModel.getSentencesOfLanguage(originalLanguage);
        Vector<String> topResult = new Vector<String>();
        if (sentences == null) {
            return topResult;
        }
        for (int i = 0; i < sentences.size(); i++) {
            String lowerCasedSentence = sentences.get(i).toLowerCase();
            double similarity = Fuzzy.similarity(input, lowerCasedSentence);
            for (int j = topResult.size() - 1; j > -1 ; j--) {
                //get the one which closer to 1
                //double compare = Math.abs(similarity - 1) - Math.abs(Fuzzy.similarity(input, topResult.get(j)) - 1);
                if (similarity < Fuzzy.similarity(input, topResult.get(j))) {
                    if (j < 4) {
                        topResult.insertElementAt(sentences.get(i), j);
                    }
                    break;
                }
            }
            if (topResult.size() == 0) {
                topResult.add(sentences.get(i));
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
                resetSpeechRecognizer();
            }
        };
        this.resetTimer = new Timer();
        this.resetTimer.schedule(resetTimerTask, Configurations.UX_resetTime);
    }
}
