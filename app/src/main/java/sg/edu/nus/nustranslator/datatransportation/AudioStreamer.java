package sg.edu.nus.nustranslator.datatransportation;

import android.media.AudioRecord;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
    public void startStream(final AudioRecord recorder) {

        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                streaming = true;
                int minBufSize = AudioRecord.getMinBufferSize(Configurations.Recorder_sampleRate,
                        Configurations.Recorder_channelConfig,
                        Configurations.Recorder_audioFormat);
                byte[] buffer = new byte[minBufSize];
                final InetAddress destination;
                try {
                    destination = InetAddress.getByName(Configurations.Server_address);
                    DatagramSocket socket = new DatagramSocket();
                    while (streaming == true) {
                        //reading data from MIC into buffer
                        recorder.read(buffer, 0, buffer.length);
                        //putting buffer in the packet
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, destination, Configurations.Server_port);
                        socket.send(packet);
                    }
                } catch (UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        streamThread.start();
    }

    public void stopStream() {
        this.streaming = false;
    }
}
