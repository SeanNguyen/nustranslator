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
}
