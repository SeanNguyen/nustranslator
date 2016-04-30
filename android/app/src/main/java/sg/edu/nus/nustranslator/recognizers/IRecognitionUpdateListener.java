package sg.edu.nus.nustranslator.recognizers;

public interface IRecognitionUpdateListener {
    void onRecognitionResult(String wordsDetected, String state);
}
