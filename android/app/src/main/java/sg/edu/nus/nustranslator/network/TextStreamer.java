package sg.edu.nus.nustranslator.network;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import sg.edu.nus.nustranslator.Configurations;


public class TextStreamer implements Streamer {

    //Attributes
    private Boolean streaming;
    private Socket socket;

    //Constructor
    public TextStreamer() {
        this.streaming = false;
    }

    @Override
    public void startStream(final String fileName) {
        this.streaming = true;
        try {
            this.socket = IO.socket("http://" + Configurations.Server_address + ":" + Configurations.Server_port);
            this.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socket.emit("dataType", Configurations.Stream_dataType_text);
                    socket.emit("fileName", fileName);
                }
            });
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopStream() {
        this.streaming = false;
        this.socket.disconnect();
    }

    @Override
    public boolean sendData(Object data) {
        if (this.streaming) {
            this.socket.emit("data", data);
            return true;
        } else {
            return false;
        }
    }
}
