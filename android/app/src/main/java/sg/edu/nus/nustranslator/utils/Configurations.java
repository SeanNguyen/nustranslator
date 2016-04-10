package sg.edu.nus.nustranslator.utils;

import android.media.AudioFormat;


public class Configurations {
    //recorder info
    public static final int Recorder_sampleRate = 8000;
    public static final int Recorder_channelConfig = AudioFormat.CHANNEL_IN_MONO;
    public static final int Recorder_audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    public static final int Recorder_minBuffSize = 10240;

    //server;
    //IP of local machine via NUS network "http://172.24.212.144"
    //IP of local machine viw Connectify "http://192.168.41.1"
    public static final String Server_address = "192.168.41.1";
    public static final int Server_port = 9000;

    //streaming protocol
    public static final String Stream_dataType_audio = "audio";
    public static final String Stream_dataType_text = "text";

    //Sphinx Config
    public static final String Sphinx_models_dir = "models";
    public static final String Sphinx_acousticModel_dir = "hmm/";
//    public static final float Sphinx_keywordThreshold = 1e-20f;
    public static final float Sphinx_keywordThreshold = 1e-1f;

    //Sphinx Keywords
    public static final String SPHINX_ACTIVATED = "search";
    public static final String SPHINX_NOT_ACTIVATED = "triggerstart";
    public static final String Sphinx_keyword_trigger_end = "triggerend";

    //Data storage
    public static final String Data_fileName_dir = "data/";
    public static final String Data_fileName_sentences = "data.txt";
    public static final String Data_fileName_dict_ext = ".dic";
    public static final String Data_fileName_languageModel_ext = ".lm";

    //String Manipulation
    public static final String Newline = String.format("%n");
    public static final String Empty = "";

    //UX
    public static final long UX_resetTime = 300;

    //available languages
    public static final String LANGUAGE_ENGLISH = "english";
    public static final String LANGUAGE_MANDARIN = "mandarin";
}
