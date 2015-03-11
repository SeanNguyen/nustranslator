package sg.edu.nus.nustranslator.data;

import android.content.Context;
import android.media.AudioRecord;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import sg.edu.nus.nustranslator.Configurations;
import sg.edu.nus.nustranslator.model.AppModel;

/**
 * Created by Storm on 3/6/2015.
 */
public class DataController {
    //Attributes
    AudioStreamer audioStreamer = new AudioStreamer();
    DataFetcher dataFetcher = new DataFetcher();

    //Constructor
    public DataController() {
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

    public void serializeData(AppModel model, Context context) {
        //the format will be:
        //data version
        //number of language
        //number of sentence in each language
        //Language name
        //sentences
        String fileName = Configurations.Data_fileName;
        int noOfLanguage = model.getNumberOfLanguage();
        int noOfPair = model.getNumberOfPair();
        Vector<String> languages = model.getAllLanguages();
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(model.getDataVersion());
            outputStream.write(Configurations.Newline.getBytes());
            outputStream.write(noOfLanguage);
            outputStream.write(Configurations.Newline.getBytes());
            outputStream.write(noOfPair);
            outputStream.write(Configurations.Newline.getBytes());
            for (int i = 0; i < noOfLanguage; i++) {
                String language = languages.get(i);
                outputStream.write(language.getBytes());
                outputStream.write(Configurations.Newline.getBytes());
                Vector<String> sentences = model.getSentencesOfLanguage(language);
                for (int j = 0; j < noOfPair; j++) {
                    outputStream.write(sentences.get(j).getBytes());
                    outputStream.write(Configurations.Newline.getBytes());
                }
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserializeData(AppModel model, Context context) {
        //the format will be:
        //data version
        //number of language
        //number of sentence in each language
        //Language name
        //sentences
        String fileName = Configurations.Data_fileName;
        Vector<String> languages = model.getAllLanguages();
        try {
            Scanner scanner = new Scanner(context.openFileInput(fileName));
            int dataVersion = Integer.parseInt(scanner.nextLine());
            model.setDataVersion(dataVersion);

            int noOfLanguage = Integer.parseInt(scanner.nextLine());
            int noOfPair = Integer.parseInt(scanner.nextLine());
            model.setNumberOfPair(noOfPair);

            for (int i = 0; i <noOfLanguage; i++) {
                String language = scanner.nextLine();
                Vector<String> sentences = new Vector<String>();
                for (int j = 0; j < noOfPair; j++) {
                    String sentence = scanner.nextLine();
                    sentences.add(sentence);
                }
                model.addLanguage(language, sentences);
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateData(AppModel model, Context context) {
        dataFetcher.fetchData(model);
        serializeData(model, context);
    }
}
