package sg.edu.nus.nustranslator.recognizers;

import android.content.Context;


public interface ISpeechRecognizer {

    void setInputLanguage(String language, Context context);

    void startListen();
    void stopListen();
    void cancelListen();
    void reset();

}
