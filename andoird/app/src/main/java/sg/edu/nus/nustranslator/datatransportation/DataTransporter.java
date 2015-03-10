package sg.edu.nus.nustranslator.datatransportation;

import android.content.Context;
import android.media.AudioRecord;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Storm on 3/6/2015.
 */
public class DataTransporter {
    //Attributes
    AudioStreamer audioStreamer = new AudioStreamer();

    //Constructor
    public DataTransporter() {
    }

    //Public Methods
    public void startAudioStream(AudioRecord recorder, Context context) {
        //Check Network Status
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            audioStreamer.startStream(recorder);
        } else {
            // display error
        }
    }

    public void stopStream() {
        audioStreamer.stopStream();
    }
}
