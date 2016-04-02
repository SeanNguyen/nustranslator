package sg.edu.nus.nustranslator.net;

import android.util.Base64;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import sg.edu.nus.nustranslator.ultis.Configurations;

/**
 * Created by Storm on 3/6/2015.
 */
public class AudioStreamer implements Streamer{

    //Attributes
    private Boolean streaming;
    private Socket socket;

    //Constructor
    public AudioStreamer() {
        this.streaming = false;
    }

    //Public Methods
    public void startStream(final String fileName) {
        try {
            this.socket = IO.socket("http://" + Configurations.Server_address + ":" + Configurations.Server_port);
            this.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socket.emit("dataType", Configurations.Stream_dataType_audio);
                    socket.emit("fileName", fileName);
                    streaming = true;
                }
            });
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopStream() {
        this.streaming = false;
        this.socket.disconnect();
    }

    @Override
    public boolean sendData(Object data) {
        byte[] base64Data = Base64.encode((byte[]) data, Base64.DEFAULT);
        if (streaming) {
            socket.emit("data", base64Data);
            return true;
        }
        return false;
    }
}
