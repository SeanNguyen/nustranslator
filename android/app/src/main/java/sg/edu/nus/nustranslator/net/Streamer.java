package sg.edu.nus.nustranslator.net;


public interface Streamer {
    public void startStream(String fileName);
    public void stopStream();
    public boolean sendData(Object data);
}
