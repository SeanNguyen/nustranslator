package sg.edu.nus.nustranslator.business;

import android.content.Context;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import sg.edu.nus.nustranslator.Configurations;
import sg.edu.nus.nustranslator.datatransportation.DataTransporter;

/**
 * Created by Storm on 3/10/2015.
 */
class RemoteSpeechRecognizer implements ISpeechRecognizer {

    AudioRecord recorder;
    DataTransporter dataTransporter = new DataTransporter();
    Context context;

    public RemoteSpeechRecognizer(Context context) {
        this.context = context;
    }

    @Override
    public void startListen() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                Configurations.Recorder_sampleRate,
                Configurations.Recorder_channelConfig,
                Configurations.Recorder_audioFormat,
                Configurations.Recorder_minBuffSize * 10);
        this.recorder.startRecording();
        this.dataTransporter.startAudioStream(recorder, this.context);
    }

    @Override
    public void stopListen() {
        this.recorder.stop();
        this.recorder.release();
        this.dataTransporter.stopStream();
    }
}
