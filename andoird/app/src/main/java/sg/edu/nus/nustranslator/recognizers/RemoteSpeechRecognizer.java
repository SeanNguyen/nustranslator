package sg.edu.nus.nustranslator.recognizers;

import android.content.Context;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import sg.edu.nus.nustranslator.ultis.Configurations;
import sg.edu.nus.nustranslator.data.DataController;

/**
 * Created by Storm on 3/10/2015.
 */
public class RemoteSpeechRecognizer{

    AudioRecord recorder;
    DataController dataController = new DataController();
    Context context;

    public RemoteSpeechRecognizer(Context context) {
        this.context = context;
    }

    public void startListen() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                Configurations.Recorder_sampleRate,
                Configurations.Recorder_channelConfig,
                Configurations.Recorder_audioFormat,
                Configurations.Recorder_minBuffSize * 10);
        this.recorder.startRecording();
        this.dataController.startAudioStream(recorder, this.context);
    }

    public void stopListen() {
        this.recorder.stop();
        this.recorder.release();
        this.dataController.stopStream();
    }
}
