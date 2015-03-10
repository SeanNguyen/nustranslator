package sg.edu.nus.nustranslator;

import android.media.AudioFormat;

/**
 * Created by Storm on 3/5/2015.
 */
public class Configurations {
    //recorder info
    public static final int Recorder_sampleRate = 8000;
    public static final int Recorder_channelConfig = AudioFormat.CHANNEL_IN_MONO;
    public static final int Recorder_audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    public static final int Recorder_minBuffSize = 10240;

    //server;
    //IP of local machine via NUS network "http://172.24.212.144/"
    //IP of local machine viw Connectify "http://192.168.41.1/"
    public static final String Server_address = "192.168.41.1";
    public static final int Server_port = 50050;

    //Sphinx Config
    public static final String Sphinx_models_dir = "models";
    public static final String Sphinx_languageModel_dir = "lm/languageModel.lm";
    public static final String Sphinx_acousticModel_dir = "hmm/en-us-semi";
    public static final String Sphinx_dictionary_dir = "dict/localLib.dic";
    public static final float Sphinx_keywordThreshold = 1e-20f;

    //Sphinx Keywords
    public static final String Sphinx_keyword_search = "search";

    //Data storage
    public static final String data_fileName = "data.txt";

    //String Manipulation
    public static final String newline = String.format("%n");
}
