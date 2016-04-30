package sg.edu.nus.nustranslator.ui;

public interface IRecognitionUpdateListener {
    void onRecognitionResult(String wordsDetected, String state);
}
