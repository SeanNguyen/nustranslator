package sg.edu.nus.nustranslator.business;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

/**
 * Created by Storm on 3/10/2015.
 */
class LocalSpeechRecognizer implements ISpeechRecognizer, RecognitionListener {

    private SpeechRecognizer recognizer;

    public LocalSpeechRecognizer() {
        
    }

    @Override
    public void startListen() {

    }

    @Override
    public void stopListen() {

    }

    //
    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
