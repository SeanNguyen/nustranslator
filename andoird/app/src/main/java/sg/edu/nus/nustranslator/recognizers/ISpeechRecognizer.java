package sg.edu.nus.nustranslator.recognizers;

import android.content.Context;

/**
 * Created by Storm on 3/10/2015.
 */
public interface ISpeechRecognizer {

    void setInputLanguage(String language, Context context);

    void startListen();
    void initListen();

    void stopListen();

    void cancelListen();

}
