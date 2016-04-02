package sg.edu.nus.nustranslator.net;

/**
 * Created by Storm on 4/10/2015.
 */
public interface Streamer {
    public void startStream(String fileName);
    public void stopStream();
    public boolean sendData(Object data);
}
