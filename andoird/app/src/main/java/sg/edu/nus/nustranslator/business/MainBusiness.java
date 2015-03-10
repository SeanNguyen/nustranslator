package sg.edu.nus.nustranslator.business;

import net.java.frej.fuzzy.Fuzzy;

import java.util.Vector;

import sg.edu.nus.nustranslator.model.AppModel;
import sg.edu.nus.nustranslator.model.States;
import sg.edu.nus.nustranslator.presentation.MainActivity;

/**
 * Created by Storm on 3/5/2015.
 */
public class MainBusiness {

    //attributes
    AppModel appModel = new AppModel();
    ISpeechRecognizer speechRecognizer;
    MainActivity mainActivity;

    //constructor
    public MainBusiness(MainActivity context) {
        this.speechRecognizer = new LocalSpeechRecognizer(context, this);
        this.mainActivity = context;
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
        Vector<String> topResults = getTopResults(input);
        mainActivity.updateSpeechRecognitionResult(topResults);
    }

    //Private Helper Methods
    private Vector<String> getTopResults(String input) {
        String originalLanguage = appModel.getOriginalLanguage();
        Vector<String> sentences = appModel.getSentencesOfLanguage(originalLanguage);
        Vector<String> topResult = new Vector<String>();
        for (int i = 0; i < sentences.size(); i++) {
            double similarity = Fuzzy.similarity(input, sentences.get(i));
            for (int j = topResult.size() - 1; j > -1 ; j--) {
                if (similarity < Fuzzy.similarity(input, topResult.get(j))) {
                    if (j < 4) {
                        topResult.insertElementAt(sentences.get(i), j);
                    }
                    break;
                }
            }
            if (topResult.size() > 5) {
                topResult.removeElementAt(4);
            }
        }
        return topResult;
    }
}
