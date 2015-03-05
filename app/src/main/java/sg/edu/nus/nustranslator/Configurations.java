package sg.edu.nus.nustranslator;

import android.media.AudioFormat;
import android.media.AudioRecord;

/**
 * Created by Storm on 3/5/2015.
 */
public class Configurations {
    //recorder info
    public static final int Recorder_sampleRate = 44100;
    public static final int Recorder_channelConfig = AudioFormat.CHANNEL_IN_MONO;
    public static final int Recorder_audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    public static final int Recorder_minBuffSize = AudioRecord.getMinBufferSize(Recorder_sampleRate,
            Recorder_channelConfig, Recorder_audioFormat);

    //server;
    public static final String Server_address = "1.2.3.4";
    public static final int Server_port = 50050;
}
