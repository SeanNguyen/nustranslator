package sg.edu.nus.nustranslator.data;

import android.media.AudioRecord;

import java.io.DataOutputStream;
import java.net.Socket;

import sg.edu.nus.nustranslator.Configurations;

/**
 * Created by Storm on 3/6/2015.
 */
public class AudioStreamer {

    //Attributes
    private Boolean streaming;

    //Constructor
    public AudioStreamer() {
        this.streaming = false;
    }

    //Public Methods
    public void startStream(AudioRecord recorder) {
        this.streaming = true;
        try {
            @SuppressWarnings("resource")
            Socket socket = new Socket(Configurations.Server_address, Configurations.Server_port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            byte[] buffer = new byte[Configurations.Recorder_minBuffSize];
            while (this.streaming) {
                System.out.println(recorder.read(buffer, 0, buffer.length));
                out.writeInt(buffer.length);
                out.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopStream() {
        this.streaming = false;
    }
}
