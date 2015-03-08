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
    public static final String Server_address = "172.24.212.144"; //IP of local machine is "http://172.24.212.144/"
    public static final int Server_port = 50050;
}
