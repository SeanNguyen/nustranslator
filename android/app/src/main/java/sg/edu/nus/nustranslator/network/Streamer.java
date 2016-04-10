package sg.edu.nus.nustranslator.network;


public interface Streamer {
    public void startStream(String fileName);
    public void stopStream();
    public boolean sendData(Object data);
}
