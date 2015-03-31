package sg.edu.nus.nustranslator.recognizers;

/**
 * Created by Storm on 3/10/2015.
 */
public interface ISpeechRecognizer {

    void setInputLanguage(String language);

    void startListen();

    void stopListen();

}
