package sg.edu.nus.nustranslator.business;

import android.content.Context;

import sg.edu.nus.nustranslator.model.AppModel;
import sg.edu.nus.nustranslator.model.States;

/**
 * Created by Storm on 3/5/2015.
 */
public class MainBusiness {

    //attributes
    AppModel appModel = new AppModel();
    ISpeechRecognizer speechRecognizer;

    //constructor
    public MainBusiness(Context context) {
        this.speechRecognizer = new LocalSpeechRecognizer(context, this);
    }

    //Public methods
    public States changeState() {
        States state = appModel.getAppState();
        if (state == States.ACTIVE) {
            appModel.setAppState(States.INACTIVE);
            this.speechRecognizer.stopListen();
        } else {
            appModel.setAppState(States.ACTIVE);
            Thread dataThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    speechRecognizer.startListen();
                }
            });
            dataThread.start();
        }
        return appModel.getAppState();
    }

    public void onSpeechRecognitionResultUpdate(String input) {

    }

    //Private Helper Methods
}
